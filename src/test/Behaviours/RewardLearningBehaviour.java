package test.Behaviours;

import java.io.IOException;

import test.Agents.TrafficLightAgent;
import test.Controllers.TrafficLightCtrl;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RewardLearningBehaviour extends CyclicBehaviour{

	TrafficLightAgent agent;
	public RewardLearningBehaviour(TrafficLightAgent ag) {
		super(ag);

		this.agent = ag;
	}

	@Override
	public void action() {

		//gets message from other agents
		System.out.println(agent.getLocalName());
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		int next;


		if ( msg != null) {
			reply = msg.createReply();

			sender = msg.getSender().getLocalName();

			if ( msg.getPerformative() == ACLMessage.REQUEST){
				content = msg.getContent();

				if ( content != null){

					//if agent receives message asking for reward
					if ( content.indexOf("reward") != -1){
						System.out.println("Agent " + agent.getLocalName() + " received reward request from " + sender);
						System.out.println("SENDED : " + sender);
						laneForReward = agent.getName().substring(13, 16) + "to" + sender.substring(13, 16) + "_0";
						reward = agent.getTrafficLightCtrl().getRewardLane(laneForReward);
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent(Integer.toString(reward));
						System.out.println("Agent " + agent.getLocalName() + " sent reward of " + reward + " to " + sender);
					}
					else
						//if agent receives message of emergency
						if ( content.indexOf("emergency") != -1 ){
							System.out.println("Agent " + agent.getLocalName() + " received emergency request from " + sender);
							//gets the neighbour that has the emergency
							neighbour = content.substring(10);
							ignore = !agent.getTrafficLightCtrl().actionEmergency(neighbour);

							if ( ignore ){
								reply.setContent("emergency ignored");
							}
							else {
								reply.setContent("emergency received");
							}

							reply.setPerformative(ACLMessage.INFORM);
						}		
				}
				else {
					System.out.println("Agent " + agent.getLocalName() + " message not admissible [ " +  content + " ] from " + sender);
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("Message not admissible");
				}
			}
			else
				//if agent receives inform message
				if ( msg.getPerformative() == ACLMessage.INFORM ){
					content = msg.getContent();

					if ( content != null ){
						//if message is not related with emergency then it is reward information
						if ( content.indexOf("emergency received") == -1 && content.indexOf("emergency ignored") == -1){
							reward = Integer.parseInt(content);
							System.out.println("Agent " + agent.getLocalName() + " received reward of " + reward + "  from " + sender);
							agent.getLearning().reinforce(agent.getTLState(), reward);
							next = agent.getLearning().getActionToTake(agent.getTLState().getState());
							agent.getTLState().applyAction(next);
							agent.getTrafficLightCtrl().setGreenTime(agent.getTLState().getGreenTime());
						}
					}
				}else
				{
					System.out.println("Agent " + agent.getLocalName() + " message not admissible [ " +  msg.getPerformative() + " ] from " + sender);
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Message not admissible " + msg.getPerformative());
				}

			agent.send(reply);
		}
		// if msg is null
		else
		{
			block();
		}

	}

}
