package test.Behaviours;

import java.util.ArrayList;

import test.Agents.TrafficLightAgent;
import jade.core.behaviours.Behaviour;
import trasmapi.sumo.SumoLane;

public class IntersectionBehaviour extends Behaviour{

	/**
	 *  rgrg
	 *  grgr
	 *  rrrr
	 *  yryr
	 *  ryry
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<String> states;
	TrafficLightAgent agent;
	ArrayList<Integer> times;
	private final int MAXCARS = 10;
	ArrayList<String> controlledLanes;
	ArrayList<Integer> NumCarrosParados;
	private final int DELTA = 3;
	int counter;
	int timer;

	public IntersectionBehaviour(TrafficLightAgent ag, ArrayList<Integer> timeStates,ArrayList<String> st) {
		this.states = st;
		this.agent = ag;
		this.times = timeStates;
		this.states = st;
		counter = 0;
		timer = 0;
		controlledLanes = ag.getControlledLanes();
		NumCarrosParados = new ArrayList<Integer>(controlledLanes.size());
	}

	@Override
	public void action() {

		counter = counter + 1;
		String state = this.agent.getStateS();


		for ( String s : controlledLanes){
			SumoLane sl = new SumoLane(s);	
			NumCarrosParados.add(new Integer( sl.getNumVehiclesStopped(0.2)));
		}

		double maxV = Math.max(NumCarrosParados.get(0), NumCarrosParados.get(2));
		double maxH = Math.max(NumCarrosParados.get(1), NumCarrosParados.get(3));

		if ( maxV > ( maxH + DELTA )){		
			counter = 0;
			if  (state.equals("rgrg"))
			{
				this.agent.changeState("ryry");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.agent.changeState("rrrr");
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				this.agent.changeState("grgr");
			}
			else
				if (state.equals("ryry"))
				{
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.agent.changeState("rrrr");
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					this.agent.changeState("grgr");
					
				}
				else
					if (state.equals("rrrr"))
					{
						this.agent.changeState("grgr");
					}
		}
		else
			if ( maxV < ( maxH + DELTA )){
				counter = 0;
				if  (state.equals("grgr"))
				{
					this.agent.changeState("yryr");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.agent.changeState("rrrr");
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					this.agent.changeState("rgrg");
				}
				else
					if (state.equals("yryr"))
					{
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						this.agent.changeState("rrrr");
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						this.agent.changeState("rgrg");
						
					}
					else
						if (state.equals("rrrr"))
						{
							this.agent.changeState("rgrg");
						}
			}
			else{
				for ( int i = 0; i < times.size(); i++){
					if (counter == times.get(i)){
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
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}



}

