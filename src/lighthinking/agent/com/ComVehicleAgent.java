package lighthinking.agent.com;

import java.util.ArrayList;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lighthinking.agent.AgentManager;
import lighthinking.agent.VehicleAgent;

@SuppressWarnings("serial")
public class ComVehicleAgent extends VehicleAgent {
	
	protected ArrayList<String> route;
	
	public ComVehicleAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.COM;
		this.setup();
	}
	
	public void setup() {
		this.route = sumoVehicle.getEdges();
	}
	
	public void update(){
		super.update();
		
		if(alive){
			ACLMessage msg;
			msg = this.receive();
			if(msg != null){
				String sender = msg.getSender().getName();
				String content = msg.getContent();
				System.out.println("Message Received from: TL" + content);
				for(String lane : route){
					if(lane.split("to")[0].equals(sender.split("@")[0]))
						content = lane.split("to")[1];
				}
				
				ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
				answer.addReceiver(new AID(sender.split("@")[0], AID.ISLOCALNAME));
				answer.setContent("car/" + content + "/" + this.internalID);
				System.out.println("Send car" + this.internalID + " to " + sender.split("@")[0]);
				send(answer);
			}
		}
	}
}
