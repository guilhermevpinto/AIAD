package test.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import test.Agents.AgentsManager;
import test.Agents.TrafficLightAgentInfo;
import test.Parser.TFAgentInfoParser;
import jade.BootProfileImpl;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

public class Main {
	
	private static ProfileImpl profile;
	private static ContainerController mainContainer;
	
	private static enum Mode {
		FIXED,
		INTERSECTION,
		LEARNING,
		SYNCRONIZED
	}

	// Setup variables
	private static boolean JADE_GUI = false;
	private static Mode mode = Mode.FIXED;
	
	public static void main(String[] args) throws UnimplementedMethod, InterruptedException, IOException, TimeoutException {	
		ArrayList<TrafficLightAgentInfo> tfai = null;
		String type = null;
		
		switch(mode) {
		case FIXED:
			tfai = TFAgentInfoParser.parseTFAgentInfo("res\\OtherMap\\trafficLightInfo.xml");
			type = "FIXED";
			break;
		case INTERSECTION:
			tfai = TFAgentInfoParser.parseTFAgentInfo("res\\OtherMap\\trafficLightInfo.xml");
			type = "INTERSECTION";
			break;
		case LEARNING:
			type = "LEARNING";
			break;
		case SYNCRONIZED:
			type = "SYNCRONIZED";
			break;
		}
		
		// Maybe use JADE GUI
		if(JADE_GUI){
			profile = new BootProfileImpl(new String[] {"-gui"});
		} else {
			profile = new ProfileImpl();
		}

		Runtime rt = Runtime.instance();
		
		// Agent container
		mainContainer = rt.createMainContainer(profile);

		//Create SUMO
		Sumo sumo = new Sumo("guisim");
		List<String> params = new ArrayList<String>();
		params.add("-c=res\\mapaLearning\\file.sumocfg");
		sumo.addParameters(params);
		sumo.addConnections("localhost", 8820);

		// Create TraSMAPI and add Sumo to it
		TraSMAPI trasmapi_api = new TraSMAPI(); 
		trasmapi_api.addSimulator(sumo);
		
		// Start TraSMAPI
		trasmapi_api.launch();
		trasmapi_api.connect();

		AgentsManager manager = null;
		if (type.equals("FIXED") || type.equals("INTERSECTION")) {
			manager = new AgentsManager(sumo,mainContainer, tfai);
		} else {
			manager  = new AgentsManager(sumo, mainContainer, type);
		}
		manager.startupAgents(mainContainer);
		
		trasmapi_api.start();
		
		manager.setBehaviour();

		//simulation loop
		while(true) {
			Thread.sleep(100);
			if(!trasmapi_api.simulationStep(0))
				break;
		}
		
	}	
		
}

