package lighthinking.agent.basic;

import jade.core.behaviours.SimpleBehaviour;
import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class BasicVehicleAgent extends VehicleAgent {

	public BasicVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.BASIC;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		try {
			if (internalID.equals("60")) {
				System.out.println("60 " + sumoVehicle.getSpeed());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(AgentManager agentManager) {
		// TODO Auto-generated method stub

	}
}
