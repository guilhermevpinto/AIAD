package test.Statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import test.Agents.TrafficLightAgent;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoVehicle;

public class Statistics implements Runnable{

	Sumo sumo;

	public Statistics(Sumo sumo) {
		this.sumo = sumo;
	}

	@Override
	public void run() {
		Boolean run = true;
		ArrayList<String> vehicles = new ArrayList<String>();
		double time = 0.0;
		double average = 0.0;
		double tempTime = 0.0;
		Date ra = new Date();
		String name = "statistics_"+ra.getTime()+".txt";
		FileWriter file;

		try {

			while(run){		

				file = new FileWriter(name, true);
				vehicles = SumoCom.getAllVehiclesIds();
				time = 0.0;

				for ( int i = 0; i < vehicles.size(); i++){
					SumoVehicle ve = new SumoVehicle(vehicles.get(i));
					tempTime = ve.getWaitingTime();
					if ( tempTime != -1 )
						time += tempTime;
				}


				if (vehicles.size() <= 0)
					average = time;
				else
					average = time / vehicles.size();

				Date now = new Date();

				file.write("-----" + now + " - Average Waiting time " + average);
				file.write("\r\n");
				System.err.println(now + " - Average Waiting time " + average);
				Thread.sleep(100);

				file.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





	}

}
