package lighthinking.agent;

import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent {
	
	protected AgentManager agentManager;
	protected SumoVehicle sumoVehicle;
	protected boolean alive;

	protected VehicleAgent(String s, AgentManager mngr) {
		super();
		internalID = s;
		agentManager = mngr;
		sumoVehicle = new SumoVehicle(internalID);
		alive = false;
	}
	
	@Override
	public void update() {
		if(!alive && sumoVehicle.getSpeed() >= 0) {
			alive = true;
			System.out.println("Vehicle " + internalID + " started.");
		}
	}

	@Override
	public void finish() {
		if(alive) {
			alive = false;
			System.out.println("Vehicle " + internalID + " finished.");
		}
	}
}
