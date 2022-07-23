package usi.si.seart.src2abs;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Abstractor {

	@SneakyThrows
	public void abstractCode(
			Parser.Granularity granularity, Path inputCodePath, Path outputCodePath, Path idiomsFilePath
	) {
		//Check inputs
		checkInputs(inputCodePath, idiomsFilePath, outputCodePath);
		String mapFileName = outputCodePath.getFileName() + ".map";
		Path mapOutputFile = outputCodePath.resolveSibling(mapFileName);

		//Idioms
		Set<String> idioms;
		@Cleanup Stream<String> stream = Files.lines(idiomsFilePath);
		idioms = stream.collect(Collectors.toSet());

		//Parser
		Parser parser = new Parser(granularity);
		try {
			parser.parseFile(inputCodePath);
		} catch (StackOverflowError e){
			System.err.println("StackOverflow during parsing!");
		} catch (Exception e) {
			System.err.println("Parsing ERROR!");
			@Cleanup PrintWriter pw = new PrintWriter(outputCodePath.toFile());
			pw.println("<ERROR>");
			return;
		}

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

		System.out.println("Source Code Abstracted successfully!");
		System.out.println("Abstracted Code: "+outputCodePath);
		System.out.println("Mapping: "+mapOutputFile);
	}

	private void checkInputs(Path inputCodePath, Path idiomsFilePath, Path outputCodePath) {
		Path outputDirectory = outputCodePath.getParent();
		if (!Files.isDirectory(outputDirectory)) {
			System.err.println("Output folder does not exist: " + outputDirectory);
			System.exit(1);
		}
		if (!Files.isRegularFile(inputCodePath)) {
			System.err.println("Input code file does not exist: " + inputCodePath);
			System.exit(1);
		}
		if (!Files.isRegularFile(idiomsFilePath)) {
			System.err.println("Idiom file does not exist: " + idiomsFilePath);
			System.exit(1);
		}
	}
}
