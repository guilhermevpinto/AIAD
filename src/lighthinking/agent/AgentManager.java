package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lighthinking.agent.basic.BasicTLAgent;
import lighthinking.agent.basic.BasicVehicleAgent;
import lighthinking.agent.reaction.ReactionTLAgent;
import lighthinking.agent.reaction.ReactionVehicleAgent;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentManager {

	private Agent.Type agentMode;

	private HashMap<String, TLAgent> trafficLightAgents;
	private HashMap<String, VehicleAgent> vehicleAgents;
	private static HashSet<String> laneIDs; // to speed up some calculations
	private HashMap<String, SumoLane> lanes;
	private static HashMap<String, Integer> vehiclesStoppedPerLane;

	public AgentManager() {
		this(Agent.Type.BASIC);
	}

	public AgentManager(Agent.Type agentMode) {
		this.agentMode = agentMode;

		trafficLightAgents = new HashMap<String, TLAgent>();
		vehicleAgents = new HashMap<String, VehicleAgent>();
		lanes = new HashMap<String, SumoLane>();
		vehiclesStoppedPerLane = new HashMap<String, Integer>();
		laneIDs = new HashSet<String>();

		init();
	}

	private void init() {
//		laneIDs = new HashSet<String>(SumoCom.getAllEdgesIds());
//		for (String id : laneIDs) {
//			actualAddLane(id);
//			vehiclesStoppedPerLane.put(id, 0);
//		}

		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehiclesIds = SumoCom.getAllVehiclesIds();

		switch (agentMode) {
		case REACTION:
			for (String id : trafficLightIds) {
				addTLAgent(new ReactionTLAgent(id, this));
			}
			for (String id : vehiclesIds) {
				addVehicleAgent(new ReactionVehicleAgent(id, this));
			}

			break;
		case BASIC:
		default:
			for (String id : trafficLightIds) {
				addTLAgent(new BasicTLAgent(id, this));
			}
			for (String id : vehiclesIds) {
				addVehicleAgent(new BasicVehicleAgent(id, this));
			}

			break;
		}
	}

	public void addVehicleAgent(VehicleAgent agent) {
		vehicleAgents.put(agent.getID(), agent);
	}

	public Boolean removeVehicleAgent(String id) {
		return vehicleAgents.remove(id) != null;
	}

	public void addTLAgent(TLAgent agent) {
		trafficLightAgents.put(agent.getID(), agent);
	}

	public boolean removeTLAgent(String id) {
		return trafficLightAgents.remove(id) != null;
	}

	public void addLane(String id) {
		if(!laneIDs.contains(id)) {
			laneIDs.add(id);
		}
		if(!lanes.containsKey(id)) {
			actualAddLane(id);
		}
	}

	private void actualAddLane(String id) {
		lanes.put(id, new SumoLane(id));
	}

	public void updateVehicleStoppedInLanes() {
		for (String id : laneIDs)
			if (!vehiclesStoppedPerLane.containsKey(id))
				vehiclesStoppedPerLane.put(id, lanes.get(id).getNumVehiclesStopped(0.2));
	}

	/**
	 * Updates manager, adding new Vehicles, removing arrived ones
	 */
	public synchronized void updateManager() {
		// Add new vehicles and remove stopped vehicles
		updateAgentObjects();
		updateAgents();
		updateVehicleStoppedInLanes();
	}

	// Add new vehicles and remove stopped vehicles
	private void updateAgentObjects() {
		for (SumoVehicle vehicle : SumoCom.vehicles) {
			if (!vehicleAgents.containsKey(vehicle.id)) {
				switch (agentMode) {
				case REACTION:
					addVehicleAgent(new ReactionVehicleAgent(vehicle.id, this));
					break;
				case BASIC:
				default:
					addVehicleAgent(new BasicVehicleAgent(vehicle.id, this));
					break;
				}
			}
		}
		for (String id : SumoCom.arrivedVehicles) {
			removeVehicleAgent(id);
		}
	}

	// Update existing vehicles and traffic lights
	private void updateAgents() {
		for (HashMap.Entry<String, VehicleAgent> entry : vehicleAgents.entrySet()) {
			VehicleAgent vehicle = entry.getValue();
			vehicle.update();
		}
		for (HashMap.Entry<String, TLAgent> entry : trafficLightAgents.entrySet()) {
			TLAgent tf = entry.getValue();
			tf.update();
		}
	}

}
