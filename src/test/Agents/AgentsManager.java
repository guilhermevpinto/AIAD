package test.Agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import test.Behaviours.TimedBehaviour;
import test.Controllers.EmergencyChecker;
import test.Statistics.Statistics;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoVehicle;

public class AgentsManager extends Agent{
	ArrayList<TrafficLightAgent> agents = new ArrayList<TrafficLightAgent>();
	private Sumo sumo;
	ArrayList<TrafficLightAgentInfo> tfai;
	private final int numDrivers = 500;
	private Statistics statistics;
	private String type;
	
	private boolean statistics_enabled = false;
	
	public AgentsManager(Sumo sumo, ContainerController mainController, String type) {
		//gets all traffic lights in the sumo simulation
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehicles = SumoCom.getAllVehiclesIds();
		this.type = type;
		
		TrafficLightAgent agent;
		statistics = new Statistics(sumo);
		
		//this.tfai = tfai;
		
		for ( String name : vehicles){
			
			addDrivers(mainController, Integer.parseInt(name));
		}
		
		for (String id : trafficLightIds) {
			SumoTrafficLight temp = new SumoTrafficLight(id);
			
			HashSet<String> lanes;
			lanes = new HashSet<String>(temp.getControlledLanes());
			
			ArrayList<String> neighbours = new ArrayList<String>();
			for (String l : lanes) {
				neighbours.add(getLaneSrcFromId(l));
			}
			
			neighbours = organizeNeighbours(id, neighbours);
			
			try {
				agent = new TrafficLightAgent(sumo, id, neighbours, type);
				agents.add(agent);
				AgentController t = mainController.acceptNewAgent("TrafficLight-" + id, agent);
				mainController.getAgent(agent.getLocalName(),AID.ISLOCALNAME);
			}catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if(statistics_enabled) {
			new Thread(statistics).start();
		}
	}

	
	public AgentsManager(Sumo sumo, ContainerController mainController, ArrayList<TrafficLightAgentInfo> tfai) {
		//gets all traffic lights in the sumo simulation
		ArrayList<String> trafficLightIds = SumoTrafficLight.getIdList();
		ArrayList<String> vehicles = SumoCom.getAllVehiclesIds();
		
		TrafficLightAgent agent;
		statistics = new Statistics(sumo);
		
		this.tfai = tfai;
		
		
		for ( String name : vehicles){
			
			addDrivers(mainController, Integer.parseInt(name));
		}
		
		for (String id : trafficLightIds) {
			SumoTrafficLight temp = new SumoTrafficLight(id);
			
			HashSet<String> lanes;
			lanes = new HashSet<String>(temp.getControlledLanes());
			
			ArrayList<String> neighbours = new ArrayList<String>();
			for (String l : lanes) {
				neighbours.add(getLaneSrcFromId(l));
			}
			
			neighbours = organizeNeighbours(id, neighbours);
			
			TrafficLightAgentInfo tempTfai = null;
			for (int j = 0; j < tfai.size(); j++){
				if ( tfai.get(j).getIdentifier().equals(id)){
					
					tempTfai = tfai.get(j);
				}
					
			}
			
			try {
				agent = new TrafficLightAgent(sumo, id, neighbours,tempTfai);
				agents.add(agent);
				AgentController t = mainController.acceptNewAgent("TrafficLight-" + id, agent);
				mainController.getAgent(agent.getLocalName(),AID.ISLOCALNAME);
			}catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if(statistics_enabled) {
			new Thread(statistics).start();
		}
	}

	
	
	private ArrayList<String> organizeNeighbours(String id, ArrayList<String> neighbours) {
		ArrayList<String> ordered = new ArrayList<String>();
		String[] splitId = id.split("/");
		int column = Integer.parseInt(splitId[0]);
		int line = Integer.parseInt(splitId[1]);
		String uppNei = column + "/" + Integer.toString(line + 1);
		String rightNei = Integer.toString(column + 1) + "/" + line;
		String downNei = column + "/" + Integer.toString(line - 1);
		String leftNei = Integer.toString(column-1) + "/" + line;
		
		if ( neighbours.contains(uppNei))
			ordered.add(uppNei);
		
		if ( neighbours.contains(rightNei))
				ordered.add(rightNei);
		
		if ( neighbours.contains(downNei))
			ordered.add(downNei);
		
		if ( neighbours.contains(leftNei))
			ordered.add(leftNei);
		
		return ordered;
 	}

	
	public void addDrivers(ContainerController mainController, int id){

		try {
			mainController.acceptNewAgent("DRIVER#"+id, new DriverAgent(id)).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

	}
	
	public void setBehaviour() {
		
		/*ArrayList<Integer> timeStates = new ArrayList<Integer>();
		ArrayList<String> states = new ArrayList<String>();
		TimedBehaviour tb;
		for ( TrafficLightAgent tl : agents){
			for ( TrafficLightAgentInfo info : tfai){
				if ( info.getIdentifier().equals(tl.getId())){
					System.out.println("WHAT IS THE ID " + tl.getId());
					timeStates = info.getTimeStates();
					states = info.getStates();
					tb = new TimedBehaviour(tl,timeStates, states);
					tl.addBehaviour(tb);
				}
				
			}
			
		}*/
		
	}
	
	public void startupAgents(ContainerController mainController) {
		try {
			for ( TrafficLightAgent ag : agents) {
				Date date = new Date();
				String temp = ag.getLocalName().split("-")[1];
				System.out.println(date + " - New agent " + temp);
				mainController.getAgent(ag.getLocalName(),AID.ISLOCALNAME).start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	//returns the src of a lane
	//srcToDest
	private static String getLaneSrcFromId(String l) {
		 return l.split("to")[0];
	}
	
	/**
	 * 
	 */
	protected void setup(){
	    DFAgentDescription ad = new DFAgentDescription();
        ad.setName(getAID()); //agentID
        Date date = new Date();
        System.out.println(date + " - AID: " + ad.getName());

        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName()); //nome do agente
        date = new Date();
        System.out.println(date + " - Nome: " + sd.getName());

        sd.setType("Manager");
        date = new Date();
        System.out.println(date + " - Tipo: " + sd.getType() + "\n\n\n");

        ad.addServices(sd);

        try {
            DFService.register(this, ad);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        super.setup();
	}
	
	  @Override
	    protected void takeDown() {
	        try {
	            DFService.deregister(this);
	        } catch (FIPAException e) {
	            e.printStackTrace();
	        }
	        super.takeDown();
	    }
}
