package lighthinking.agent.learning;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class Genetics {

	public static final int MAX_GENERATIONS = 5; 	// first generation is number 1
	public static final int MAX_SIM_TICKS = 300;
	public static final int TICKS_PER_BIT = 5;		// each bit represents a state change every TICKS_PER_BIT ticks
	public static final int GENERATION_SIZE = 20;	// chromossomes per individual
	public static final int POPULATION_SIZE = 21;	// number of individuals

	public static int currGeneration = 0;
	public static int currIndividual = 0;
	
	public static HashMap<Integer, Vector<String>> individualChromossomes;

	// returns true if reached last generation
	public static boolean nextGeneration() {
		if(currGeneration == MAX_GENERATIONS) {
			return true;
		} else if (currGeneration == 0) {
			individualChromossomes = new HashMap<>();
			for(int i = 0; i < POPULATION_SIZE; ++i) {
				individualChromossomes.put(i, generateIndividualChromossomes());
			}
			currGeneration = 1;
		} else {
			// TODO apply crossover and mutation
		}
		
		return false;
	}

	// returns true if reached last individual
	public static boolean nextIndividualsOnGeneration() {
		return true;
	}
	
	// called when individual set ended
	public static void evalIndividuals() {
		
	}
	
	// called when generation ended
	public static void evalGeneration() {
		
	}
	
	public static Vector<String> generateIndividualChromossomes() {
		Vector<String> chromossomes = new Vector<>();
		
		int chromossome_size = MAX_SIM_TICKS / TICKS_PER_BIT;
		Random rand = new Random();
		for(int i = 0; i < GENERATION_SIZE; ++i) {
			String chromossome = "";
			for(int j = 0; j < chromossome_size; ++j) {
				if(rand.nextBoolean()) {
					chromossome += "1";
				} else {
					chromossome += "0";
				}
			}
			chromossomes.add(chromossome);
			System.out.println(chromossome);
		}
		
		return chromossomes;
	}
}
