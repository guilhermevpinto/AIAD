package lighthinking.behavior;

import jade.core.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TFAgent;

public class ReactionTFAgent extends TFAgent{

	public ReactionTFAgent(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public void update(){
		int indexState = this.getIDCurrentPhase();
		
		//int numOfStoppedVeh = AgentManager.vehStoppedPerLane.get(key)
	}
	

}
