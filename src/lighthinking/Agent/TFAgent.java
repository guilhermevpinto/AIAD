package lighthinking.Agent;

import java.util.ArrayList;

import jade.core.Agent;
import trasmapi.sumo.SumoTrafficLight;

public class TFAgent extends Agent{
	
	String TFId;
	SumoTrafficLight SumoTF;
	

	public TFAgent(String id) {
		super();
		TFId = id;
		
		ArrayList<String>  neighbourEdgeIds = SumoTF.getControlledLanes();
	}
}
