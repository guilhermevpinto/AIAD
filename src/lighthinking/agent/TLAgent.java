package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	protected int index = -1;

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
		
		//System.out.println(phases);
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
	
	public String getNextState(){
		return phases.get((index + 1) % this.phases.size()).getState();
	}
	
	public String getCurrentState(){
		return phases.get(index).getState(); 	
	}
	
	public ArrayList<Integer> getNumberOfStoppedCars(){
		ArrayList<Integer> returnArray = new ArrayList<>();
		for(String laneID : controlledLaneIds)
		{
			returnArray.add(agentManager.getVehiclesStoppedPerLane(laneID));
		}
		return returnArray;
	}
	
	@Override
	public void update() {
		int newIndex;
		if((newIndex = sumoTrafficLight.getCurrentPhaseIndex()) != index)
			index = newIndex;
	}
	
	
}
