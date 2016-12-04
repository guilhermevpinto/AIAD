package lighthinking.agent.reaction;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class ReactionTLAgent extends TLAgent {

	public ReactionTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.REACTION;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update() {
		// TODO completar
		
		//int indexState = this.getIDCurrentPhase();
		//int numOfStoppedVeh = AgentManager.vehStoppedPerLane.get(key)
	}

	@Override
	public void update(AgentManager agentManager) {
		// TODO completar		
	}
	

}
