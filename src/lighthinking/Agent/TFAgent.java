package lighthinking.Agent;

import java.util.ArrayList;

import jade.core.Agent;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoTrafficLightProgram;

public class TFAgent extends Agent{

	String TFId;
	SumoTrafficLight SumoTF;
	SumoTrafficLightProgram TFProgram;


	public TFAgent(String id) {
		super();
		TFId = id;
		
		SumoTF = new SumoTrafficLight(id);

		SumoTF.getControlledLanes();
		TFProgram = SumoTF.getProgram();
	}

	public void update(){

	}
}
