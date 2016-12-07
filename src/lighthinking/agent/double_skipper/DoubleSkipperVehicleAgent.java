package lighthinking.agent.double_skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class DoubleSkipperVehicleAgent extends VehicleAgent {
	public DoubleSkipperVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}
}
