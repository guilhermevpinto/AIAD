package lighthinking.agent.learning;

import java.util.ArrayList;

public class TestMain {

	public static void main(String[] args) {
		ArrayList<Chromossome> p1 = Genetics.generateIndividualChromossomes();
		Chromossome.printGeneration(p1);
		System.out.println("");
		ArrayList<Chromossome> p2 = Genetics.evolveGeneration(p1);
		Chromossome.printGeneration(p2);
	}

}