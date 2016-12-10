package lighthinking.agent.learning;

import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class LearningLocalResTLAgent extends TLAgent {
	
	private String result;
	
	public LearningLocalResTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		type = Type.LEARNING;
		result = GeneticsResults.learningLocalResults.get(id);
	}
	
	@Override
	public void update() {
		if(Genetics.shouldProgramSwitch(result, Agent.tick)) {
			skipCurrentPhase();
		}
	}

}
