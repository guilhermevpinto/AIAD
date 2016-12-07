package lighthinking.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent {
	
	public static final double VELOCITY_THRESHOLD = 0.4;
	
	public static int totalTicksStopped = 0;
	public static int vehiclesended = 0;
	public static int maxTime = 0;
	public static int minTime = Integer.MAX_VALUE;
	
	protected AgentManager agentManager;
	protected SumoVehicle sumoVehicle;
	protected boolean alive;
	protected boolean end;
	protected int ticksStopped;
	
	//Params
	protected double speed;
	protected String routeID = null;
	protected ArrayList<String> edges = null;
	protected String edgeID = null;
	protected double waitingTime = -1;
	// new parameters added here must be added to "resetParams()"

	protected VehicleAgent(String s, AgentManager mngr) {
		super();
		internalID = s;
		agentManager = mngr;
		sumoVehicle = new SumoVehicle(internalID);
		alive = false;
		end = false;
		ticksStopped = 0;
	}
	
	@Override
	public void update() {
		if(!end){
			if(!alive) {
				if(sumoVehicle.getSpeed() >= 0 && !SumoCom.arrivedVehicles.contains(this.internalID)) {
					alive = true;
					if(agentManager.isDebug()) {
						System.out.println("Vehicle " + internalID + " started.");
					}
					resetParams();
				}
			}
			if(alive){
				double currentSpeed = this.getSpeed();
				
				if(currentSpeed < 0.2)
					ticksStopped++;
			}
			resetParams();
		}
	}
	
	private void resetParams() {
		speed = -1;
		routeID = null;
		edges = null;
		edgeID = null;
		waitingTime = -1;
	}

	@Override
	public void finish() {
		if(alive) {
			alive = false;
			end = true;
			totalTicksStopped += ticksStopped;
			vehiclesended++;
			if(ticksStopped > maxTime)
				maxTime = ticksStopped;
			if(ticksStopped < minTime)
				minTime = ticksStopped;
			if(agentManager.isDebug()) {
				System.out.println("Vehicle " + internalID + " finished.");
			}
		}
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public double getSpeed() {
		if(alive) {
			if(speed == -1) {
				speed = sumoVehicle.getSpeed();
			}
			return speed;
		}
		return -1;
	}
	
	public String getRouteID() {
		if (routeID == null) {
			routeID = sumoVehicle.getRouteId();
		}
		return routeID;
	}
	
	public ArrayList<String> getEdges() {
		if(alive) {
			if(edges == null) {
				edges = sumoVehicle.getEdges();
			}
			return edges;
		}
		return null;
	}
	
	public String getEdgeID() {
		if(alive) {
			if(edgeID == null) {
				edgeID = sumoVehicle.getEdgeId();
			}
			return edgeID;
		}
		return null;
	}
	
	public double getWaitingTime() {
		if(alive) {
			if(waitingTime == -1) {
				waitingTime = sumoVehicle.getWaitingTime();
			}
			return waitingTime;
		}
		return -1;	
	}
	
	public static int getVehiclesOnLane(String laneID, Collection<VehicleAgent> vehicles) {
		int result = 0;

		for(VehicleAgent v : vehicles) {
			if(v.isAlive() && laneID.startsWith(v.getEdgeID())) {
				++result;
			}
		}
		
		return result;
	}
	
	public static int getVehiclesStoppedOnLane(String laneID, Collection<VehicleAgent> vehicles) {
		int result = 0;

		for(VehicleAgent v : vehicles) {
			if(v.isAlive() && laneID.startsWith(v.getEdgeID()) && v.getSpeed() <= VELOCITY_THRESHOLD) {
				++result;
			}
		}
		
		return result;
	}
	
}
