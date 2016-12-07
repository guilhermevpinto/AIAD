package lighthinking.agent.basic;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class BasicVehicleAgent extends VehicleAgent {
	public BasicVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}
}
