package test.Learning;

import java.util.Random;

public class QLearning {
	
	// impact of the current update ( 0 <= alpha <= 1)
	private static final float LEARNING_RATE = (float) 1.0;
	
	//importance of Q's value in future states
	private static final float DISCOUNT_FACTOR = (float) 0.7;
	
	//factor that determines the map of the possible nrActions to a probablility according to their Q-value
	private static final float SOFTMAX_TEMPERATURE = (float) 0.5;
	
	private float[][] table;
	private int states = 0;
	private int actions = 0;
	private String name;
	
	/**
	 * 
	 * @param name
	 * @param states
	 * @param actions
	 */
	public QLearning(String name , int states, int actions){
		this.states = states;
		this.actions = actions;
		this.name = name;
		
		
		table = new float[states][actions];
	}
	
	/***
	 * Updates state according with reward
	 * @param state
	 * @param reward
	 */
	public void reinforce(State state, float reward){
		int currState = state.getState();
		int action = state.getLastAction();
		
		float currQValue = table[currState][action];
		
		int nextState = state.testAction(action);
		int bestNextAction = getBestPossibleAction(nextState);
		
		float bestNetQ = table[nextState][bestNextAction];
		
		// Q(s,a) = Q(s,a) + alpha * ( r + discountFactor * ( Q(s',a') - Q(s,a)))
		table[currState][action] = ( float) ( currQValue + LEARNING_RATE * ( reward + DISCOUNT_FACTOR * (bestNetQ - currQValue)));
	}
	
	/**
	 * Returns the best state, the state with the higher reward
	 * @param state
	 * @return
	 */
	public int getBestPossibleAction(int state){
		int bestAction = 0;
		float bestQ = 0;
		
		float[] action = table[state];
		
		
		for (int i = 0; i < actions; i++){
			float currAction = action[i];
			
			if ( currAction > bestQ){
				bestAction = i;
				bestQ = currAction;
			}
		}
		
		return bestAction;
	}
	
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	public int getActionToTake(int state){
		int nextAction = 0;
		float[] actionsProbabilities = new float[actions];
		float[] possibleActions = table[state];
		float rand = new Random().nextFloat();
		
		//denominator of the softmax calculation
		float deno = 0;
		
		//Sum ( exp(Q(s,b)/r))
		for ( int i = 0; i < actions; i++){
			deno += Math.exp(( possibleActions[i] / SOFTMAX_TEMPERATURE));
		}
		
		//fills the probabilities that each action has
		for ( int i = 0; i < actions; i++){
			actionsProbabilities[i] = (float) (Math.exp(possibleActions[i]/SOFTMAX_TEMPERATURE)/deno);
			
			
			//update value with previous action
			if ( i > 0){
				actionsProbabilities[i] += actionsProbabilities[i-1];
			}
		}
		
		//uses probabilitie to choose next state 
		for ( int i = 0; i < actions; i++){
			if ( i == actions - 1){
				nextAction = i;
				break;
			}
			else
				if ( rand < actionsProbabilities[i+1]){
					nextAction = i;
					break;
				}
		}
		
		
		return nextAction;
	}
	
	
	public void printTable(){
		
	}
	
	
	
}
