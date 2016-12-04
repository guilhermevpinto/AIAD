package lighthinking.agent;

@SuppressWarnings("serial")
public abstract class Agent extends jade.core.Agent {
	
	public static enum Type {
		BASIC,	// DEFAULT
		REACTION
	}
	
	protected Type type;
}
