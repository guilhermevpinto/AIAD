package lighthinking.agent;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent {
	
	protected AgentManager agentManager;

	protected VehicleAgent(String s, AgentManager mngr) {
		super();
		internalID = s;
		agentManager = mngr;
	}
}
