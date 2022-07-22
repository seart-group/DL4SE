package usi.si.seart.src2abs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Abstractor {

	public void abstractCode(
			Parser.CodeGranularity granularity, String inputCodePath, String outputCodePath, String idiomsFilePath
	) {

		//Check inputs
		checkInputs(inputCodePath, idiomsFilePath, outputCodePath);
		String mapOutputFile = outputCodePath+".map";

		//Idioms
		Set<String> idioms = new HashSet<>();
		try (Stream<String> stream = Files.lines(Paths.get(idiomsFilePath))) {
			idioms = stream.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Parser
		Parser parser = new Parser(granularity);
		try {
			parser.parseFile(inputCodePath);
		} catch(StackOverflowError e){
			System.err.println("StackOverflow during parsing!");
		} catch (Exception e) {
			System.err.println("Parsing ERROR!");
			PrintWriter pw;
			try {
				pw = new PrintWriter(outputCodePath);
				pw.println("<ERROR>");
				pw.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			return;
		}

		//Tokenizer
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.setTypes(parser.getTypes());
		tokenizer.setMethods(parser.getMethods());
		tokenizer.setIdioms(idioms);
		tokenizer.setAnnotations(parser.getAnnotations());

		String abstractCode = tokenizer.tokenize(inputCodePath);

		//Write output files
		writeAbstractCode(abstractCode, outputCodePath);
		tokenizer.exportMaps(mapOutputFile);

		System.out.println("Source Code Abstracted successfully!");
		System.out.println("Abstracted Code: "+outputCodePath);
		System.out.println("Mapping: "+mapOutputFile);
	}

	private void writeAbstractCode(String abstractCode, String outputCodePath) {
		try {
			Files.write(Paths.get(outputCodePath), abstractCode.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkInputs(String inputCodePath, String idiomsFilePath, String outputCodePath) {
		checkFileExists(inputCodePath, "Input code file does not exist: ");
		checkFileExists(idiomsFilePath, "Idiom file does not exist: ");
		checkParentFolderExists(outputCodePath, "Output folder does not exist: ");
	}

	private void checkParentFolderExists(String filePath, String error) {
		if (!Files.isDirectory(Paths.get(filePath).getParent())) {
			try {
				throw new FileNotFoundException(error+filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkFileExists(String filePath, String error) {
		if (!fileExists(filePath)){
			try {
				throw new FileNotFoundException(error+filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean fileExists(String path) {
		return new File(path).isFile();
	}
}
