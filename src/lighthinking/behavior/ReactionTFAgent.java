package lighthinking.behavior;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TFAgent;

public class ReactionTFAgent extends TFAgent {

	public ReactionTFAgent(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update() {
		// TODO completar
		int indexState = this.getIDCurrentPhase();
		
		//int numOfStoppedVeh = AgentManager.vehStoppedPerLane.get(key)
	}

	@Override
	public void update(AgentManager agentManager) {
		// TODO completar		
	}
	

}
