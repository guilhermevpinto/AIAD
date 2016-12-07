package lighthinking;

import lighthinking.agent.VehicleAgent;

public class Statistics {
	
	public static void createStats(){
		
		int totalNumberOfTicksStopped = VehicleAgent.totalTicksStopped;
		
		int totalNumberOfVehicles = VehicleAgent.vehiclesended;
		
		int maxTimeStopped = VehicleAgent.maxTime;
		
		int minTimeStopped = VehicleAgent.minTime;
		
		System.out.println("Number of Vehicles: " + totalNumberOfVehicles);
		System.out.println("Total Time Stopped: " + totalNumberOfTicksStopped);
		System.out.println("Median of Stopped time: " + totalNumberOfTicksStopped/totalNumberOfVehicles);
		System.out.println("Max Time a Car is Stopped: " + maxTimeStopped);
		System.out.println("Min Time a Car is Stopped: " + minTimeStopped);
		
	}
}
