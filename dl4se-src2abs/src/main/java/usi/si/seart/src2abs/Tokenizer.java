package usi.si.seart.src2abs;

import edu.wm.cs.compiler.tools.generators.scanners.JavaLexer;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tokenizer {

	static final String ERROR_LEXER = "<ERROR>";
	static final String SPACED_DOT = " . ";

	// int count_identifiers = 0;
	int count_types = 0;
	int count_methods = 0;
	int count_annotations = 0;
	int count_vars = 0;
	int count_character = 0;
	int count_floatingpoint = 0;
	int count_integer = 0;
	int count_string = 0;

	// final Map<String, String> identifiers = new HashMap<>();
	final Map<String, String> stringLiterals = new HashMap<>();
	final Map<String, String> charLiterals = new HashMap<>();
	final Map<String, String> intLiterals = new HashMap<>();
	final Map<String, String> floatLiterals = new HashMap<>();

	final Map<String, String> typeMap = new HashMap<>();
	final Map<String, String> methodMap = new HashMap<>();
	final Map<String, String> annotationMap = new HashMap<>();
	final Map<String, String> varMap = new HashMap<>();

	//Parser sets
	@Setter
	Set<String> idioms;
	@Setter
	Set<String> types;
	@Setter
	Set<String> methods;
	@Setter
	Set<String> annotations;

	public String tokenize(String filePath) {
		List<Token> tokens;
		try {
			tokens = readTokens(filePath);
		} catch(StackOverflowError e){
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
				token = getCharacterID(t);
			} else if (t.getType() == JavaLexer.FloatingPointLiteral) {
				token = getFloatingPointID(t);
			} else if (t.getType() == JavaLexer.IntegerLiteral) {
				token = getIntegerID(t);
			} else if (t.getType() == JavaLexer.StringLiteral) {
				token = getStringID(t);
			} else {
				token = t.getText();
			}

			sb.append(token).append(" ");
		}

		return sb.toString().trim();
	}



	private static List<Token> readTokens(String filePath) {
		JavaLexer jLexer = null;
		try {
			//Read source code
			String sourceCode = Analyzer.readSourceCode(filePath);
			//Remove comments and annotations
			String cleanedCode = Analyzer.removeCommentsAndAnnotations(sourceCode);
			InputStream inputStream = new ByteArrayInputStream(cleanedCode.getBytes(StandardCharsets.UTF_8));
			jLexer = new JavaLexer(new ANTLRInputStream(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Extract tokens
		List<Token> tokens = new ArrayList<>();
		for (Token t = jLexer.nextToken(); t.getType() != Token.EOF; t = jLexer.nextToken()) {
			tokens.add(t);
		}

		return tokens;
	}


	public void exportMaps(String outFile) {
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

		try {
			Files.write(Paths.get(outFile), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					return getTypeID(firstPart)	+ SPACED_DOT + lastPart;
				} else {
					// var_# . idiom
					return getVarID(firstPart) + SPACED_DOT + lastPart;
				}
			} else if (idioms.contains(firstPart)){
				if (types.contains(lastPart)) {
					// idiom . type_#
					return firstPart + SPACED_DOT + getTypeID(lastPart);
				} else {
					// idiom . var_#
					return firstPart + SPACED_DOT + getVarID(lastPart);
				}
			}
		}

		if (types.contains(token)) {
			// type_#
			return getTypeID(token);
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
			return getMethodID(token);
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (methods.contains(lastPart) && couldBeMethod) {
				if (idioms.contains(firstPart)) {
					// idiom . method_#
					return firstPart + SPACED_DOT + getMethodID(lastPart);
				} else if (types.contains(firstPart)) {
					// type . method_#
					return getTypeID(firstPart)	+ SPACED_DOT + getMethodID(lastPart);
				} else {
					// var_# . method_#
					return getVarID(firstPart) + SPACED_DOT + getMethodID(lastPart);
				}
			}
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (varMap.containsKey(lastPart)){
				if (idioms.contains(firstPart)) {
					// idiom . var_#
					return firstPart + SPACED_DOT + getVarID(lastPart);
				} else if (types.contains(firstPart)) {
					// type . var_#
					return getTypeID(firstPart)	+ SPACED_DOT + getVarID(lastPart);
				} else {
					// var_# . var_#
					return getVarID(firstPart) + SPACED_DOT + getVarID(lastPart);
				}
			}
		}

		if (tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if (types.contains(firstPart)){
				if (idioms.contains(lastPart) || lastPart.equals("this") || lastPart.equals("class")) {
					// type_# . idiom
					return getTypeID(firstPart) + SPACED_DOT + lastPart;
				} else {
					// type_# . var_#
					return getTypeID(firstPart) + SPACED_DOT + getVarID(lastPart);
				}
			} else if (varMap.containsKey(firstPart)){
				if (idioms.contains(lastPart)) {
					// var_# . idiom
					return getVarID(firstPart) + SPACED_DOT + lastPart;
				} else if (lastPart.equals("new")){
					return getVarID(firstPart) + SPACED_DOT + lastPart;
				} else{
					// var_# . var_#
					return getVarID(firstPart) + SPACED_DOT + getVarID(lastPart);
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
					return getVarID(firstPart) + SPACED_DOT + lastPart;
				}
			}

			if (idioms.contains(firstPart) && idioms.contains(lastPart)){
				return firstPart + SPACED_DOT + lastPart;
			} else if (idioms.contains(firstPart)){
				return firstPart + SPACED_DOT + getVarID(lastPart);
			} else if (idioms.contains(lastPart)){
				return getVarID(firstPart) + SPACED_DOT + lastPart;
			}

			return getVarID(firstPart) + SPACED_DOT + getVarID(lastPart);
		}

		// var_#
		return getVarID(token);
	}

	//------------------ IDs ----------------------

	private String getTypeID(String token) {
		if (typeMap.containsKey(token)) {
			return typeMap.get(token);
		} else {
			count_types += 1;
			String ID = "TYPE_" + count_types;
			typeMap.put(token, ID);
			return ID;
		}
	}

	private String getVarID(String token) {
		if (varMap.containsKey(token)) {
			return varMap.get(token);
		} else {
			count_vars += 1;
			String ID = "VAR_" + count_vars;
			varMap.put(token, ID);
			return ID;
		}
	}

	private String getMethodID(String token) {
		if (methodMap.containsKey(token)) {
			return methodMap.get(token);
		} else {
			count_methods += 1;
			String ID = "METHOD_" + count_methods;
			methodMap.put(token, ID);
			return ID;
		}
	}

	private String getAnnotationID(String token) {
		if (idioms.contains("@" + token)) {
			return "@" + token;
		}
		if (annotationMap.containsKey(token)) {
			return annotationMap.get(token);
		} else {
			count_annotations += 1;
			String ID = "ANNOTATION_" + count_annotations;
			annotationMap.put(token, ID);
			return ID;
		}
	}

	//------------------ LITERALS ----------------------

	private String getCharacterID(Token t) {
		if (idioms.contains(t.getText())) {
			return t.getText();
		} else if (charLiterals.containsKey(t.getText())) {
			return charLiterals.get(t.getText());
		} else {
			count_character += 1;
			String Id = "CHAR_" + count_character;
			charLiterals.put(t.getText(), Id);
			return Id;
		}
	}

	private String getFloatingPointID(Token t) {
		if (idioms.contains(t.getText())) {
			return t.getText();
		} else if (floatLiterals.containsKey(t.getText())) {
			return floatLiterals.get(t.getText());
		} else {
			count_floatingpoint += 1;
			String Id = "FLOAT_" + count_floatingpoint;
			floatLiterals.put(t.getText(), Id);
			return Id;
		}
	}

	private String getIntegerID(Token t) {
		if (idioms.contains(t.getText())) {
			return t.getText();
		} else if (intLiterals.containsKey(t.getText())) {
			return intLiterals.get(t.getText());
		} else {
			count_integer += 1;
			String Id = "INT_" + count_integer;
			intLiterals.put(t.getText(), Id);
			return Id;
		}
	}

	private String getStringID(Token t) {
		if (idioms.contains(t.getText())) {
			return t.getText();
		} else if (stringLiterals.containsKey(t.getText())) {
			return stringLiterals.get(t.getText());
		} else {
			count_string += 1;
			String Id = "STRING_" + count_string;
			stringLiterals.put(t.getText(), Id);
			return Id;
		}
	}
}