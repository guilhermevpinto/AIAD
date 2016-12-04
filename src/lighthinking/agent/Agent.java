package lighthinking.agent;

import utils.Updateable;

@SuppressWarnings("serial")
public abstract class Agent extends jade.core.Agent implements Updateable {
	
	public static enum Type {
		BASIC,	// DEFAULT
		REACTION
	}
	
	protected Type type;
	protected String internalID;
}
