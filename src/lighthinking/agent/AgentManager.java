package lighthinking.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import lighthinking.Config;
import lighthinking.agent.Agent.Type;
import lighthinking.agent.basic.BasicTLAgent;
import lighthinking.agent.basic.BasicVehicleAgent;
import lighthinking.agent.com.ComTLAgent;
import lighthinking.agent.com.ComVehicleAgent;
import lighthinking.agent.double_skipper.DoubleSkipperTLAgent;
import lighthinking.agent.double_skipper.DoubleSkipperVehicleAgent;
import lighthinking.agent.learning.LearningTLAgent;
import lighthinking.agent.learning.LearningVehicleAgent;
import lighthinking.agent.skipper.SkipperTLAgent;
import lighthinking.agent.skipper.SkipperVehicleAgent;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentManager {

	private Config config;
	private Agent.Type agentMode;

	public static HashMap<String, TLAgent> trafficLightAgents;
	private HashMap<String, VehicleAgent> vehicleAgents;
	private static HashSet<String> laneIDs; // to speed up some calculations
	private HashMap<String, SumoLane> lanes;
	private static HashMap<String, Integer> vehiclesStoppedPerLane;
	private ContainerController mainContainer;
	private ArrayList<String> arrivedVehicles;


	public AgentManager() {
		this(new Config(), null);
	}

	public AgentManager(Config config, ContainerController mainContainer) {
		this.config = config;
		this.mainContainer = mainContainer;
		this.agentMode = this.config.agentType;

		trafficLightAgents = new HashMap<String, TLAgent>();
		vehicleAgents = new HashMap<String, VehicleAgent>();
		lanes = new HashMap<String, SumoLane>();
		vehiclesStoppedPerLane = new HashMap<String, Integer>();
		laneIDs = new HashSet<String>();
		arrivedVehicles = new ArrayList<>();

		
		try {
			init();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws ControllerException{
//		laneIDs = new HashSet<String>(SumoCom.getAllEdgesIds());
//		for (String id : laneIDs) {
//			actualAddLane(id);
//			vehiclesStoppedPerLane.put(id, 0);
//		}
		
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehiclesIds = SumoCom.getAllVehiclesIds();
		
		try {
		
			switch (agentMode) {
			case SKIPPER:
				for (String id : trafficLightIds) {
					addTLAgent(new SkipperTLAgent(id, this));
				}
				for (String id : vehiclesIds) {
					addVehicleAgent(new SkipperVehicleAgent(id, this));
				}
	
				break;
			case DOUBLE_SKIPPER:
				for (String id : trafficLightIds) {
					addTLAgent(new DoubleSkipperTLAgent(id, this));
				}
				for (String id : vehiclesIds) {
					addVehicleAgent(new DoubleSkipperVehicleAgent(id, this));
				}
				break;
			case COM:
				for (String id : trafficLightIds) {
					ComTLAgent agent = new ComTLAgent(id, this);
					mainContainer.acceptNewAgent(agent.getID(), agent);
					mainContainer.getAgent(id).start();
					addTLAgent(agent);
				}
				for (String id : vehiclesIds) {
					ComVehicleAgent agent = new ComVehicleAgent(id, this);
					mainContainer.acceptNewAgent(agent.getID(), agent);
					mainContainer.getAgent(id).start();
					addVehicleAgent(agent);
				}
				break;
			case LEARNING:
				for (String id : trafficLightIds) {
					addTLAgent(new LearningTLAgent(id, this));
				}
				for (String id : vehiclesIds) {
					addVehicleAgent(new LearningVehicleAgent(id, this));
				}
				
				break;
			case BASIC:
			default:
				for (String id : trafficLightIds) {
					addTLAgent(new BasicTLAgent(id, this));
				}
				for (String id : vehiclesIds) {
					addVehicleAgent(new DoubleSkipperVehicleAgent(id, this));
				}
				break;
			}
		} catch(StaleProxyException e) {
			e.printStackTrace();
		}
	}

	public void addVehicleAgent(VehicleAgent agent) {
		vehicleAgents.put(agent.getID(), agent);
	}

	public Boolean removeVehicleAgent(String id) {
		VehicleAgent agent = vehicleAgents.get(id);
		if(agent != null) {
			agent.finish();
			arrivedVehicles.add(id);
			return vehicleAgents.remove(id) != null;
		}
		return false;
	}

	public void addTLAgent(TLAgent agent) {
		trafficLightAgents.put(agent.getID(), agent);
	}

	public boolean removeTLAgent(String id) {
		TLAgent agent = trafficLightAgents.get(id);
		if(agent != null) {
			agent.finish();
			return trafficLightAgents.remove(id) != null;
		}
		return false;
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
		try {
			updateAgentObjects();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateAgents();
		
		//updateVehicleStoppedInLanes();
	}

	// Add new vehicles and remove stopped vehicles
	private void updateAgentObjects() throws ControllerException {

		for (String id : SumoCom.arrivedVehicles) {
			removeVehicleAgent(id);
		}
		
		for (SumoVehicle vehicle : SumoCom.vehicles) {
			if (!vehicleAgents.containsKey(vehicle.id) && !this.arrivedVehicles.contains(vehicle.id)) {
				switch (agentMode) {
				case SKIPPER:
					addVehicleAgent(new SkipperVehicleAgent(vehicle.id, this));
					break;
				case DOUBLE_SKIPPER:
					addVehicleAgent(new DoubleSkipperVehicleAgent(vehicle.id, this));
					break;
				case COM:
					ComVehicleAgent agent = new ComVehicleAgent(vehicle.id, this);
					mainContainer.acceptNewAgent(agent.getID(), agent);
					mainContainer.getAgent(vehicle.id).start();
					addVehicleAgent(agent);
					break;
				case LEARNING:
					addVehicleAgent(new LearningVehicleAgent(vehicle.id, this));
					break;
				case BASIC:
				default:
					addVehicleAgent(new BasicVehicleAgent(vehicle.id, this));
					break;
				}
			}
		}
	}

	// Update existing vehicles and traffic lights
	private void updateAgents() {
		for (HashMap.Entry<String, VehicleAgent> entry : vehicleAgents.entrySet()) {
			VehicleAgent vehicle = entry.getValue();
			vehicle.update();
		}
		for (HashMap.Entry<String, TLAgent> entry : trafficLightAgents.entrySet()) {
			TLAgent tl = entry.getValue();
			tl.update();	
		}
	}
	
	public Config getConfig() {
		return config;
	}
	
	public boolean isDebug() {
		return config.debug;
	}
	

	public static Integer getVehiclesStoppedPerLane(String lane) {
		return vehiclesStoppedPerLane.get(lane);
	}
	
	public Collection<VehicleAgent> getVehicles() {
		return vehicleAgents.values();
	}

	public HashMap<String, TLAgent> getTrafficLightAgents() {
		return trafficLightAgents;
	}

	public ArrayList<String> getArrivedVehicles() {
		return arrivedVehicles;
	}
	
	

}
