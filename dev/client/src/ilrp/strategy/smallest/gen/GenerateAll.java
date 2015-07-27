package ilrp.strategy.smallest.gen;

import ilrp.strategy.smallest.SmallestDecision;

import java.io.IOException;

public class GenerateAll {

	public static void main(String[] args) throws IOException {
		SmallestDecision.initialize();
//		System.out.println("=== Generating probability matrix ===");
//		SmallestProbabilityGenerator.generate();
		System.out.println("=== Generating ranking ===");
		SmallestRankGenerator.generate();
		System.out.println("=== Finished ===");
	}

}
