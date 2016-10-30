package test.Controllers;

import java.util.ArrayList;
import java.util.Date;

import test.Agents.TrafficLightAgent;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;

public class SyncCtrl implements Runnable{

	private ArrayList<String> neighbours;
	private String name;
	private TrafficLightAgent parentAgent;
	private Sumo sumo;
	
	public SyncCtrl(TrafficLightAgent parentAgent, Sumo sumo, String name, ArrayList<String> neighbours){
		this.parentAgent = parentAgent;
		this.sumo = sumo;
		this.neighbours = neighbours;
		this.name = name;
	}
	
	public ArrayList<String> getNeighbours(){
		return neighbours;
	}

	public String getName(){
		return name;
	}
	
	public TrafficLightAgent getParentAgent() {
		return parentAgent;
	}
	
	@Override
	public void run() {
		
		SumoTrafficLight sumoTrafficLight = new SumoTrafficLight(this.parentAgent.getId());

		while ( true ){

			for(int i = 0; i < neighbours.size(); i++){
				if(sumoTrafficLight.getState().toString().length() == 4){
					parentAgent.SyncInitialization(sumoTrafficLight.getState().toString());
				}
			}

		}
		
	}

}
