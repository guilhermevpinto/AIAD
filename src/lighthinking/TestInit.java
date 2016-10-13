package lighthinking;

import javax.swing.UIManager;

import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.gui.FrameFactory;

/**
 * Adapted from uchicago.src.sim.engine.SimInit
 */
public class TestInit {

	private boolean exitOnExit = true;

	private int numRuns = 1;

	public void setNumRuns(int numRuns) {
		this.numRuns = numRuns;
	}

	public void setExitOnExit(boolean val) {
		exitOnExit = val;
	}

	private void load(SimModel model) {
		// Load the system specific look and feel
		try {
			String nativeLF = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(nativeLF);
		} catch (Exception e) {
			// oh well, couldn't load another look and feel
		}

		FrameFactory.load(model.getClass().getName());
		Controller control = null;
		control = new Controller();

		model.setController(control);
		control.setPauseAt(numRuns);
		control.setExitOnExit(exitOnExit);
		control.setModel(model);
		model.addSimEventListener(control);

		control.display();
	}

	public void loadModel(SimModel model, String fileName, boolean isBatch) {
		load(new TestModel());
	}

	public static void main(String[] args) {
		TestInit si = new TestInit();
		si.load(new TestModel());
	}
}
