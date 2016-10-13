package lighthinking;

import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimpleModel;

public class TestModel extends SimpleModel {

	private int numberOfAgents;
	
	public TestModel() {
		name = "My Test Model";
	}
	
	@Override
	public void setup() {
		super.setup();
		numberOfAgents = 3;
		autoStep = true;
		shuffle = true;
	}
	
	@Override
	public void buildModel() {
		for(int i = 0; i < numberOfAgents; ++i) {
			agentList.add(new TestAgent(i));
		}
	}
	
	@Override
	protected void preStep() {
		System.out.println("Initiating step " + getTickCount());
	}
	
	@Override
	protected void postStep() {
		System.out.println("Done step " + getTickCount());
	}
}
