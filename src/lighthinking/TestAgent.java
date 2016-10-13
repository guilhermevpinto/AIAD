package lighthinking;

import uchicago.src.sim.engine.Stepable;

public class TestAgent implements Stepable {

	private int id;
	
	public TestAgent(int id) {
		this.id = id;
	}

	@Override
	public void step() {
		System.out.println("Agent " + id + " says hi!");
	}

}
