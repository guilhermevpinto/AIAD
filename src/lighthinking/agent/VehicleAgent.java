package lighthinking.agent;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent implements Updateable {

	String VehicleAgentID;

	protected VehicleAgent(String s) {
		VehicleAgentID = s;
	}
}
