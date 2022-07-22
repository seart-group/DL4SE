package usi.si.seart.src2abs;

public class Main {

	private static final String SINGLE_MODE = "single";
	private static final String PAIR_MODE = "pair";

	private static final String GRANULARITY_METHOD = "method";
	private static final String GRANULARITY_CLASS = "class";

	public static void main(String[] args) {
		if (args.length < 1) {
			printIllegalArgumentError("Not enough arguments!");
			throw new IllegalArgumentException();
		}

		String mode = args[0];

		if (mode.equalsIgnoreCase(SINGLE_MODE)) {
			abstractCode(args);
		} else if (mode.equalsIgnoreCase(PAIR_MODE)) {
			abstractCodePair(args);
		} else {
			printIllegalArgumentError("Wrong mode! Use: {single, pair}");
			throw new IllegalArgumentException();
		}
	}

	private static void abstractCode(String[] args) {
		if (args.length < 5) {
			printIllegalArgumentError("Not enough arguments!");
			throw new IllegalArgumentException();
		}

		String granularity = args[1];
		String inputCodePath = args[2];
		String outputCodePath = args[3];
		String idiomsFilePath = args[4];

		Parser.CodeGranularity codeGranularity = getCodeGranularity(granularity);

		new Abstractor().abstractCode(codeGranularity, inputCodePath, outputCodePath, idiomsFilePath);
	}

	private static void abstractCodePair(String[] args) {
		if (args.length < 7) {
			printIllegalArgumentError("Not enough arguments!");
			throw new IllegalArgumentException();
		}

		String granularity = args[1];
		String inputCodePath1 = args[2];
		String inputCodePath2 = args[3];
		String outputCodePath1 = args[4];
		String outputCodePath2 = args[5];
		String idiomsFilePath = args[6];

		Parser.CodeGranularity codeGranularity = getCodeGranularity(granularity);

		new Abstractor().abstractCodePair(
				codeGranularity,
				inputCodePath1,
				inputCodePath2,
				outputCodePath1,
				outputCodePath2,
				idiomsFilePath
		);
	}

	private static Parser.CodeGranularity getCodeGranularity(String granularity) {
		if (granularity.equalsIgnoreCase(GRANULARITY_METHOD)) {
			return Parser.CodeGranularity.METHOD;
		} else if (granularity.equalsIgnoreCase(GRANULARITY_CLASS)) {
			return Parser.CodeGranularity.CLASS;
		} else {
			printIllegalArgumentError("Wrong granularity! Use: {method, class}");
			throw new IllegalArgumentException();
		}
	}

	private static void printIllegalArgumentError(String error) {
		System.err.println("ERROR: "+error);
		System.out.println("-----------------------");
		System.out.println("src2abs usage:");
		System.out.println("1. Mode {single, pair}");
		System.out.println("2. Granularity {method, class}");
		System.out.println("3. Source Code file(s) path(s) input (1 file if single mode, 2 files if pair mode)");
		System.out.println("4. Abstract Code file(s) path(s) output (1 file if single mode, 2 files if pair mode)");
		System.out.println("5. Idiom file path");
	}
}
