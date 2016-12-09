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
	public static final int CHROMOSSOME_SIZE = MAX_SIM_TICKS / TICKS_PER_BIT;

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
			for (int i = 0; i < POPULATION_SIZE; ++i) {
				ArrayList<Chromossome> c = individualChromossomes.get(i);
				individualChromossomes.put(i, evolveGeneration(c));
			}
			++currGeneration;
		}

		return false;
	}

	// returns true if reached last individual
	public static boolean nextIndividualsOnGeneration() {
		return true;
	}

	// called when individual set ended
	// gives penalty if "wasTimedOut" is true
	public static void evalIndividuals(boolean wasTimedOut) {

	}

	public static ArrayList<Chromossome> generateIndividualChromossomes() {
		ArrayList<Chromossome> chromossomes = new ArrayList<>();

		Random rand = new Random();
		for (int i = 0; i < GENERATION_SIZE; ++i) {
			String chromossome = "";
			for (int j = 0; j < CHROMOSSOME_SIZE; ++j) {
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

	public static Chromossome[] crossoverChromossomes(Chromossome c1, Chromossome c2, int splitIndex) {
		String s1 = c1.content;
		String s2 = c2.content;
		String new_c1 = s1.substring(0, splitIndex);
		String new_c2 = s2.substring(0, splitIndex);
		new_c1 += s2.substring(splitIndex);
		new_c2 += s1.substring(splitIndex);
		return new Chromossome[] { new Chromossome(new_c1), new Chromossome(new_c2) };
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
	
		ArrayList<Chromossome> crossedGeneration = crossoverGeneration(newGeneration);
		Collections.shuffle(crossedGeneration);
		return crossedGeneration;
	}
	
	public static ArrayList<Chromossome> crossoverGeneration(ArrayList<Chromossome> generation) {
		ArrayList<Chromossome> crossedGeneration = new ArrayList<>();
		
		Random rand = new Random();
		int crossoverIndex = rand.nextInt(CHROMOSSOME_SIZE);
		Chromossome firstCross = null;
		for(Chromossome c : generation) {
			if(rand.nextDouble() <= CROSSOVER_PROB) {
				if(firstCross == null) {
					firstCross = c;
				} else {
					Chromossome crossed[] = crossoverChromossomes(firstCross, c, crossoverIndex);
					crossedGeneration.add(crossed[0]);
					crossedGeneration.add(crossed[1]);
					firstCross = null;
				}
			} else {
				crossedGeneration.add(c);
			}
		}
		if(firstCross != null) {
			crossedGeneration.add(firstCross);
		}
		
		return crossedGeneration;
	}
	
	public static String getChromossomeContentForIndividual(int id) {
		return individualChromossomes.get(id).get(currIndividual).content;
	}
}
