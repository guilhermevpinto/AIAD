package trasmapi.sumo;

import java.util.ArrayList;
import java.util.List;

public class SumoTrafficLightProgram {
	
	/*
	 * State meaning:
	 * r	'red light' for a signal - vehicles must stop
	 * y	'amber (yellow) light' for a signal - vehicles will start to decelerate if far away from the junction, otherwise they pass
	 * g	'green light' for a signal, no priority - vehicles may pass the junction if no vehicle uses a higher priorised foe stream, otherwise they decelerate for letting it pass
	 * G	'green light' for a signal, priority - vehicles may pass the junction
	 * u	'red+yellow light' for a signal, may be used to indicate upcoming green phase but vehicles may not drive yet (shown as orange in the gui)
	 * o	'off - blinking' signal is switched off, blinking light indicates vehicles have to yield
	 * O	'off - no signal' signal is switched off, vehicles have the right of way
	 * 
	 * 			  0
	 * 					1
	 * 		3
	 * 			  2
	*/
    
    public class Phase {
        
        private int duration;
        private String state;
        
        public Phase(int duration, String state) {
            this.duration = duration;
            this.state = state;
        }
        
        public int getDuration() {
            return duration;
        }
        
        public String getState() {
            return state;
        }
    }

    private String id;
    private List<Phase> phases;
    
    public SumoTrafficLightProgram(String id) {
        this.id = id;
        this.phases = new ArrayList<Phase>();
    }
    
    public SumoTrafficLightProgram(ArrayList<Pair<Integer, Object>> items) {
        this((String) items.get(1).second());

        int numPhases = (int) items.get(5).second();
        for (int i = 0; i < numPhases; i++) {
            phases.add(new Phase((int) items.get(i * 4 + 6).second(),
                    (String) items.get(i * 4 + 9).second()));
        }
    }
    
    public String getId() {
        return id;
    }
    
    public List<Phase> getPhases() {
        return phases;
    }    
    
    public void addPhase(int duration, String state) {
        phases.add(new Phase(duration, state));
    }
    
    public int getDuration() {
        int duration = 0;
        for (Phase phase: phases) {
            duration += phase.getDuration();
        }
        return duration;
    }

}
