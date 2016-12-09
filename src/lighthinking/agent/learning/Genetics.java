package lighthinking.agent.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Genetics {

	public static final int MAX_GENERATIONS = 5; 	// first generation is number 1
	public static final int MAX_SIM_TICKS = 300;
	public static final int TICKS_PER_BIT = 5; 		// each bit represents a state  change every TICKS_PER_BIT  ticks
	public static final int GENERATION_SIZE = 20; 	// chromossomes per individual
	public static final int POPULATION_SIZE = 21; 	// number of individuals
	public static final double CROSSOVER_PROB = 0.3;
	public static final int ELITE_INDIVIDUALS = 4;

	public static int currGeneration = 0;
	public static int currIndividual = 0;

	public static HashMap<Integer, ArrayList<Chromossome>> individualChromossomes;

	// returns true if reached last generation
	public static boolean nextGeneration() {
		if (currGeneration == MAX_GENERATIONS) {
			return true;
		} else if (currGeneration == 0) {
			individualChromossomes = new HashMap<>();
			for (int i = 0; i < POPULATION_SIZE; ++i) {
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

	public static ArrayList<Chromossome> generateIndividualChromossomes() {
		ArrayList<Chromossome> chromossomes = new ArrayList<>();

		int chromossome_size = MAX_SIM_TICKS / TICKS_PER_BIT;
		Random rand = new Random();
		for (int i = 0; i < GENERATION_SIZE; ++i) {
			String chromossome = "";
			for (int j = 0; j < chromossome_size; ++j) {
				if (rand.nextBoolean()) {
					chromossome += "1";
				} else {
					chromossome += "0";
				}
			}
			chromossomes.add(new Chromossome(chromossome));
		}

		return chromossomes;
	}

	public static String[] crossoverChromossomes(String c1, String c2, int splitIndex) {
		String new_c1 = c1.substring(0, splitIndex);
		String new_c2 = c2.substring(0, splitIndex);
		new_c1 += c2.substring(splitIndex);
		new_c2 += c1.substring(splitIndex);
		return new String[] { new_c1, new_c2 };
	}
	
	public static ArrayList<Chromossome> evolveGeneration(ArrayList<Chromossome> oldGeneration) {
		ArrayList<Chromossome> newGeneration = new ArrayList<>();
		
		Collections.sort(oldGeneration, Chromossome.comparator);
		
		for(int i = 0; i < ELITE_INDIVIDUALS; ++i) {
			newGeneration.add(oldGeneration.get(i).cleanCopy());	
		}
		
		double totalValue = 0;
		for(Chromossome c : oldGeneration) {
			totalValue += c.value;
			c.value = totalValue;
		}
		for(Chromossome c : oldGeneration) {
			c.value = c.value / totalValue;
		}
		
		Random rand = new Random();
		for(int i = 0; i < GENERATION_SIZE - ELITE_INDIVIDUALS; ++i) {
			double selected = rand.nextDouble();
			for(Chromossome c : oldGeneration) {
				if(c.value >= selected) {
					newGeneration.add(c);
					break;
				}
			}
		}
	
		return newGeneration;
	}
}
