package lighthinking;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jade.BootProfileImpl;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import test.Agents.AgentsManager;
import test.Agents.TrafficLightAgentInfo;
import test.Parser.TFAgentInfoParser;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

public class Main {

	private static final String SUMO_ADDRESS = "localhost";
	private static final int SUMO_PORT = 8820;
	private static final String SUMO_CFG = "src\\test\\mapaLearning\\file.sumocfg";
	
	private static ProfileImpl profile;
	private static ContainerController mainContainer;

	// Setup variables
	private static boolean JADE_GUI = false;
	
	public static void main(String[] args) throws UnknownHostException, IOException, TimeoutException, UnimplementedMethod, InterruptedException {
		ArrayList<TrafficLightAgentInfo> tfai = TFAgentInfoParser.parseTFAgentInfo("res\\OtherMap\\trafficLightInfo.xml");
		String type = "FIXED";
		
		// Maybe use JADE GUI
		if (JADE_GUI) {
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
		params.add("-c=" + SUMO_CFG);
		sumo.addParameters(params);
		sumo.addConnections(SUMO_ADDRESS, SUMO_PORT);

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
