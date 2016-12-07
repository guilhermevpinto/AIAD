package lighthinking.agent.skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class SkipperVehicleAgent extends VehicleAgent {

	public SkipperVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}

	@Override
	public void update() {
		super.update();
	}

}
