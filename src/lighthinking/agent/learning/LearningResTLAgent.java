package lighthinking.agent.learning;

import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class LearningResTLAgent extends TLAgent {
	
	private String result;
	
	public LearningResTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		type = Type.LEARNING;
		result = GeneticsResults.learningResults.get(id);
	}
	
	@Override
	public void update() {
		if(Genetics.shouldProgramSwitch(result, Agent.tick)) {
			skipCurrentPhase();
		}
	}

}
