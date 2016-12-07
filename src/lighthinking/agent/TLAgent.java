package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoTrafficLightProgram;
import trasmapi.sumo.SumoTrafficLightProgram.Phase;

@SuppressWarnings("serial")
public abstract class TLAgent extends Agent {
	
	protected AgentManager agentManager;
	protected SumoTrafficLight sumoTrafficLight;
	protected SumoTrafficLightProgram sumoTrafficLightProgram;
	
	protected ArrayList<String> controlledLaneIds;
	protected ArrayList<String> neighbourLights;
	
	protected ArrayList<ArrayList<Integer>> laneChanging;
	
	//params
	protected int carsOnGreenLanes = -1;
	protected int carsStopped = -1;
	// new parameters added here must be added to "resetParams()"
	
	public TLAgent(String id, AgentManager mngr) {
		super();
		internalID = id;
		agentManager = mngr;
		
		sumoTrafficLight = new SumoTrafficLight(internalID);
		
		controlledLaneIds = new ArrayList<>(sumoTrafficLight.getControlledLanes());
		
		neighbourLights = new ArrayList<String>();
		for (String l : controlledLaneIds) {
			mngr.addLane(l);
			neighbourLights.add(l.split("to")[0]);
		}
		
		sumoTrafficLightProgram = sumoTrafficLight.getProgram();
		
		this.getLaneChanging();
	}
	
	//Next after yellow light phase
	public String getNextState(){
		List<Phase> phases = sumoTrafficLightProgram.getPhases();
		return phases.get((getPhaseIndex() + 2) % phases.size()).getState();
	}
	
	public String getCurrentState(){
		return sumoTrafficLight.getState();
	}
	
	public int getCarsOnGreenLanes() {
		if(carsOnGreenLanes == -1) {
			carsOnGreenLanes = 0;
			String state = getCurrentState();
			
			HashSet<String> uniqueGreenLanes = new HashSet<>();
			for(int i = 0; i < controlledLaneIds.size(); ++i) {
				char color = state.charAt(i);
				if(color == 'g' || color == 'G') {
					uniqueGreenLanes.add(controlledLaneIds.get(i));
				}
			}
			for(String lane : uniqueGreenLanes) {
				carsOnGreenLanes += VehicleAgent.getVehiclesOnLane(lane, agentManager.getVehicles());
			}
		}
		
		return carsOnGreenLanes;
	}
	
	
	public int getNumberOfStoppedCars(){
		if(carsStopped == -1) {
			carsStopped = 0;
			String state = getCurrentState();
			
			HashSet<String> uniqueRedLanes = new HashSet<>();
			for(int i = 0; i < controlledLaneIds.size(); ++i) {
				char color = state.charAt(i);
				if(color == 'r' || color == 'R') {
					uniqueRedLanes.add(controlledLaneIds.get(i));
				}
			}
			for(String lane : uniqueRedLanes) {
				carsStopped += VehicleAgent.getVehiclesStoppedOnLane(lane, agentManager.getVehicles());
			}
		}
		
		return carsStopped;
	}
	
	@Override
	public void update() {
		resetParams();
	}
	
	public int getPhaseIndex() {
		return TLProgram.indexOfPhase(sumoTrafficLightProgram, sumoTrafficLight.getState());
	}
	
	private void resetParams() {
		carsOnGreenLanes = -1;
		carsStopped = -1;
	}
	
	//get the lanes changing to green at each state
	private void getLaneChanging() {
		List<Phase> phases = sumoTrafficLightProgram.getPhases();
		laneChanging = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < phases.size();i++)
		{
			ArrayList<Integer> changes = new ArrayList<>();
			String currentState = phases.get(i).getState();
			String nextState = phases.get( (i+1) % phases.size()).getState();
			
			for(int j = 0; j < currentState.length(); j++)
			{
				if(currentState.charAt(j) == 'r')
					if(nextState.charAt(j) == 'g' || nextState.charAt(j) == 'G')
						changes.add(j);
			}
			
			laneChanging.add(changes);
		}
	}
	
	public void skipCurrentPhase() {
		getPhaseIndex();
		List<Phase> phases = sumoTrafficLightProgram.getPhases();
		sumoTrafficLightProgram = TLProgram.programSkipPhase(sumoTrafficLightProgram, phases.get(getPhaseIndex()));
		resetParams();
		sumoTrafficLight.setProgram(sumoTrafficLightProgram);
	}
}
