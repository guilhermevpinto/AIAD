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
	
	static boolean JADE_GUI = true;
	private static ProfileImpl profile;
	private static ContainerController mainContainer;
	
	//args[0] == 1 -> fixed behaviour
	//args[0] == 2 -> intersection behaviour
	//args[0] == 3 -> learning behaviour
	//args[0] == 4 -> syncronized behaviour
	
	public static void main(String[] args) throws UnimplementedMethod, InterruptedException, IOException, TimeoutException {	
		ArrayList<TrafficLightAgentInfo> tfai = null;
		String type = null;
		args = new String[] { "1" };
		if ( args[0].equals("1")){
			tfai = TFAgentInfoParser.parseTFAgentInfo("src\\test\\OtherMap\\trafficLightInfo.xml");
			type = "FIXED";
		}
		else
			if ( args[0].equals("2")){
				tfai = TFAgentInfoParser.parseTFAgentInfo("src\\test\\OtherMap\\trafficLightInfo.xml");
				type = "INTERSECTION";
			}
			else
				if ( args[0].equals("3"))
					type = "LEARNING";
				else
					if ( args[0].equals("4"))
						type = "SYNCRONIZED";
					else
					{
						System.out.println("Error in arg");
						return;
					}
		
		
		//Init JADE platform w/ or w/out GUI		
		if(JADE_GUI){
			List<String> params = new ArrayList<String>();
			params.add("-gui");
			profile = new BootProfileImpl(params.toArray(new String[0]));
		} else
			profile = new ProfileImpl();

		Runtime rt = Runtime.instance();
		
		//mainContainer - agents' container
		mainContainer = rt.createMainContainer(profile);
		
		//"\uD83D\uDCA9"
		TraSMAPI api = new TraSMAPI(); 

		//Create SUMO
		Sumo sumo = new Sumo("guisim");
		List<String> params = new ArrayList<String>();
		params.add("-c=src\\test\\mapaLearning\\file.sumocfg");
		sumo.addParameters(params);
		sumo.addConnections("localhost", 8820);

		//Add Sumo to TraSMAPI
		api.addSimulator(sumo);
		
		//Launch and Connect all the simulators added
		api.launch();

		api.connect();

		
		//
		AgentsManager manager = null;
		if  (type.equals("FIXED") || type.equals("INTERSECTION"))
				manager = new AgentsManager(sumo,mainContainer, tfai);
		else
			manager  = new AgentsManager(sumo, mainContainer, type);
		
		
		manager.startupAgents(mainContainer);
		
		api.start();
		
		manager.setBehaviour();

		//simulation loop
		while(true) {
			Thread.sleep(100);
			if(!api.simulationStep(0))
				break;
		}
		
		

	}	
		
}

