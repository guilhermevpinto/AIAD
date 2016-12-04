package lighthinking.agent;

import jade.core.Agent;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent implements Updateable {

	String VehicleAgentID;

	protected VehicleAgent(String s) {
		VehicleAgentID = s;
	}
}
