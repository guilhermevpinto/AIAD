package lighthinking.agent.skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class SkipperTLAgent extends TLAgent {

	public SkipperTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.REACTION;
	}

	@Override
	public void update() {
		super.update();
	}

}
