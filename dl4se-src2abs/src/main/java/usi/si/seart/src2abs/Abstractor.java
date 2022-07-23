package usi.si.seart.src2abs;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class Abstractor {

	@SneakyThrows
	public void abstractCode(
			Parser.Granularity granularity, Path inputCodePath, Path outputCodePath, Path idiomsFilePath
	) {
		//Check inputs
		String mapFileName = outputCodePath.getFileName() + ".map";
		Path mapOutputFile = outputCodePath.resolveSibling(mapFileName);

		//Idioms
		Set<String> idioms;
		@Cleanup Stream<String> stream = Files.lines(idiomsFilePath);
		idioms = stream.collect(Collectors.toSet());

		//Parser
		Parser parser = new Parser(granularity);
		parser.parseFile(inputCodePath);

		//Tokenizer
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.setTypes(parser.getTypes());
		tokenizer.setMethods(parser.getMethods());
		tokenizer.setAnnotations(parser.getAnnotations());
		tokenizer.setIdioms(idioms);

		String abstractCode = tokenizer.tokenize(inputCodePath);

		//Write output files
		Files.write(outputCodePath, abstractCode.getBytes());
		tokenizer.exportMaps(mapOutputFile);
	}
}
