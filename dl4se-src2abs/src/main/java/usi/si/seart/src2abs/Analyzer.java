package usi.si.seart.src2abs;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {

	@SneakyThrows
	public static String readSourceCode(Path filePath) {
		return new String(Files.readAllBytes(filePath));
	}

	public static String removeCommentsAndAnnotations(String sourceCode) {
		Pattern pattern = Pattern.compile("(\".+\")");
		Matcher matcher = pattern.matcher(sourceCode);

		// Save Strings with '//' and '@'
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

		// Remove Comments
		sourceCode = sourceCode.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
		// Remove Annotations
		//sourceCode = sourceCode.replaceAll("@.+", "");

		return sourceCode;
	}

}
