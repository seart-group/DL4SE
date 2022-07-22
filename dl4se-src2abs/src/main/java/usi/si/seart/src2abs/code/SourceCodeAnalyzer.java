package usi.si.seart.src2abs.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCodeAnalyzer {



	public static String readSourceCode(String filePath) {
		String sourceCode = "";
		try {
			sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sourceCode;	
	}


	public static String removeCommentsAndAnnotations(String sourceCode) {
		String regex = "(\".+\")";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sourceCode);

		//save string with '//' and '@' inside
		String group = "";
		String okGroup = "";
		while (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				group = matcher.group(i);
				//okGroup = group.replaceAll("@", "<AT>");
				okGroup = group.replaceAll("//", "<DOUBLE_SLASH>");
				sourceCode = sourceCode.replace(group, okGroup);
			}
		}

		//remove comments
		sourceCode = sourceCode.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
		//remove annotations
		//sourceCode = sourceCode.replaceAll("@.+", "");

		return sourceCode;
	}

}
