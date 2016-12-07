package lighthinking.agent.double_skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class DoubleSkipperTLAgent extends TLAgent {

	private static final int MAX_TICKS_WITHOUT_CARS = 7;

	private int ticksWithoutCarsOnGreen = 0;
	private int carsOnGreen = 0;
	private int carsOnRed = 0;
	private String previousState = null;	
	
	public DoubleSkipperTLAgent(String s, AgentManager mngr) {
		super(s, mngr);
		this.type = Type.DOUBLE_SKIPPER;
	}

	@Override
	public void update() {
		super.update();
		updateTicks();
//		if(previousState == null || !previousState.equals(getCurrentState())) {
//			ticksWithoutCarsOnGreen = 0;
//		}	
//		
//		if (ticksWithoutCarsOnGreen >= MAX_TICKS_WITHOUT_CARS) {
//			ticksWithoutCarsOnGreen = 0;
//			if (agentManager.isDebug()) {
//				System.out.println("Traffic light " + internalID + " skipped a phase (no cars on green).");
//			}
//			this.skipCurrentPhase();
//		}
//		
//		previousState = getCurrentState();
	}

	private void updateTicks() {
		carsOnGreen = getCarsOnGreenLanes();
		carsOnRed = getNumberOfStoppedCars();
		if(internalID.equals("B3")) {
			System.out.println("G " + carsOnGreen + "   R " + carsOnRed);
		}
		if (carsOnGreenLanes == 0) {
			++ticksWithoutCarsOnGreen;
		} else {
			ticksWithoutCarsOnGreen = 0;
		}
	}
}
