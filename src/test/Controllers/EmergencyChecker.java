package test.Controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import trasmapi.sumo.SumoLane;

/**
 * Checks for Imcoming emergencies, by checking the EmergencyIndex of the traffic light
 * @author Liliana
 *
 */
public class EmergencyChecker implements Runnable{

	TrafficLightCtrl tlc;
	
	public EmergencyChecker(TrafficLightCtrl tlc) {
		this.tlc= tlc;
	}
	
	@Override
	public void run() {
		
		Boolean run = true;
		
		ArrayList<SumoLane> lanes = new ArrayList<SumoLane>();
		
		for ( int i = 0; i < tlc.getNeighbours().size(); i++ ){
			Date date = new Date();
			System.out.println(date + " - LANES : " + tlc.getNeighbours().get(i) + "to" + tlc.getName() + "_0");
			lanes.add(new SumoLane(tlc.getNeighbours().get(i) + "to" + tlc.getName() + "_0"));
		}
		
		while ( run )
		{
			try {
				
				for ( int i = 0; i < lanes.size(); i++ ){
					if ( lanes.get(i).getNumVehicles("eme") > 0 && tlc.getEmergencyIndex() == -1 ){
						tlc.setEmergencyIndex(i);
						tlc.getParentAgent().alertNeighbourOfEmergency();
						Date date = new Date();
						System.out.println(date + " - " + tlc.getName() + " will alert parents");
						while ( tlc.getEmergencyIndex() == i)
							Thread.sleep(5);
					}
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}

}
