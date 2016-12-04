package utils;

import lighthinking.agent.AgentManager;

public interface Updateable {

	// Simple version (less comunication with other entities)
	public void update();

	// Complete version (more comunication with other entities)
	public void update(AgentManager agentManager);

}
