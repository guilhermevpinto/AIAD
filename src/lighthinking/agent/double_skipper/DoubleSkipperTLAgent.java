package lighthinking.agent.double_skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class DoubleSkipperTLAgent extends TLAgent {
	public DoubleSkipperTLAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}
}
