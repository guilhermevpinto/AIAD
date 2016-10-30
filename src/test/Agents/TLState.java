package test.Agents;

import java.io.IOException;

import test.Learning.State;

public class TLState implements State{

	private static final int LIGHTS_GRANULARITY = 8;
	private static final int ACTIONS_BY_LIGHT = 3;
	private static final int LIGHTS_MIN_TIME = 20;
	private static final int LIGHT_MAX_TIME = 60;
	private static final int NUM_STATES_PER_LIGHT = ( LIGHT_MAX_TIME - LIGHTS_MIN_TIME ) / LIGHTS_GRANULARITY + 1;
	
	private String name;
	private int state;
	private int lastAction;
	private int[] greenTime;
	private int intersections;
	
	/**
	 * 
	 * @param name
	 * @param intersections
	 * @param states
	 */
	public TLState(String name, int intersections, int states) {
		this.name = name;
		this.lastAction = 0;
		this.intersections = intersections;
		
		greenTime = new int[intersections];
		
		this.state = 0;
		
		for ( int i = 0; i < intersections; i++){
			state += Math.pow(NUM_STATES_PER_LIGHT,i)*2;
		}
		
		updateState(state);
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getStatesPerLight(){
		return NUM_STATES_PER_LIGHT;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getActionPerLight(){
		return ACTIONS_BY_LIGHT;
	}
	
	/**
	 * 
	 * @param state
	 */
	private void updateState(int state){
		this.state = state;
		for ( int i = 0; i < intersections; i++){
			greenTime[i] = LIGHTS_MIN_TIME +  ( ( ( state / (int) Math.pow(NUM_STATES_PER_LIGHT, i)) % NUM_STATES_PER_LIGHT) * LIGHTS_GRANULARITY );
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public int getGreenTime(int index){
		return greenTime[index];
	}
	
	/**
	 * 
	 * @return
	 */
	public int[] getGreenTime(){
		return greenTime;
	}
	
	/**
	 * 
	 */
	@Override
	public int getState() {
		return state;
	}

	/**
	 * 
	 */
	@Override
	public int getLastAction() {
		return lastAction;
	}

	/**
	 * 
	 */
	@Override
	public int testAction(int action) {
		int newState = state;
		
		for ( int i = 0;i < intersections; i++){
			int temp = ( action / (int) Math.pow(ACTIONS_BY_LIGHT,i)) % ACTIONS_BY_LIGHT;
			
			switch (temp) {
			case 0:
				//decreases time
				if ( greenTime[i] > LIGHTS_MIN_TIME){
					newState -= (int) Math.pow(NUM_STATES_PER_LIGHT, i);
				}
				break;
			case 1:
				//does nothing
				break;
			case 2:
				//increases time
				if ( greenTime[i] < LIGHT_MAX_TIME){
					newState += (int) Math.pow(NUM_STATES_PER_LIGHT, i);
				}
				break;

			default:
				break;
			}
		}
		
		return newState;
	}
	
	/**
	 * 
	 * @param action
	 * @return
	 */
	public int applyAction(int action){
		int newState = testAction(action);
		updateState(lastAction);
		lastAction = action;
		return newState;
	}

}
