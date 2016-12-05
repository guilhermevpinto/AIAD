package lighthinking.agent.reaction;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class ReactionTLAgent extends TLAgent {

	public ReactionTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.REACTION;
	}

	@Override
	public void update() {
		super.update();
	}

}
