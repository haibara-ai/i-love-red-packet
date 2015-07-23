package ilrp.strategy.smallest.gen;

import java.io.IOException;

public class GenerateAll {

	public static void main(String[] args) throws IOException {
		System.out.println("generating probability matrix");
		SmallestProbabilityGenerator.main(null);
		System.out.println("generating ranking");
		SmallestRankGenerator.main(null);
		System.out.println("finished");
	}

}
