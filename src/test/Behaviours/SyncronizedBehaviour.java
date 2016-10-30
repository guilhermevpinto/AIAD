package test.Behaviours;

import java.io.IOException;
import java.util.ArrayList;

import test.Agents.TrafficLightAgent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class SyncronizedBehaviour extends Behaviour {

	/*
	 * rgr
	 * grr
	 * rrg
	 * yry
	 * ryy
	 * yyr
	 * rrr
	 * */
	
	TrafficLightAgent agent;
	ArrayList<String> states;
	int turner = 0;

	public void changeLigth(String content){
		if(content.equals("rgrg") || content.equals("yryr")){
			//horizontais verdes
			// 1 e 3
			this.agent.changeState("ryr");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.agent.changeState("rrr");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			this.agent.changeState("grg");
		}else{
			if(turner == 0){
				// 2 e 3
				this.agent.changeState("yrr");
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.agent.changeState("rrr");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				this.agent.changeState("rgg");
				
				turner=1;
			}else{
				//2 e 1
				this.agent.changeState("rry");
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.agent.changeState("rrr");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				this.agent.changeState("ggr");
				
				turner = 0;
			}
		}
	}
	
	@Override
	public void action() {
		try{
			System.in.read();
		}catch (IOException e) {
			e.printStackTrace();
		};
		
		ACLMessage msg = agent.receive();
		ACLMessage reply;
		String sender = null;
		String content = null;
		int reward;
		String laneForReward = null;
		String neighbour = null;
		Boolean ignore = false;
		
		if(msg != null){
			reply = msg.createReply();
			sender = msg.getSender().getLocalName();
				if ( msg.getPerformative() == ACLMessage.INFORM ){
					content = msg.getContent();

					if ( content != null ){
						//if message is not related with emergency then it is reward information
						if ( content.indexOf("verde") != -1 || content.indexOf("vermelho") != -1){
							System.out.println("Agent " + agent.getLocalName() + " received message from " + sender);
							changeLigth(content);
						}
					}
				}else{
					System.out.println("Agent " + agent.getLocalName() + " message not admissible [ " +  msg.getPerformative() + " ] from " + sender);
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Message not admissible " + msg.getPerformative());
				}
			
			agent.send(reply);
		}else block();
		
	}
	
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
