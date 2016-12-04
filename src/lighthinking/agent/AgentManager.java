package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lighthinking.agent.basic.BasicTLAgent;
import lighthinking.agent.basic.BasicVehicleAgent;
import lighthinking.agent.reaction.ReactionTLAgent;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentManager {

	private HashMap<String, TLAgent> trafficLightAgents = new HashMap<String, TLAgent>();
	private HashMap<String, VehicleAgent> vehicleAgents = new HashMap<String, VehicleAgent>();
	private HashMap<String, SumoLane> lanes = new HashMap<String, SumoLane>();
	public static HashMap<String, Integer> vehStoppedPerLane = new HashMap<String, Integer>();
	public static HashSet<String> laneIDs = new HashSet<>();

	public AgentManager(Agent.Type mode) {
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehiclesIds = SumoCom.getAllVehiclesIds();

		for (String id : trafficLightIds) {
			switch (mode) {
			case REACTION:
				trafficLightAgents.put(id, new ReactionTLAgent(id, this));
				break;
			case BASIC:
			default:
				trafficLightAgents.put(id, new BasicTLAgent(id, this));
				break;
			}
		}
		for (String id : vehiclesIds) {
			vehicleAgents.put(id, new BasicVehicleAgent(id, this));
		}

		for (String ids : laneIDs) {
			vehStoppedPerLane.put(ids, 0);
		}
	}

	/**
	 * Adds a vehicle Agent
	 * 
	 * @param id
	 */
	public void addVehicleAgent(String id) {
		vehicleAgents.put(id, new BasicVehicleAgent(id, this));
	}

	/**
	 * Removes a vehicle Agent
	 * 
	 * @param id
	 * @return
	 */
	public Boolean removeVehicleAgent(String id) {
		return vehicleAgents.remove(id) != null;
	}

	/**
	 * Updates manager, adding new Vehicles, removing arrived ones
	 */
	public synchronized void updateManager() {

		// adding and removing vehicles
		for (SumoVehicle vehicle : SumoCom.vehicles) {
			if (!vehicleAgents.containsKey(vehicle.id))
				addVehicleAgent(vehicle.id);
		}
		for (String id : SumoCom.arrivedVehicles) {
			removeVehicleAgent(id);
		}

		// get number of cars stopped by lane

		getVehicleStoppedInLanes();

		// update each vehicle and TF
		for (HashMap.Entry<String, VehicleAgent> entry : vehicleAgents.entrySet()) {
			VehicleAgent vehicle = entry.getValue();
			vehicle.update();
		}
		for (HashMap.Entry<String, TLAgent> entry : trafficLightAgents.entrySet()) {
			TLAgent TF = entry.getValue();
			TF.update();
		}
	}

	public void getVehicleStoppedInLanes() {
		for (String id : laneIDs)
			if (!vehStoppedPerLane.containsKey(id))
				vehStoppedPerLane.put(id, lanes.get(id).getNumVehiclesStopped(0.2));
	}

	public static void addLane(String id) {
		laneIDs.add(id);
	}

}
