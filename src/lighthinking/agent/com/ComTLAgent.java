package lighthinking.agent.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;
import lighthinking.agent.VehicleAgent;
import trasmapi.sumo.Pair;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public class ComTLAgent extends TLAgent {
	
	/**
	 * <CarID,Lane>
	 */
	HashMap<String,String> carsIncoming;

	public ComTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.COM;
		this.carsIncoming = new HashMap<>();
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
					sendMessage(sumocar.id,this.internalID);
					carsIncoming.remove(sumocar.id);
				}
			}
		}
		else
		{
			
			//protocol for 10 ticks
			if(this.progMngr.ticksAfterChange() > 10)
			{
				//System.out.println("carsIncoming" + ":" + this.internalID + ":" + carsIncoming.size());
				ArrayList<Integer> indexes = laneChanging.get((this.getPhaseIndex() + 1) % this.progMngr.getPhases().size());
				int carsInGreen = this.getCarsOnGreenLanes();
				int carsInRed = 0;
				for(Integer index : indexes)
				{
					SumoLane lane = new SumoLane(this.controlledLaneIds.get(index));
					//carsInRed += lane.getNumVehiclesStopped(0.2);
					
					for (HashMap.Entry<String, String> entry : carsIncoming.entrySet()) {
						String laneID = entry.getValue();
						System.out.println("LaneID" + laneID);
						if(laneID.equals(this.controlledLaneIds.get(index)))
							carsInRed++;	
					}
				}
				
				if(carsInRed*2 > carsInGreen){
					this.skipCurrentPhase();
					if(this.internalID.equals("B2"))
						System.out.println("SKIP");
				}
				else 
					if(this.internalID.equals("B2"))
						System.out.println("NO SKIP :" + carsInRed*2 + "/" + carsInGreen);
			}
		}
		
		inboxHandler();
		
		super.update();
	}
	
	public void inboxHandler() {
		ACLMessage msg = this.receive();
		while(msg != null)
		{
			//if message from semaphore
			if(msg.getContent().split("/")[0].equals("sem")){
				String carID = msg.getContent().split("/")[2];
				String lane = msg.getContent().split("/")[1] + "to" + this.internalID;
				carsIncoming.put(carID,lane); 
				if(this.internalID.equals("B2"))
					System.out.println("put a car:" + carID +"/" + carsIncoming.size());
			}
			else if (msg.getContent().split("/")[0].equals("car")){
			//if message from car
				this.sendMessage(msg.getContent().split("/")[1], "sem/"+this.internalID + "/" + msg.getContent().split("/")[1]);
				if(this.internalID.equals("B2"))
					System.out.println("Send Message to: " + msg.getContent().split("/")[1] +  " with: " + this.internalID + "/" + msg.getContent().split("/")[2]);
				
			}
			else 
				System.out.println("peido");
			msg = this.receive();
		}
	}
	
	protected void sendMessage(String target, String content){
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(target, AID.ISLOCALNAME));
		msg.setContent(content);
		send(msg);
	}

}
