package usi.si.seart.src2abs;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class Abstractor {

	@SneakyThrows
	public void abstractCode(
			Parser.Granularity granularity, Path inputCodePath, Path outputCodePath, Path idiomsFilePath
	) {
		String sourceCode = new String(Files.readAllBytes(inputCodePath));
		sourceCode = removeCommentsAndAnnotations(sourceCode);

		Set<String> idioms;
		@Cleanup Stream<String> stream = Files.lines(idiomsFilePath);
		idioms = stream.collect(Collectors.toSet());

		Parser parser = new Parser(granularity);
		parser.parseCode(sourceCode);

		Tokenizer tokenizer = new Tokenizer(parser, idioms);
		String abstractCode = tokenizer.tokenize(sourceCode);

		Files.write(outputCodePath, abstractCode.getBytes());

		String mapFileName = outputCodePath.getFileName() + ".map";
		Path mapOutputFile = outputCodePath.resolveSibling(mapFileName);
		tokenizer.export(mapOutputFile);
	}

	public static String removeCommentsAndAnnotations(String sourceCode) {
		Pattern pattern = Pattern.compile("(\".+\")");
		Matcher matcher = pattern.matcher(sourceCode);

		String group;
		String okGroup;
		while (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				group = matcher.group(i);
				//okGroup = group.replaceAll("@", "<AT>");
				okGroup = group.replaceAll("//", "<DOUBLE_SLASH>");
				sourceCode = sourceCode.replace(group, okGroup);
			}
		}

		sourceCode = sourceCode.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
		//sourceCode = sourceCode.replaceAll("@.+", "");

		return sourceCode;
	}
}
