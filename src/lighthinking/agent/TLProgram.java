package lighthinking.agent;

import java.util.List;

import trasmapi.sumo.SumoTrafficLightProgram;
import trasmapi.sumo.SumoTrafficLightProgram.Phase;

public class TLProgram {

	// New program starts at phase "phase"
	public static SumoTrafficLightProgram programSkipToPhase(SumoTrafficLightProgram program, Phase phase) {
		return actualProgramSkipPhase(program, phase, 0);
	}
	
	// New program starts at phase after "phase"
	public static SumoTrafficLightProgram programSkipPhase(SumoTrafficLightProgram program, Phase phase) {
		return actualProgramSkipPhase(program, phase, 1);
	}
	
	public static SumoTrafficLightProgram actualProgramSkipPhase(SumoTrafficLightProgram program, Phase phase, int offset) {
		SumoTrafficLightProgram result = new SumoTrafficLightProgram(program.getId());
		List<Phase> phases = program.getPhases();
		int startPos = indexOfPhase(program, phase) + offset;
		int currPos = 0;
		
		System.out.println(phase.getState() + "  " + startPos);
		
		int size = phases.size();
		while(currPos < size) {
			Phase p = phases.get((startPos + currPos) % size);
			result.addPhase(p.getDuration(), p.getState());
			System.out.println("Adding " + p.getState());
			
			++currPos;
		}
		
		return result;
	}
	
	private static int indexOfPhase(SumoTrafficLightProgram program, Phase phase) {
		List<Phase> phases = program.getPhases();
		
		for(int i = 0; i < phases.size(); ++i) {
			Phase p = phases.get(i);
			if(p.getDuration() == phase.getDuration() && p.getState().equals(phase.getState())) {
				return i;
			}
		}
		
		return -1;
	}

}
