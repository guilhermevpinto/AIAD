package lighthinking;

import java.util.HashMap;

import lighthinking.agent.Agent;
import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;
import lighthinking.agent.VehicleAgent;

public class Statistics {
	
	public static final int MAX_AVERAGE_ALIVE = 400;
	public static final int MIN_SCORE = 10;
	
	public static void createStats(){
		
		int totalNumberOfTicksStopped = VehicleAgent.totalTicksStopped;

		int totalNumberOfTicksAlive = VehicleAgent.totalTicksAlive;
		
		int totalNumberOfVehicles = VehicleAgent.vehiclesended;
		
		int maxTimeStopped = VehicleAgent.maxTime;
		
		int minTimeStopped = VehicleAgent.minTime;
		
		System.out.println("Number of Vehicles: " + totalNumberOfVehicles);
		System.out.println("Total Time Stopped: " + totalNumberOfTicksStopped);
		System.out.println("Total Time Alive: " + totalNumberOfTicksAlive);
		if(totalNumberOfVehicles != 0) {
			System.out.println("Average of Stopped time: " + totalNumberOfTicksStopped/totalNumberOfVehicles);
			System.out.println("Average of Alive time: " + totalNumberOfTicksAlive/totalNumberOfVehicles);
		}
		System.out.println("Max Time a Car is Stopped: " + maxTimeStopped);
		System.out.println("Min Time a Car is Stopped: " + minTimeStopped);
		
		System.out.println("Stats per Semaphore :");
		
		for (HashMap.Entry<String, TLAgent> entry : AgentManager.trafficLightAgents.entrySet()) {
			TLAgent tl = entry.getValue();
			System.out.println("Local Stopped Time: " + tl.getLocalStopped());	
		}
		
	}
	
	public static double getOverallPerformance() {
		int totalNumberOfTicksAlive = VehicleAgent.totalTicksAlive;
		int totalNumberOfVehicles = VehicleAgent.vehiclesended;
		if(totalNumberOfVehicles != 0) {
			return MAX_AVERAGE_ALIVE - (totalNumberOfTicksAlive/totalNumberOfVehicles);
		}
		return MIN_SCORE;
	}
	
	public static void resetStats() {
		Agent.tick = 0;
		VehicleAgent.totalTicksStopped = 0;
		VehicleAgent.totalTicksAlive = 0;
		VehicleAgent.vehiclesended = 0;
		VehicleAgent.maxTime = 0;
		VehicleAgent.minTime = Integer.MAX_VALUE;
	}
}
