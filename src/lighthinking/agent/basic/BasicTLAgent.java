package lighthinking.agent.basic;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class BasicTLAgent extends TLAgent {

	public BasicTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.BASIC;
	}

}
