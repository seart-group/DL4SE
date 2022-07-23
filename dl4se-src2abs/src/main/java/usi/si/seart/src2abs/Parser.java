package usi.si.seart.src2abs;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Parser {

	public enum CodeGranularity { METHOD, CLASS }

	Set<String> types = new HashSet<>();
	Set<String> methods = new HashSet<>();
	Set<String> annotations = new HashSet<>();
	CodeGranularity granularity;

	public void parseFile(String filePath) {
		String sourceCode = Analyzer.readSourceCode(filePath);
		sourceCode = Analyzer.removeCommentsAndAnnotations(sourceCode);
		_parse(sourceCode);
	}

	public void parseCode(String sourceCode) {
		sourceCode = Analyzer.removeCommentsAndAnnotations(sourceCode);
		_parse(sourceCode);
	}

	public void _parse(String code) {
		if (granularity == CodeGranularity.METHOD) {
			code = "public class DummyClass {" + code + "}";
		}

		// create compilation unit
		CompilationUnit cu = StaticJavaParser.parse(code);

		// create set of annotations
		cu.findAll(AnnotationExpr.class).stream()
				.map(AnnotationExpr::getNameAsString)
				.forEach(annotations::add);

		// create set of types
		cu.findAll(Type.class).stream()
				.flatMap(type -> {
					String[] allTypes = filterString(type.asString());
					return Stream.of(allTypes);
				})
				.forEach(types::add);

		// create set of methods and insert declared methods
		cu.findAll(MethodDeclaration.class).stream()
				.map(MethodDeclaration::getNameAsString)
				.forEach(methods::add);

		// insert referenced methods into methods set
		cu.findAll(MethodCallExpr.class).stream()
				.map(MethodCallExpr::getNameAsString)
				.forEach(methods::add);

		// insert scope of methods into types
		cu.findAll(MethodCallExpr.class).stream()
				.map(MethodCallExpr::getScope)
				.flatMap(Optional::stream)
				.filter(expression -> expression.isFieldAccessExpr() || expression.isNameExpr())
				.forEach(expression -> {
					String exprStr = expression.toString();
					String[] fragments = exprStr.split("\\.");
					String[] letters = fragments[fragments.length - 1].split("");
					if (!letters[0].equals(letters[0].toLowerCase())) {
						String[] allTypes = filterString(exprStr);
						types.addAll(Arrays.asList(allTypes));
					}
				});

		// insert scope of methods into types
		cu.findAll(MethodReferenceExpr.class).stream()
				.map(MethodReferenceExpr::getIdentifier)
				.forEach(methods::add);
	}

	public String[] filterString(String typeString){
		String[] listString;
		typeString = typeString.replaceAll("\\[", " ");
		typeString = typeString.replaceAll("\\]", " ");
		typeString = typeString.replaceAll("\\(", " ");
		typeString = typeString.replaceAll("\\)", " ");
		typeString = typeString.replaceAll(">", " ");
		typeString = typeString.replaceAll("<", " ");
		typeString = typeString.replaceAll("\\{", " ");
		typeString = typeString.replaceAll("\\}", " ");
		typeString = typeString.replaceAll("\\,", " ");
		typeString = typeString.replaceAll("\\?", " ");
		typeString = typeString.replaceAll("extends", " ");
		typeString = typeString.replaceAll("implements", " ");
		listString = typeString.split(" ");
		return listString;
	}
}
