package lighthinking.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		return VehicleAgents.remove(id) != null;
	}

	/**
	 * Updates manager, adding new Vehicles, removing arrived ones
	 */
	public synchronized  static void updateManager() {

		//adding and removing vehicles
		for(SumoVehicle vehicle: SumoCom.vehicles){
			if(!VehicleAgents.containsKey(vehicle.id))
				addVehicleAgent(vehicle.id);
		}
		for(String id : SumoCom.arrivedVehicles){
			removeVehicleAgent(id);
		}

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

}
