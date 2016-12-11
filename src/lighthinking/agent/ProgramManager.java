package lighthinking.agent;

import java.util.List;

import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.SumoTrafficLightProgram;
import trasmapi.sumo.SumoTrafficLightProgram.Phase;

public class ProgramManager {

	private SumoTrafficLight light;
	private SumoTrafficLightProgram program;
	private int tick;
	
	private int totalProgramDuration;
	private List<Phase> phases;
	
	public ProgramManager(SumoTrafficLight light) {
		this(light, light.getProgram());	
	}
	
	public ProgramManager(SumoTrafficLight light, SumoTrafficLightProgram program) {
		tick = 0;
		this.light = light;
		setProgram(program, false);		
	}
	
	public void setProgram(SumoTrafficLightProgram program) {
		setProgram(program, true);
	}
	
	public void setProgram(SumoTrafficLightProgram program, boolean forceUpdate) {
		tick = 0;
		this.program = program;
		
		phases = program.getPhases();
		totalProgramDuration = 0;
		for(Phase p : phases) {
			totalProgramDuration += (p.getDuration() / 1000);
		}
		if(forceUpdate) {
			updateAgentState();
		}
	}
	
	public int getTick() {
		return tick;
	}
	
	public SumoTrafficLightProgram getProgram() {
		return program;
	}
	
	public void updateAgent() {
		++tick;
		updateAgentState();
	}
	
	public void updateAgentState() {
		light.setState(getCurrentPhaseState());
	}
	
	public int getCurrentPhaseIndex() {
		int totalDuration = 0;
		for(int i = 0; i < phases.size(); ++i) {
			Phase p = phases.get(i);
			totalDuration += (p.getDuration() / 1000);
			if(totalDuration > (tick % totalProgramDuration)) {
				return i;
			}
		}
		return 0;
	}
	
	public Phase getCurrentPhase() {
		int totalDuration = 0;
		for(Phase p : phases) {
			totalDuration += (p.getDuration() / 1000);
			if(totalDuration > (tick % totalProgramDuration)) {
				return p;
			}
		}
		return phases.get(0);
	}
	
	public String getCurrentPhaseState() {
		int totalDuration = 0;
		for(Phase p : phases) {
			totalDuration += (p.getDuration() / 1000);
			if(totalDuration > (tick % totalProgramDuration)) {
				return p.getState();
			}
		}
		return phases.get(0).getState();
	}
	
	public int ticksToChangeState(){

		int tickOnProgram = tick % totalProgramDuration;
		int totalDuration = 0;
		for(int i = 0; i < phases.size(); ++i) {
			Phase p = phases.get(i);
			totalDuration += (p.getDuration() / 1000);
			if(totalDuration > (tick % totalProgramDuration)) {
				return totalDuration - tickOnProgram;
			}
		}
		return 0;
	}
	
	public List<Phase> getPhases() {
		return phases;
	}
}
