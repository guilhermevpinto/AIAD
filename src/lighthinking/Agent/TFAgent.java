package lighthinking.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.Agent;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoTrafficLightProgram;

public class TFAgent extends Agent{

	String TFId;
	SumoTrafficLight SumoTF;
	SumoTrafficLightProgram TFProgram;
	
	HashSet<String> iDLanes;
	HashMap<String, SumoLane> lanes;


	public TFAgent(String id) {
		super();
		TFId = id;
		
		SumoTF = new SumoTrafficLight(TFId);
		
		iDLanes = new HashSet<>(SumoTF.getControlledLanes());
		
		lanes = new HashMap<>();
		
		for(String idlane : iDLanes)
			lanes.put(idlane, new SumoLane(idlane));
		
		ArrayList<String> neighbours = new ArrayList<String>();
		for (String l : iDLanes) {
			neighbours.add(l.split("to")[0]);
		}
		
		for(String t : neighbours)
			System.out.println("Neigbour of " + TFId + ": " + t);
		
	}

	public void update(){
//		System.out.println("Duration of "+ TFId.split("to")[0] +": " + SumoTF.getTimeToNextSwitch());
//		HashMap<String,Integer> vehStoppedPerLane = getVehicleStoppedInLanes();
	}
	

	public HashMap<String,Integer> getVehicleStoppedInLanes(){
		HashMap<String,Integer> vehStoppedPerLane = new HashMap<>();
		for(String id : iDLanes)
			if(!vehStoppedPerLane.containsKey(id))
				vehStoppedPerLane.put(id,lanes.get(id).getNumVehiclesStopped(0.0));
		return vehStoppedPerLane;
	}
}
