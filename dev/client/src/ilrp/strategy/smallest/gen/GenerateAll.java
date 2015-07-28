package ilrp.strategy.smallest.gen;

import java.io.IOException;

public class GenerateAll {

	public static void main(String[] args) throws IOException {
		System.out.println("=== Generating probability matrix ===");
		SmallestProbabilityGenerator.generate();
		System.out.println("=== Generating ranking ===");
		SmallestRankGenerator.generate();
		System.out.println("=== Finished ===");
	}

}
