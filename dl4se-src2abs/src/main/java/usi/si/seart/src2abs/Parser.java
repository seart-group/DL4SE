package usi.si.seart.src2abs;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
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
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Parser {

	public enum Granularity {
		METHOD, CLASS
	}

	Set<String> types = new HashSet<>();
	Set<String> methods = new HashSet<>();
	Set<String> annotations = new HashSet<>();

	Granularity granularity;

	public void parse(String sourceCode) {
		Function<String, Node> parser = StaticJavaParser::parse;
		if (granularity == Granularity.METHOD) {
			parser = StaticJavaParser::parseMethodDeclaration;
		}

		// create compilation unit
		Node node = parser.apply(sourceCode);

		// create set of annotations
		node.findAll(AnnotationExpr.class).stream()
				.map(AnnotationExpr::getNameAsString)
				.forEach(annotations::add);

		// create set of types
		node.findAll(Type.class).stream()
				.flatMap(type -> {
					String[] allTypes = filterString(type.asString());
					return Stream.of(allTypes);
				})
				.forEach(types::add);

		// create set of methods and insert declared methods
		node.findAll(MethodDeclaration.class).stream()
				.map(MethodDeclaration::getNameAsString)
				.forEach(methods::add);

		// insert referenced methods into methods set
		node.findAll(MethodCallExpr.class).stream()
				.map(MethodCallExpr::getNameAsString)
				.forEach(methods::add);

		// insert scope of methods into types
		node.findAll(MethodCallExpr.class).stream()
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
		node.findAll(MethodReferenceExpr.class).stream()
				.map(MethodReferenceExpr::getIdentifier)
				.forEach(methods::add);
	}

	private String[] filterString(String typeString){
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
