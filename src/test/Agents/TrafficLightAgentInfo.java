package test.Agents;

import java.util.ArrayList;

public class TrafficLightAgentInfo {
	private String identifier;
	private int timeGreen;
	private int timeRed;
	private int timeYellow;
	private String beginState;
	ArrayList<String> states;
	ArrayList<Integer> timeStates;
	
	public TrafficLightAgentInfo(String identifier, ArrayList<Integer> timeStates, ArrayList<String> states){
		this.identifier = identifier;
		this.timeStates = timeStates;
		this.states = states;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	
	public ArrayList<Integer> getTimeStates(){
		return timeStates;
	}
	
	public ArrayList<String> getStates(){
		return states;
	}
}
