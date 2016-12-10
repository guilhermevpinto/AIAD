package lighthinking.agent.com;

import java.util.ArrayList;
import java.util.HashSet;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public class ComTLAgent extends TLAgent {

	public ComTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.COM;
	}
	
	public void setup() {
		System.out.println("Ready");
	}
	
	public void update(){

		if(this.progMngr.ticksToChangeState() == 1)
		{
			ArrayList<Integer> indexes = this.laneChanging.get(this.getPhaseIndex());
			
			HashSet<String> redlanes = this.getRedLaneIds(indexes);
			
			ArrayList<String> carsID = new ArrayList<>();
			
			for(String lanes : redlanes){
				SumoLane sumolane = new SumoLane(lanes);
				SumoVehicle[] cars = sumolane.vehiclesList();
				for(SumoVehicle car : cars)
					carsID.add(car.id);
			}
			if(internalID.equals("C2"))
				System.out.println("oi" + carsID);
		}
		
		action();
		
		inboxHandler();
		
		super.update();
	}
	
	public void action() {
		HashSet<String> neighbours = new HashSet<>(this.neighbourLights);
		for(String id : neighbours){
				sendMessage(id, "Sender is " + this.internalID);
		}
	}
	
	public void inboxHandler() {
		ACLMessage msg = receive();
		while(msg != null)
		{
			//System.out.println("Message from " + msg.getSender().getName().split("@")[0] + " = " + msg.getContent());
			msg = receive();
		}
	}
	
	protected void sendMessage(String target, String content){
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(target, AID.ISLOCALNAME));
		msg.setContent(content);
		//System.out.println(this.getID() + " is sending message to " + target);
		send(msg);
	}

}
