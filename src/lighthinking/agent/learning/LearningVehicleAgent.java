package lighthinking.agent.learning;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class LearningVehicleAgent extends VehicleAgent {

	protected LearningVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		type = Type.LEARNING;
	}
	
	

}
