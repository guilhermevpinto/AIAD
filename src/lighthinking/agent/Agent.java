package lighthinking.agent;

import utils.Updateable;

@SuppressWarnings("serial")
public abstract class Agent extends jade.core.Agent implements Updateable {
	
	public static int tick = 0;
	
	public static enum Type {
		BASIC,	// DEFAULT
		SKIPPER,
		DOUBLE_SKIPPER,
		COM,
		LEARNING,
		LEARNING_RESULTS,
		LEARNING_LOCAL,
		LEARNING_LOCAL_RESULTS
	}
	
	public static enum Map {
		MAP1,	// DEFAULT
		MAP2
	}
	
	protected Type type;
	protected String internalID;
	
	public Type getType() {
		return type;
	}
	
	public String getID() {
		return internalID;
	}
	
	public void finish() {}
	
	public static void updateTicker() {
		++tick;
	}

}
