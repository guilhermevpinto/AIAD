package lighthinking.agent.com;

import java.util.ArrayList;
import java.util.HashSet;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;
import trasmapi.sumo.Pair;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public class ComTLAgent extends TLAgent {
	
	/**
	 * <CarID,Lane>
	 */
	ArrayList<Pair<String,String>> carsIncoming;

	public ComTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.COM;
		this.carsIncoming = new ArrayList<>();
	}
	
	public void setup() {
		
	}
	
	public void update(){

		if(this.progMngr.ticksToChangeState() == 1)
		{
			ArrayList<Integer> indexes = this.laneChanging.get(this.getPhaseIndex());
			
			HashSet<String> redlanes = this.getRedLaneIds(indexes);
			
			ArrayList<Pair<SumoVehicle,String>> carsID = new ArrayList<>();
			
			for(String lanes : redlanes){
				SumoLane sumolane = new SumoLane(lanes);
				SumoVehicle[] cars = sumolane.vehiclesList();
				for(SumoVehicle car : cars)
					carsID.add(new Pair(car,lanes));
			}
			//send message to cars so they warn TL where they are going
			if(carsID.size() != 0)
			{
				for(Pair car : carsID)
				{
					SumoVehicle sumocar = (SumoVehicle) car.first();
					sendMessage(sumocar.id,"");
					carsIncoming.remove(car);
				}
			}
		}
		else
		{
			HashSet<String> redlanes = this.getRedLaneIds();
			for(String laneID : redlanes){
				SumoLane sumolane = new SumoLane(laneID);
				SumoVehicle[] cars = sumolane.vehiclesList();
				for(SumoVehicle car : cars)
					if(!carsIncoming.contains(new Pair(car,laneID)))
						carsIncoming.add(new Pair(car,laneID));
			}
		}
		
		inboxHandler();
		
		super.update();
	}
	
	public void inboxHandler() {
		ACLMessage msg = receive();
		while(msg != null)
		{
			
			//if message from semaphore
			if(this.agentManager.trafficLightAgents.containsKey(msg.getSender().getName().split("@")[0])){
				System.out.println("Sender:" + msg.getSender().getName().split("@")[0] + "/Receiver:" + this.internalID + "/content:" + msg.getContent());
				String carID = msg.getContent();
				String lane = msg.getSender().getName().split("@")[0] + "to" + this.internalID;
				carsIncoming.add(new Pair(carID,lane));
			}
			else{
			//if message from car
				System.out.println("AID:" + this.getAID());
				System.out.println("PERF:" + msg.getPerformative());
				System.out.println("Content:" + msg.getContent());
				System.out.println("Cont Sender:" + msg.getSender().getName().split("@")[0]);
				if(this.agentManager.getTrafficLightAgents().containsKey(msg.getSender().getName().split("@")[0]))
					this.sendMessage(msg.getContent(), msg.getSender().getName().split("@")[0]);
			}
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
