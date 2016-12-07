package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	protected List<Phase> phases;
	protected ArrayList<ArrayList<Integer>> laneChanging;
	
	
	//params
	protected int index = -1;
	protected String state = null;
	protected int carsOnGreenLanes = -1;
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
		phases = sumoTrafficLightProgram.getPhases();
		
		index = sumoTrafficLight.getCurrentPhaseIndex();
		
		this.getLaneChanging();
	}
	
	public int getIDCurrentPhase(){
		String state = sumoTrafficLight.getState();
		int i = 0;
		for(Phase phase : phases){
			if(phase.getState().equals(state))
				break;
			else i++;
		}
		return i;
	}
	
	//Next after yellow light phase
	public String getNextState(){
		return phases.get((getPhaseIndex() + 2) % this.phases.size()).getState();
	}
	
	public String getCurrentState(){
		if(state != null) {
			state = phases.get(getPhaseIndex()).getState();
		}
		return phases.get(getPhaseIndex()).getState(); 	
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
	
	
	public void getNumberOfStoppedCars(){
//		String currentState = this.getCurrentState();			
//		ArrayList<Integer> returnArray = new ArrayList<>();
//		for(String laneID : controlledLaneIds)
//		{
//			returnArray.add(agentManager.getVehiclesStoppedPerLane(laneID));
//		}
//		return returnArray;
	}
	
	@Override
	public void update() {
		resetParams();
	}
	
	public int getPhaseIndex() {
		if(index == -1) {
			index = sumoTrafficLight.getCurrentPhaseIndex();
			return index;
		}
		return index;
	}
	
	private void resetParams() {
		index = -1;
		state = null;
		carsOnGreenLanes = -1;
	}
	
	//get the lanes changing to green at each state
	private void getLaneChanging() {
		
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
		sumoTrafficLightProgram = TLProgram.programSkipPhase(sumoTrafficLightProgram, phases.get(getPhaseIndex()));
		phases = sumoTrafficLightProgram.getPhases();
		sumoTrafficLight.setProgram(sumoTrafficLightProgram);
	}
}
