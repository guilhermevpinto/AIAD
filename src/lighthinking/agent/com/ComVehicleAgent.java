package lighthinking.agent.com;

import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class ComVehicleAgent extends VehicleAgent {
	public ComVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.COM;
	}
	
	public void setup() {
		System.out.println("Ready");
	}
}
