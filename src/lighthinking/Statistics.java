package lighthinking;

import lighthinking.agent.VehicleAgent;

public class Statistics {
	
	public int createStats(){
		
		int totalNumberOfTicks = VehicleAgent.totalTicksStopped;
		
		int totalNumberOfVehicles = VehicleAgent.vehiclesended;
		
		int maxTimeStopped = VehicleAgent.maxTime;
		
		int minTimeStopped = VehicleAgent.minTime;
		
		return 0;
	}
}
