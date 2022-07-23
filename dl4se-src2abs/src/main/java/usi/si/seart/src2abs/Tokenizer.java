package usi.si.seart.src2abs;

import edu.wm.cs.compiler.tools.generators.scanners.JavaLexer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Tokenizer {

	private static final String ERROR_LEXER = "<ERROR>";
	private static final String SPACED_DOT = " . ";

	Map<String, String> stringLiterals = new HashMap<>();
	Map<String, String> charLiterals = new HashMap<>();
	Map<String, String> intLiterals = new HashMap<>();
	Map<String, String> floatLiterals = new HashMap<>();
	Map<String, String> typeMap = new HashMap<>();
	Map<String, String> methodMap = new HashMap<>();
	Map<String, String> annotationMap = new HashMap<>();
	Map<String, String> varMap = new HashMap<>();

	Set<String> types;
	Set<String> methods;
	Set<String> annotations;
	Set<String> idioms;

	public Tokenizer(Parser parser, Set<String> idioms) {
		this(parser.getTypes(), parser.getMethods(), parser.getAnnotations(), idioms);
	}

	public String tokenize(Path filePath) {
		List<Token> tokens;
		try {
			tokens = readTokens(filePath);
		} catch (StackOverflowError e){
			System.err.println("STACKOVERFLOW DURING LEXICAL ANALYSIS!");
			return ERROR_LEXER;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tokens.size(); i++) {
			String token = "";
			Token t = tokens.get(i);

			//Handling annotations
			if (t.getType() == JavaLexer.AT){
				int j = i + 1;
				Token nextToken = tokens.get(j);

				if (nextToken.getType() == JavaLexer.Identifier && annotations.contains(nextToken.getText())) {
					//This is an annotation
					token = getAnnotationID(nextToken.getText());
					i = j;
				}

			} else if (t.getType() == JavaLexer.Identifier) {
				String tokenName = t.getText();
				int j = i + 1;

				boolean expectDOT = true;
				while (j < tokens.size()) {
					Token nextToken = tokens.get(j);
					if (expectDOT) {
						if (nextToken.getType() == JavaLexer.DOT) {
							tokenName += nextToken.getText();
							expectDOT = false;
						} else {
							i = j - 1;
							break;
						}
					} else {
						if ((nextToken.getType() == JavaLexer.Identifier || nextToken.getType() == JavaLexer.THIS
								|| nextToken.getType() == JavaLexer.CLASS || nextToken.getType() == JavaLexer.NEW) &&
								tokens.get(j-1).getType() == JavaLexer.DOT) {
							tokenName += nextToken.getText();
						} else {
							i = j-1;
							break;
						}
					}
					j++;
				}


				token = analyzeIdentifier(tokenName, tokens, i);
			} else if (t.getType() == JavaLexer.CharacterLiteral) {
				token = getCharId(t);
			} else if (t.getType() == JavaLexer.FloatingPointLiteral) {
				token = getFloatId(t);
			} else if (t.getType() == JavaLexer.IntegerLiteral) {
				token = getIntId(t);
			} else if (t.getType() == JavaLexer.StringLiteral) {
				token = getStringId(t);
			} else {
				token = t.getText();
			}

			sb.append(token).append(" ");
		}

		return sb.toString().trim();
	}

	private static List<Token> readTokens(Path filePath) {
		String sourceCode = Analyzer.readSourceCode(filePath);
		return readTokens(sourceCode);
	}

	@SneakyThrows
	private static List<Token> readTokens(String sourceCode) {
		String cleanedCode = Analyzer.removeCommentsAndAnnotations(sourceCode);
		InputStream inputStream = new ByteArrayInputStream(cleanedCode.getBytes(StandardCharsets.UTF_8));
		JavaLexer jLexer = new JavaLexer(new ANTLRInputStream(inputStream));

		List<Token> tokens = new ArrayList<>();
		for (Token t = jLexer.nextToken(); t.getType() != Token.EOF; t = jLexer.nextToken()) {
			tokens.add(t);
		}

		return tokens;
	}

	@SneakyThrows
	public void exportMaps(Path outFile) {
		List<String> lines = new ArrayList<>();

		//lines.addAll(getKeysAndValues(identifiers));
		lines.addAll(getKeysAndValues(typeMap));
		lines.addAll(getKeysAndValues(methodMap));
		lines.addAll(getKeysAndValues(varMap));
		lines.addAll(getKeysAndValues(annotationMap));
		lines.addAll(getKeysAndValues(charLiterals));
		lines.addAll(getKeysAndValues(floatLiterals));
		lines.addAll(getKeysAndValues(intLiterals));
		lines.addAll(getKeysAndValues(stringLiterals));

		Files.write(outFile, lines);
	}

	private List<String> getKeysAndValues(Map<String, String> map){
		Collector<CharSequence, ?, String> commaCollector = Collectors.joining(",");
		String keys = map.keySet().stream().collect(commaCollector);
		String values = map.values().stream().collect(commaCollector);
		return List.of(keys, values);
	}

	private String analyzeIdentifier(String token, List<Token> tokens, int i) {
		if (idioms.contains(token)) return token;

		String[] tokenParts = token.split("\\.");

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (idioms.contains(lastPart)) {
				if (idioms.contains(firstPart)) {
					// idiom . idiom
					return firstPart + SPACED_DOT + lastPart;
				} else if (types.contains(firstPart)) {
					// type_# . idiom
					return getTypeId(firstPart)	+ SPACED_DOT + lastPart;
				} else {
					// var_# . idiom
					return getVarId(firstPart) + SPACED_DOT + lastPart;
				}
			} else if (idioms.contains(firstPart)){
				if (types.contains(lastPart)) {
					// idiom . type_#
					return firstPart + SPACED_DOT + getTypeId(lastPart);
				} else {
					// idiom . var_#
					return firstPart + SPACED_DOT + getVarId(lastPart);
				}
			}
		}

		if (types.contains(token)) {
			// type_#
			return getTypeId(token);
		}

		//Check if it could be a method (the next token is a parenthesis)
		boolean couldBeMethod = false;
		if (i+1 < tokens.size()) {
			Token t = tokens.get(i+1);
			if (t.getType() == JavaLexer.LPAREN) {
				couldBeMethod = true;
			}
		}
		//MethodReference check (Type : : Method)
		if (i > 2) {
			Token t1 = tokens.get(i - 1);
			Token t2 = tokens.get(i - 2);

			if (t1.getType() == JavaLexer.COLON && t2.getType() == JavaLexer.COLON) {
				couldBeMethod = true;
			}
		}

		if (methods.contains(token) && couldBeMethod) {
			// method_#
			return getMethodId(token);
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (methods.contains(lastPart) && couldBeMethod) {
				if (idioms.contains(firstPart)) {
					// idiom . method_#
					return firstPart + SPACED_DOT + getMethodId(lastPart);
				} else if (types.contains(firstPart)) {
					// type . method_#
					return getTypeId(firstPart)	+ SPACED_DOT + getMethodId(lastPart);
				} else {
					// var_# . method_#
					return getVarId(firstPart) + SPACED_DOT + getMethodId(lastPart);
				}
			}
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (varMap.containsKey(lastPart)){
				if (idioms.contains(firstPart)) {
					// idiom . var_#
					return firstPart + SPACED_DOT + getVarId(lastPart);
				} else if (types.contains(firstPart)) {
					// type . var_#
					return getTypeId(firstPart)	+ SPACED_DOT + getVarId(lastPart);
				} else {
					// var_# . var_#
					return getVarId(firstPart) + SPACED_DOT + getVarId(lastPart);
				}
			}
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (types.contains(firstPart)){
				if (idioms.contains(lastPart) || lastPart.equals("this") || lastPart.equals("class")) {
					// type_# . idiom
					return getTypeId(firstPart) + SPACED_DOT + lastPart;
				} else {
					// type_# . var_#
					return getTypeId(firstPart) + SPACED_DOT + getVarId(lastPart);
				}
			} else if (varMap.containsKey(firstPart)){
				if (idioms.contains(lastPart)) {
					// var_# . idiom
					return getVarId(firstPart) + SPACED_DOT + lastPart;
				} else if (lastPart.equals("new")){
					return getVarId(firstPart) + SPACED_DOT + lastPart;
				} else{
					// var_# . var_#
					return getVarId(firstPart) + SPACED_DOT + getVarId(lastPart);
				}
			}
		}

		// var_# . var_#
		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (lastPart.equals("this") || lastPart.equals("class")){
				if (idioms.contains(firstPart)){
					return firstPart + SPACED_DOT + lastPart;
				} else {
					return getVarId(firstPart) + SPACED_DOT + lastPart;
				}
			}

			if (idioms.contains(firstPart) && idioms.contains(lastPart)){
				return firstPart + SPACED_DOT + lastPart;
			} else if (idioms.contains(firstPart)){
				return firstPart + SPACED_DOT + getVarId(lastPart);
			} else if (idioms.contains(lastPart)){
				return getVarId(firstPart) + SPACED_DOT + lastPart;
			}

			return getVarId(firstPart) + SPACED_DOT + getVarId(lastPart);
		}

		// var_#
		return getVarId(token);
	}

	//------------------ IDs ----------------------

	Function<String, String> getTypeId = _idGetter(typeMap, "TYPE_");
	private String getTypeId(String token) {
		return getTypeId.apply(token);
	}

	Function<String, String> getVarId = _idGetter(varMap, "VAR_");
	private String getVarId(String token) {
		return getVarId.apply(token);
	}

	Function<String, String> getMethodId = _idGetter(methodMap, "METHOD_");
	private String getMethodId(String token) {
		return getMethodId.apply(token);
	}
	
	private Function<String, String> _idGetter(
			Map<String, String> literals, String prefix
	) {
		AtomicInteger counter = new AtomicInteger();
		return (text) -> {
			if (literals.containsKey(text)) {
				return literals.get(text);
			} else {
				String id = prefix + counter.incrementAndGet();
				literals.put(text, id);
				return id;
			}
		};
	}

	AtomicInteger annotationsCounter = new AtomicInteger();
	private String getAnnotationID(String token) {
		if (idioms.contains("@" + token)) {
			return "@" + token;
		} else if (annotationMap.containsKey(token)) {
			return annotationMap.get(token);
		} else {
			String id = "ANNOTATION_" + annotationsCounter.incrementAndGet();
			annotationMap.put(token, id);
			return id;
		}
	}

	//------------------ LITERALS ----------------------

	Function<Token, String> getCharId = _literalIdGetter(charLiterals, "CHAR_");
	private String getCharId(Token token) {
		return getCharId.apply(token);
	}

	Function<Token, String> getFloatId = _literalIdGetter(floatLiterals, "FLOAT_");
	private String getFloatId(Token token) {
		return getFloatId.apply(token);
	}

	Function<Token, String> getIntId = _literalIdGetter(intLiterals, "INT_");
	private String getIntId(Token token) {
		return getIntId.apply(token);
	}

	Function<Token, String> getStringId = _literalIdGetter(stringLiterals, "STRING_");
	private String getStringId(Token token) {
		return getStringId.apply(token);
	}

	private Function<Token, String> _literalIdGetter(
			Map<String, String> literals, String prefix
	) {
		AtomicInteger counter = new AtomicInteger();
		return (token) -> {
			String text = token.getText();
			if (idioms.contains(text)) {
				return text;
			} else if (literals.containsKey(text)) {
				return literals.get(text);
			} else {
				String id = prefix + counter.incrementAndGet();
				literals.put(text, id);
				return id;
			}
		};
	}
}
