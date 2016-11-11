package lighthinking.Agent;

import java.util.ArrayList;
import java.util.HashMap;

import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentManager {

	static HashMap<String,TFAgent> TFAgents = new HashMap<String,TFAgent>();
	static HashMap<String,VehicleAgent> VehicleAgents = new HashMap<String,VehicleAgent>();
	
	public AgentManager(){
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehiclesIds = SumoCom.getAllVehiclesIds();
		
		for(String id : trafficLightIds){
			TFAgents.put(id, new TFAgent(id));
		}
		for(String id : vehiclesIds){
			VehicleAgents.put(id, new VehicleAgent(id));
		}
	}
	
	/**
	 * Adds a vehicle Agent
	 * @param id
	 */
	public static void addVehicleAgent(String id){
		VehicleAgents.put(id, new VehicleAgent(id));
	}
	/**
	 * Removes a vehicle Agent
	 * @param id
	 * @return
	 */
	public static Boolean removeVehicleAgent(String id){
		System.out.println("Acabou");
		return VehicleAgents.remove(id) != null;
	}

	/**
	 * Updates manager, adding new Vehicles, removing arrived ones
	 */
	public static void updateManager() {
		for(SumoVehicle vehicle: SumoCom.vehicles){
			if(!VehicleAgents.containsKey(vehicle.id))
				addVehicleAgent(vehicle.id);
		}
		for(String id : SumoCom.arrivedVehicles){
			removeVehicleAgent(id);
		}
	}
	
}
