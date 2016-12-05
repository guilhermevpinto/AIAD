package lighthinking.agent.reaction;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class ReactionVehicleAgent extends VehicleAgent {

	public ReactionVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}

	@Override
	public void update() {
		super.update();
	}

}
