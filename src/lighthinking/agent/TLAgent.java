package lighthinking.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoTrafficLightProgram;
import trasmapi.sumo.SumoTrafficLightProgram.Phase;

@SuppressWarnings("serial")
public abstract class TLAgent extends Agent {

	String TFId;
	SumoTrafficLight SumoTF;
	SumoTrafficLightProgram TFProgram;
	
	HashSet<String> iDLanes;
	HashMap<String, SumoLane> lanes;
	ArrayList<String> TFneighbours;
	
	List<Phase> phases;
	
	static HashMap<String,Integer> vehStoppedPerLane;


	public TLAgent(String id) {
		super();
		TFId = id;
		
		SumoTF = new SumoTrafficLight(TFId);
		
		iDLanes = new HashSet<>(SumoTF.getControlledLanes());
		
		ArrayList<String> TFneighbours = new ArrayList<String>();
		for (String l : iDLanes) {
			AgentManager.addLane(l);
			TFneighbours.add(l.split("to")[0]);
		}
		
		SumoTrafficLightProgram TFProgram = SumoTF.getProgram();
		
		phases = TFProgram.getPhases();
		
		System.out.println(phases);
	}
	
	public int getIDCurrentPhase(){
		String state = SumoTF.getState();
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
