package lighthinking.agent.learning;

public class Genetics {

	public static final int MAX_GENERATIONS = 5;
	public static final int MAX_SIM_TICKS = 2;
	public static final int GENERATION_SIZE = 20;

	public static int currGeneration = 0;

	// returns true if reached last generation
	public static boolean nextGeneration() {
		return true;
	}

	// returns true if reached last individual
	public static boolean nextIndividualsOnGeneration() {
		return true;
	}
}
