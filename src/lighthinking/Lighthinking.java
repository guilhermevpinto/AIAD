package lighthinking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.BootProfileImpl;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

public class Lighthinking {

	private static final String SUMO_ADDRESS = "localhost";
	private static final int SUMO_PORT = 8820;
	private static final String SUMO_CFG = "res\\Lighthinking\\test\\sumo.cfg";

	private static final int SIMULATION_TICK = 100;

	// Enables or disables the JADE GUI
	private static boolean JADE_GUI = false;

	private static ProfileImpl profile;
	private static ContainerController mainContainer;

	public static void start(Agent.Type agentType) throws IOException, UnimplementedMethod, TimeoutException, InterruptedException {
		// ArrayList<TrafficLightAgentInfo> tfai =
		// TFAgentInfoParser.parseTFAgentInfo(TRAFFIC_LIGHT_INFO_XML);

		// Use the JADE GUI if enabled
		if (JADE_GUI) {
			profile = new BootProfileImpl(new String[] { "-gui" });
		} else {
			profile = new ProfileImpl();
		}

		Runtime rt = Runtime.instance();

		// Agent container
		// mainContainer = rt.createMainContainer(profile);

		// Create SUMO
		Sumo sumo = new Sumo("guisim");
		List<String> params = new ArrayList<String>();
		params.add("-c=" + SUMO_CFG);
		sumo.addParameters(params);
		sumo.addConnections(SUMO_ADDRESS, SUMO_PORT);

		// Agent container
		mainContainer = rt.createMainContainer(profile);

		// Create TraSMAPI and add Sumo to it
		TraSMAPI trasmapi_api = new TraSMAPI();
		trasmapi_api.addSimulator(sumo);

		// Start TraSMAPI
		trasmapi_api.launch();
		trasmapi_api.connect();

		trasmapi_api.start();

		// AgentsManager manager = new AgentsManager(sumo, mainContainer, tfai);
		// manager.startupAgents(mainContainer);

		// manager.setBehaviour();
		AgentManager agentManager = new AgentManager(lighthinking.agent.Agent.Type.REACTION);
		// simulation loop
		while (true) {
			Thread.sleep(SIMULATION_TICK);
			agentManager.updateManager();
			if (!trasmapi_api.simulationStep(0))
				break;
		}
	}

}
