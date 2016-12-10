package lighthinking.agent.learning;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import lighthinking.Statistics;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

public class Genetics {
	
	public static boolean LOCAL_EVAL_ONLY = false;

	public static final int MAX_GENERATIONS = 30; 	// first generation is number 1
	public static final int MAX_SIM_TICKS = 470;
	public static final int TICKS_PER_BIT = 5; 		// each bit represents a state  change every TICKS_PER_BIT  ticks
	public static final int GENERATION_SIZE = 30; 	// chromossomes per individual
	public static final int POPULATION_SIZE = 21; 	// number of individuals
	public static final double CROSSOVER_PROB = 0.2;
	public static final int ELITE_INDIVIDUALS = 5;
	public static final int CHROMOSSOME_SIZE = MAX_SIM_TICKS / TICKS_PER_BIT;
	public static final double PENALTY = 0.5;

	public static int currGeneration = 0;
	public static int currIndividual = 0;
	
	private static PrintWriter writer;// = new PrintWriter("the-file-name.txt", "UTF-8");

	public static HashMap<String, ArrayList<Chromossome>> individualChromossomes;
	public static String[] ids = new String[] {
		"A1", "A2", "A3", "B0", "B1", "B2", "B3", "B4", "C0", "C1", "C2", "C3", "C4", "D0", "D1", "D2", "D3", "D4", "E1", "E2", "E3"
	};
	
	public static void startLog() {
		try {
			writer = new PrintWriter("res/genetics/log.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.err.println("Unable to start log");
			e.printStackTrace();
		}
	}
	
	public static void tryLog(String s) {
		if(writer != null) {
			writer.println(s);
		}
	}
	
	public static void endLog() {
		if(writer != null) {
			writer.close();
		}
	}

	// returns true if reached last generation
	public static boolean nextGeneration() {
		Statistics.resetStats();
		for(String id : ids) {
			if(AgentManager.trafficLightAgents != null) {
				AgentManager.trafficLightAgents.get(id).resetLocalStopped();
			}
		}
		if (currGeneration == MAX_GENERATIONS) {
			return true;
		} else if (currGeneration == 0) {
			individualChromossomes = new HashMap<>();
			for (int i = 0; i < POPULATION_SIZE; ++i) {
				individualChromossomes.put(ids[i], generateIndividualChromossomes());
			}
			currGeneration = 1;
			currIndividual = 0;
		} else {
			for (int i = 0; i < POPULATION_SIZE; ++i) {
				String id = ids[i];
				ArrayList<Chromossome> c = individualChromossomes.get(id);
				individualChromossomes.put(ids[i], evolveGeneration(c));
			}
			++currGeneration;
			currIndividual = 0;
		}

		return false;
	}

	// returns true if reached last individual
	public static boolean nextIndividualsOnGeneration() {
		Statistics.resetStats();
		for(String id : ids) {
			if(AgentManager.trafficLightAgents != null) {
				AgentManager.trafficLightAgents.get(id).resetLocalStopped();
			}
		}
		++currIndividual;
		if(currIndividual >= GENERATION_SIZE) {
			tryLog(printGenerationFull());
			return true;
		}
		return false;
	}

	// called when individual set ended
	// gives penalty if "wasTimedOut" is true
	public static void evalIndividuals(boolean wasTimedOut) {
		if(LOCAL_EVAL_ONLY) {
			for(String id : ids) {
				double perf = (double) (1.0/AgentManager.trafficLightAgents.get(id).getLocalStopped());
				if(wasTimedOut) {
					System.out.println("Penalty to generation " + currGeneration + "/" + currIndividual);
					perf *= PENALTY;
				}
				individualChromossomes.get(id).get(currIndividual).value = perf;
			}
		} else {
			double perf = Statistics.getOverallPerformance();
			if(wasTimedOut) {
				System.out.println("Penalty to generation " + currGeneration + "/" + currIndividual);
				perf *= PENALTY;
			}
			System.out.println("Perf for generation " + perf);
			for(int i = 0; i < POPULATION_SIZE; ++i) {
				individualChromossomes.get(ids[i]).get(currIndividual).value = perf;
			}
		}
	}

	public static ArrayList<Chromossome> generateIndividualChromossomes() {
		ArrayList<Chromossome> chromossomes = new ArrayList<>();

		Random rand = new Random();
		for (int i = 0; i < GENERATION_SIZE; ++i) {
			String chromossome = "";
			for (int j = 0; j <= CHROMOSSOME_SIZE; ++j) {
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
		ArrayList<Chromossome> mutable = new ArrayList<>();
		
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
					mutable.add(c.cleanCopy());
					break;
				}
			}
		}
	
		ArrayList<Chromossome> crossedGeneration = crossoverGeneration(mutable);
		newGeneration.addAll(crossedGeneration);
		Collections.shuffle(newGeneration);
		return newGeneration;
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
			crossedGeneration.add(firstCross.cleanCopy());
		}
		
		return crossedGeneration;
	}
	
	public static Chromossome getChromossomeForIndividual(String id) {
		return individualChromossomes.get(id).get(currIndividual);
	}
	
	public static String getChromossomeContentForIndividual(String id) {
		return getChromossomeForIndividual(id).content;
	}
	
	public static boolean shouldProgramSwitch(String chromossome, int tick) {
		if(tick % TICKS_PER_BIT == 0) {
			try {
				return chromossome.charAt(tick / TICKS_PER_BIT) == '1';	
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	public static boolean shouldIndividualSwitch(String individualID, int tick) {
		if(tick % TICKS_PER_BIT == 0) {
			return getChromossomeContentForIndividual(individualID).charAt(tick / TICKS_PER_BIT) == '1';	
		}
		return false;
	}
	
	public static String printGenerationFull() {
		String content = "Result of generation " + currGeneration + '\n';
		
		double bestValue = 0;
		double sumValues = 0;
		double numValues = 0;
		
		for(String id : ids) {
			content += "Agent " + id + '\n';
			ArrayList<Chromossome> chromossomes = individualChromossomes.get(id);
			for(Chromossome c : chromossomes) {
				sumValues += c.value;
				++numValues;
				if(c.value > bestValue) {
					bestValue = c.value;
				}
				content += "" + c.content + "  =>  " + c.value + '\n';
			}
			content += '\n';
		}
		content += "AGV( " + sumValues/numValues + ")  BEST( " + bestValue + ")" + '\n';
		
		return content;
	}
}
