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
		ACLMessage msg;
		msg = receive();
		if(msg != null){
			String sender = msg.getSender().getName();
			String content = null;
			for(String lane : route)
				if(lane.split("to")[0] == sender)
					content = lane.split("to")[1];
			//TODO retirar
			if(content == null)
				System.out.println("RIP");
			
			ACLMessage answer = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(sender, AID.ISLOCALNAME));
			msg.setContent(content);
		}
		super.update();
	}
}
