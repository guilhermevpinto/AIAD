package lighthinking.agent;

import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent {
	
	protected AgentManager agentManager;
	protected SumoVehicle sumoVehicle;

	protected VehicleAgent(String s, AgentManager mngr) {
		super();
		internalID = s;
		agentManager = mngr;
		sumoVehicle = new SumoVehicle(internalID);
	}
}
