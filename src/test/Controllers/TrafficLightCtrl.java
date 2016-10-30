package test.Controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import test.Agents.TrafficLightAgent;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;

/**
 * 
 * @author Liliana
 *
 */
public class TrafficLightCtrl implements Runnable{

	private double LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB;
	private ArrayList<String> neighbours;
	private String name;
	private TrafficLightAgent parentAgent;
	private int emergencyIndex = -1;
	private Sumo sumo;
	private int[] greenTimes; // saves the max time that a traffic light stays in green

	/**
	 * 
	 * @param par
	 * @param sumo
	 * @param name
	 * @param neighbours
	 * @param greentime
	 */
	public TrafficLightCtrl(TrafficLightAgent par, Sumo sumo, String name, ArrayList<String> neighbours, int[] greentime){

		//Sets the probability for the agent to listen to others emergency, based on the number of neighbours
		//TODO increqase probability???
		this.LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB = 1.0 / (float) neighbours.size();

		this.parentAgent = par;
		this.sumo = sumo;
		this.neighbours = new ArrayList<String>(neighbours);
		this.greenTimes = greentime;
		this.name = name;

		//starts the thread that checks for neighbours emergency
		//
		new Thread(new EmergencyChecker(this)).start();

	}

	/**
	 * 
	 * @param ne
	 */
	public void setGreenTime(int[] ne){
		//Sync is needed because of the use of multiple threads accessing this variable
		synchronized (this.greenTimes) {
			this.greenTimes = ne.clone();
		}
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getNeighbours(){
		return neighbours;
	}

	/**
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public int getEmergencyIndex() {
		return emergencyIndex;
	}

	/**
	 * 
	 * @param i
	 */
	public void setEmergencyIndex(int i) {
		emergencyIndex = i;

	}

	/**
	 * 
	 * @return
	 */
	public TrafficLightAgent getParentAgent() {
		return parentAgent;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		int intersectionNumber = this.neighbours.size();
		SumoTrafficLight sumoTrafficLight = new SumoTrafficLight(this.parentAgent.getId());
		int previous = -1;
		Boolean run = true;
		int YellowTime = 5;
		int step = 1000;
		int greenTemp;
		int initPhase;
		int endPhase;

		while ( run ){

			for ( int i = 0; i < intersectionNumber; i++){

				//needs to be sync because of threads
				synchronized (this.greenTimes) {
					//green time of current traffic light
					greenTemp = greenTimes[i];
				}

				//traffic light start green
				String stat = buildState(i, "G");
				//changes the current state of traffic light
				
				//Debug 
				Date date = new Date();
				System.out.println(date + " - OLD " + sumoTrafficLight.getState());
				date = new Date();
				System.out.println(date + " - New" + stat);
				date = new Date();
				if ( stat.length() == sumoTrafficLight.getState().length())
					System.out.println(date  + " - EQUALS!!!!" + stat.length() + " " + sumoTrafficLight.getState().length());
				else
					System.out.println(date + " - DIFFERENT!!!!" + stat.length() + " " + sumoTrafficLight.getState().length());

				sumoTrafficLight.setState(stat);
				date = new Date();
				System.out.println(date + " - " + name + " : Changed to " + stat + " for " + greenTemp + " ticks");

				//get current step of simulation
				initPhase = sumo.getCurrentSimStep() / step;
				endPhase = initPhase;

				//waits until green time is over or emergency is received
				while ( greenTemp > (endPhase - initPhase + YellowTime)){

					//if emergency is received and this was not the traffic light that sent the emergency
					if ( emergencyIndex != -1 && emergencyIndex != i){
						break;
					}

					try {
						Thread.sleep(1);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					//updates the actual step of the simulation
					endPhase = sumo.getCurrentSimStep() / step;
				}

				//next state will be yellow
				stat = buildState(i, "y");
				date = new Date();
				System.out.println(date + " - " + name + " : Changed to " + stat + " for " + YellowTime + " ticks");
				sumoTrafficLight.setState(stat);

				initPhase = sumo.getCurrentSimStep() / step;
				endPhase = initPhase;

				//waits yellow time
				while ( YellowTime > (endPhase - initPhase)){
					try{
						Thread.sleep(1);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					endPhase = sumo.getCurrentSimStep() / step;
				}


				//if there is an emergency
				if ( emergencyIndex != -1){
					//if traffic light was in emergency state
					if  (emergencyIndex == i){
						emergencyIndex = -1;
						if ( previous != -1 ){
							i = previous - 1;
							previous = -1;
						}
						date = new Date();
						System.out.println(date + " - " + name + " is leaving emergency state");
					}	
					else
					{
						previous = ( i + 1 ) % intersectionNumber;
						i = emergencyIndex - 1;
					}
				}

			}

			//send message requesting reward
			parentAgent.requestReward();

		}

	}

	/**
	 * 
	 * @param i
	 * @param string
	 * @return
	 */
	private String buildState(int nr, String s) {
		int num = neighbours.size();
		StringBuffer st = new StringBuffer();

		for (int i = 0; i < num; i++) {
			if (i == nr) {
				st.append(appendXTimes(s, num - 1));
			} else if (i == nr + 1 || nr == num - 1 && i == 0) {
				st.append(s + appendXTimes("r", num - 2));
			} else {
				st.append(appendXTimes("r", num - 1));
			}
		}
		return st.toString();
	}

	/**
	 * Appends the string str X times
	 * @param str
	 * @param x
	 * @return
	 */
	private String appendXTimes(String str, int x){
		StringBuffer temp = new StringBuffer();

		for ( int i = 0; i < x; i++)
			temp.append(str);

		return temp.toString();
	}

	/**
	 * 
	 * @return
	 */
	public int getRewardLane(String lane){
		SumoLane sumoLane = new SumoLane(lane);
		int vehicles = 0;
		int laneLenght = (int) Math.floor(sumoLane.getLength());
		float ratio;

		vehicles += sumoLane.getNumVehicles("nor") * 1;
		vehicles += sumoLane.getNumVehicles("pub") * 3; //bus are bigger
		vehicles += sumoLane.getNumVehicles("emer") * 9; // take in consideration that emergency have priority????

		ratio = (float) vehicles / (float) laneLenght;
		Date date = new Date();
		System.out.println(date + " - Lane " + lane + " has " + vehicles + " vehicles and lenght of " + laneLenght);

		//the smaller the ratio the bigger the reward
		if ( ratio > 0.96)
			return 100;
		else
			if ( ratio > 0.89)
				return 10;
			else
				return -10;

	}

	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	public boolean actionEmergency(String neighbour) {
		float rand = new Random().nextFloat();
		//debug
		Date date = new Date();
		System.out.println(date + " - Agent " + this.parentAgent.getLocalName() + " Probability to listen to emergencies equals " + LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB + " rand number is " + rand);

		if ( rand < LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB ) {
			for ( int i = 0; i < neighbours.size(); i++){
				if ( neighbour.indexOf(neighbours.get(i)) != -1 ){
					emergencyIndex = i;
					return true;
				}
			}
		}

		return false;
	}


}
