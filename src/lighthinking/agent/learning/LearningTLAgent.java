package lighthinking.agent.learning;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class LearningTLAgent extends TLAgent {

	public LearningTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		type = Type.LEARNING;
	}

}
