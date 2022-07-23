package usi.si.seart.src2abs;

import com.github.javaparser.ParseProblemException;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Main {

	public static void main(String[] args) {
		int code = 0;
		try {
			Parser.Granularity granularity = Parser.Granularity.valueOf(args[0].toUpperCase());

			Path inputPath = Path.of(args[1]);
			Path outputPath = Path.of(args[2]);
			Path idiomsPath = Path.of(args[3]);

			Path outputParent = outputPath.getParent();

			if (Files.notExists(inputPath)) throw new NoSuchFileException(inputPath.toString());
			if (Files.notExists(outputParent)) throw new NoSuchFileException(outputParent.toString());
			if (Files.notExists(idiomsPath)) throw new NoSuchFileException(idiomsPath.toString());

			new Abstractor().abstractCode(granularity, inputPath, outputPath, idiomsPath);
		} catch (ArrayIndexOutOfBoundsException ignored) {
			System.err.println("Not enough arguments!");
			printUsage();
			code = 1;
		} catch (IllegalArgumentException ignored) {
			System.err.println("Wrong granularity!");
			printUsage();
			code = 1;
		} catch (ParseProblemException ex) {
			System.err.println("Parsing Error: ");
			ex.printStackTrace();
			code = 1;
		} catch (NoSuchFileException ex) {
			System.err.println("File or directory does not exist: " + ex.getFile());
			code = 1;
		} catch (Throwable thr) {
			thr.printStackTrace();
			code = 1;
		} finally {
			System.exit(code);
		}
	}

	private static void printUsage() {
		System.out.println("-----------------------");
		System.out.println("src2abs usage:");
		System.out.println("1. Granularity {method, class}");
		System.out.println("2. Source Code file path input");
		System.out.println("3. Abstract Code file path output");
		System.out.println("4. Idiom file path");
	}
}
