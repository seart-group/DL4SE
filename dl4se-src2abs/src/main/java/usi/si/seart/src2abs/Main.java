package usi.si.seart.src2abs;

public class Main {

	public static void main(String[] args) {

		if (args.length < 4) {
			printIllegalArgumentError("Not enough arguments!");
			System.exit(1);
		}

		String granularity = args[0];
		String inputCodePath = args[1];
		String outputCodePath = args[2];
		String idiomsFilePath = args[3];

		Abstractor abstractor = new Abstractor();
		Parser.CodeGranularity codeGranularity;
		switch (granularity.toLowerCase()) {
			case "method":
				abstractor.abstractCode(Parser.CodeGranularity.METHOD, inputCodePath, outputCodePath, idiomsFilePath);
				break;
			case "class":
				abstractor.abstractCode(Parser.CodeGranularity.CLASS, inputCodePath, outputCodePath, idiomsFilePath);
				break;
			default:
				printIllegalArgumentError("Wrong granularity! Use: {method, class}");
				System.exit(1);
		}
	}

	private static void printIllegalArgumentError(String error) {
		System.err.println("ERROR: " + error);
		System.out.println("-----------------------");
		System.out.println("src2abs usage:");
		System.out.println("1. Granularity {method, class}");
		System.out.println("2. Source Code file path input");
		System.out.println("3. Abstract Code file path output");
		System.out.println("4. Idiom file path");
	}
}
