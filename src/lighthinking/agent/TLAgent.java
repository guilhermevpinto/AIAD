package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	protected HashSet<String> controlledLaneIds;
	protected ArrayList<String> neighbourLights;
	
	protected List<Phase> phases;
	
	protected HashMap<String,Integer> vehiclesStoppedPerLane;

	
	
	public TLAgent(String id, AgentManager mngr) {
		super();
		internalID = id;
		agentManager = mngr;
		
		sumoTrafficLight = new SumoTrafficLight(internalID);
		
		controlledLaneIds = new HashSet<>(sumoTrafficLight.getControlledLanes());
		
		neighbourLights = new ArrayList<String>();
		for (String l : controlledLaneIds) {
			AgentManager.addLane(l);
			neighbourLights.add(l.split("to")[0]);
		}
		
		sumoTrafficLightProgram = sumoTrafficLight.getProgram();
		phases = sumoTrafficLightProgram.getPhases();
		
		System.out.println(phases);
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
	
	public String getNextState(int i){
		return phases.get(i).getState();
	}
}
