package lighthinking.agent.com;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class ComTLAgent extends TLAgent {

	public ComTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.COM;
	}
	
	public void setup() {
		System.out.println("Ready");
	}

}
