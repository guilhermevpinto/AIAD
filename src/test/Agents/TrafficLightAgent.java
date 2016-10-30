package test.Agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import test.Behaviours.IntersectionBehaviour;
import test.Behaviours.RewardLearningBehaviour;
import test.Behaviours.TimedBehaviour;
import test.Behaviours.SyncronizedBehaviour;
import test.Controllers.TrafficLightCtrl;
import test.Learning.QLearning;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.protocol.Constants;

public class TrafficLightAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TrafficLightAgentInfo tfai;
	private String identifier;
	private Sumo sumo;
	private ArrayList<String> neighbours;
	private SumoTrafficLight stf;
	private TrafficLightCtrl controller;
	private int intersectionsNum;
	private int states;
	private int actions;
	QLearning learn;
	TLState st;
	String type;

	public TrafficLightAgent(Sumo sumo, String id, ArrayList<String> neighbour, String type) {
		super();
		this.identifier = id;
		this.sumo = sumo;
		this.neighbours = neighbour;
		this.stf = new SumoTrafficLight(identifier);
		this.intersectionsNum = neighbour.size();
		this.type = type;
		states = (int) Math.pow(TLState.getStatesPerLight(), intersectionsNum);
		actions = (int) Math.pow(TLState.getActionPerLight(), intersectionsNum);

		if ( type.equals("LEARNING"))
			learn = new QLearning(identifier, states, actions);
		st = new TLState(identifier, intersectionsNum, states);
		if ( type.equals("LEARNING"))
			controller = new TrafficLightCtrl(this, sumo, identifier, neighbours, st.getGreenTime());

		// cleans names of neighbours and removes if is not a traffic light
		ArrayList<String> temp = SumoTrafficLight.getIdList();

		for (int i = 0; i < this.neighbours.size(); i++) {
			if (Arrays.asList(temp.toArray()).contains((this.neighbours).get(i))) {
				this.neighbours.set(i, "TrafficLght-" + this.neighbours.get(i));
			} else {
				// if neighbour is not considered a traffic light
				this.neighbours.remove(i);
				i--;
			}
		}

		Date date = new Date();
		System.out.println(date + " - New TrafficLightAgent named " + this.identifier);
	}

	
	public TrafficLightAgent(Sumo sumo, String id, ArrayList<String> neighbour,TrafficLightAgentInfo tfai ) {
		super();
		this.identifier = id;
		this.sumo = sumo;
		this.neighbours = neighbour;
		this.stf = new SumoTrafficLight(identifier);
		this.intersectionsNum = neighbour.size();
		this.tfai = tfai;


		// cleans names of neighbours and removes if is not a traffic light
		ArrayList<String> temp = SumoTrafficLight.getIdList();

		for (int i = 0; i < this.neighbours.size(); i++) {
			if (Arrays.asList(temp.toArray()).contains((this.neighbours).get(i))) {
				this.neighbours.set(i, "TrafficLght-" + this.neighbours.get(i));
			} else {
				// if neighbour is not considered a traffic light
				this.neighbours.remove(i);
				i--;
			}
		}

		Date date = new Date();
		System.out.println(date + " - New TrafficLightAgent named " + this.identifier);
	}
	
	public TLState getTLState() {
		return st;
	}

	/**
	 * 
	 * @return
	 */
	public QLearning getLearning() {
		return learn;
	}

	/**
	 * 
	 * @return
	 */
	public TrafficLightCtrl getTrafficLightCtrl() {
		return controller;
	}

	public String getId() {
		return identifier;
	}

	public void changeState(String state) {
		stf.setState(state);
	}

	public String getStateS() {
		return stf.getState();
	}

	public int getState() {
		
		  String s = stf.getState(); if ( s.equals("r")) return
		  Constants.TLPHASE_RED; else if ( s.equals("g")) return
		  Constants.TLPHASE_GREEN; else if ( s.equals("y")) return
		  Constants.TLPHASE_YELLOW; else return -1;
		 
		//return -1;
	}

	public ArrayList<String> getControlledLanes() {
		return stf.getControlledLanes();
	}

	/**
	 * Sends message to neighbours alerting of emergency
	 */
	public void alertNeighbourOfEmergency() {
		for (int i = 0; i < neighbours.size(); i++) {
			Date date = new Date();
			System.out.println(date + " - " + identifier + " : Warned neighbour about emergency: " + neighbours.get(i));
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(new AID(neighbours.get(i), AID.ISLOCALNAME));
			request.setContent("emergency " + identifier);
			date = new Date();
			System.out.println(date + " - " + identifier + " : Sent emergency request to " + neighbours.get(i));
			send(request);
		}

	}

	/**
	 * Sends messages to neighbours asking for reward
	 */
	public void requestReward() {

		for (int i = 0; i < neighbours.size(); i++) {

			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(new AID(neighbours.get(i), AID.ISLOCALNAME));
			request.setContent("reward");
			Date date = new Date();
			System.out.println(date + " - " + identifier + " : sent reward request to " + neighbours.get(i));
			send(request);
		}
	}
	
	/**
	 * Sends messages to neighbours sending trafficLight status
	 */
	public void SyncInitialization(String contentToSend) {

		for (int i = 0; i < neighbours.size(); i++) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReplyTo(new AID(neighbours.get(i), AID.ISLOCALNAME));
			msg.setContent(contentToSend);
			Date date = new Date();
			System.out.println(date + " - " + identifier + " : sync start message sent to: " + neighbours.get(i));
			send(msg);
		}
	}

	@Override
	protected void setup() {

		// register with the DF
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();

		sd.setType("TrafficLightAgent");
		sd.setName(getName());
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
			if ( type.equals("LEARNING")) {
				RewardLearningBehaviour be = new RewardLearningBehaviour(this);
				this.addBehaviour(be);
			}
			else
				if (type.equals("FIXED")){
					TimedBehaviour be = new TimedBehaviour(this, tfai.getTimeStates(), tfai.getStates());
					this.addBehaviour(be);
				}
				else
					if ( type.equals("INTERSECTION")){
						IntersectionBehaviour be = new IntersectionBehaviour(this, tfai.getTimeStates(), tfai.getStates());
						this.addBehaviour(be);
					}
					else if( type.equals("SYNCRONIZED") ){
						SyncronizedBehaviour be = new SyncronizedBehaviour();
						this.addBehaviour(be);
					}
			Date date = new Date();
			System.out.println(date + " - " + identifier + " : added Reward and Learning Behaviour");
		} catch (FIPAException e) {
			e.printStackTrace();
			Date date = new Date();
			System.out.println(date + " - " + identifier + " Could not register");
		}

		new Thread(controller).start();
		super.setup();
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
