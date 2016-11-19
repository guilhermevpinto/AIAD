package lighthinking.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import lighthinking.behavior.ReactionTFAgent;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentManager {

	private HashMap<String,TFAgent> TFAgents = new HashMap<String,TFAgent>();
	private HashMap<String,VehicleAgent> VehicleAgents = new HashMap<String,VehicleAgent>();
	private HashMap<String, SumoLane> lanes = new HashMap<String,SumoLane>();
	public static HashMap<String, Integer> vehStoppedPerLane = new HashMap<String,Integer>();
	public static HashSet<String> laneIDs = new HashSet<>();
	
	public static enum AgentType{
		REACTION
	}

	public AgentManager(AgentType mode){
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehiclesIds = SumoCom.getAllVehiclesIds();
		

		for(String id : trafficLightIds){
			switch(mode)
			{
			case REACTION: 
				TFAgents.put(id, new ReactionTFAgent(id));
				break;
			default:
				TFAgents.put(id, new TFAgent(id));
				break;
			}
		}
		for(String id : vehiclesIds){
			VehicleAgents.put(id, new VehicleAgent(id));
		}
		
		for(String ids : laneIDs)
		{
			vehStoppedPerLane.put(ids, 0);
		}
	}

	/**
	 * Adds a vehicle Agent
	 * @param id
	 */
	public void addVehicleAgent(String id){
		VehicleAgents.put(id, new VehicleAgent(id));
	}
	/**
	 * Removes a vehicle Agent
	 * @param id
	 * @return
	 */
	public Boolean removeVehicleAgent(String id){
		return VehicleAgents.remove(id) != null;
	}

	/**
	 * Updates manager, adding new Vehicles, removing arrived ones
	 */
	public synchronized void updateManager() {

		//adding and removing vehicles
		for(SumoVehicle vehicle: SumoCom.vehicles){
			if(!VehicleAgents.containsKey(vehicle.id))
				addVehicleAgent(vehicle.id);
		}
		for(String id : SumoCom.arrivedVehicles){
			removeVehicleAgent(id);
		}
		
		//get number of cars stopped by lane
	
		getVehicleStoppedInLanes();

		//update each vehicle and TF
		for (HashMap.Entry<String, VehicleAgent> entry : VehicleAgents.entrySet())
		{
			VehicleAgent vehicle = entry.getValue();
			vehicle.update();
		}
		for (HashMap.Entry<String, TFAgent> entry : TFAgents.entrySet())
		{
			TFAgent TF = entry.getValue();
			TF.update();
		}
	}
	
	public void getVehicleStoppedInLanes(){
		for(String id : laneIDs)
			if(!vehStoppedPerLane.containsKey(id))
				vehStoppedPerLane.put(id,lanes.get(id).getNumVehiclesStopped(0.2));
	}
	
	public static void addLane(String id){
		laneIDs.add(id);
	}

}
