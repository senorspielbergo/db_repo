package main;

public class Main {
	public static void main(String[] args) {
		try {
			new Setup(SetupOptions.generate(args)).run();
		} catch (Exception e) {
			e.printStackTrace();
			Setup.printHelpMessage();
		}
		System.exit(0);
	}
}
