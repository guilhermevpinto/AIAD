package lighthinking.agent.skipper;

import lighthinking.agent.AgentManager;
import lighthinking.agent.TLAgent;

@SuppressWarnings("serial")
public class SkipperTLAgent extends TLAgent {

	private static final int MAX_TICKS_WITHOUT_CARS = 7;

	private int ticksWithoutCarsOnGreen = 0;

	public SkipperTLAgent(String id, AgentManager mngr) {
		super(id, mngr);
		this.type = Type.SKIPPER;
	}

	@Override
	public void update() {
		super.update();
		updateTicks();
		
		if (ticksWithoutCarsOnGreen >= MAX_TICKS_WITHOUT_CARS) {
			ticksWithoutCarsOnGreen = 0;
			if (agentManager.isDebug()) {
				System.out.println("Traffic light " + internalID + " skipped a phase (no cars on green).");
			}
			this.skipCurrentPhase();
		}
	}

	private void updateTicks() {
		getCarsOnGreenLanes();
		if (carsOnGreenLanes == 0) {
			++ticksWithoutCarsOnGreen;
		} else {
			ticksWithoutCarsOnGreen = 0;
		}
	}
}
