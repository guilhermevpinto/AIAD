package lighthinking.agent;

import java.util.ArrayList;

import trasmapi.sumo.SumoVehicle;

@SuppressWarnings("serial")
public abstract class VehicleAgent extends Agent {
	
	protected AgentManager agentManager;
	protected SumoVehicle sumoVehicle;
	protected boolean alive;
	
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
	}
	
	@Override
	public void update() {
		if(!alive) {
			if(sumoVehicle.getSpeed() >= 0) {
				alive = true;
				if(agentManager.isDebug()) {
					System.out.println("Vehicle " + internalID + " started.");
				}
				resetParams();
			}
		}
		resetParams();
	}
	
	protected void resetParams() {
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
	
}
