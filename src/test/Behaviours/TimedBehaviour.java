package test.Behaviours;

import java.util.ArrayList;

import test.Agents.TrafficLightAgent;
import jade.core.behaviours.Behaviour;

public class TimedBehaviour extends Behaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int counter;
	private TrafficLightAgent agent;
	ArrayList<Integer> timeStates;
	ArrayList<String> states;

	public TimedBehaviour(TrafficLightAgent ag, ArrayList<Integer> timeStates, ArrayList<String> states){
		this.agent = ag;
		this.counter = 0;
		this.timeStates = timeStates;
		this.states = states;

		System.out.println("Agent " + this.agent.getId() + "|just added TimedBehaviour");
		this.agent.changeState(states.get(0));
	}

	@Override
	public void action() {
		counter = counter + 1;
		String state = this.agent.getStateS();

		for ( int i = 0; i < timeStates.size(); i++){
			if (counter == timeStates.get(i)){
				if( state.toUpperCase().equals(states.get(i).toUpperCase())){
					counter = 0;
					i = (i + 1) % states.size();

					//System.out.println("Changes status " + this.agent.getId());
					this.agent.changeState(states.get(i));
					break;
				}
			}
		}

	}

	@Override
	public boolean done() {
		return false;
	}

}

