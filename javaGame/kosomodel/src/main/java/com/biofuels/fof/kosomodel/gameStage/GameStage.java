package com.biofuels.fof.kosomodel.gameStage;

import org.json.simple.*;

// Interface for a game stage. This would be something like Contracts, Management, etc
//	This stage mechanism encapsulates all of the logic for running the stage on the back
//	end as well as managing sending the data needed for a given stage to the clients
//	attached to a given room.
//------------------------------------------------------------------------------
public interface GameStage {
//------------------------------------------------------------------------------

	// Checks current game settings about whether this game stage should be entered or skipped
	public boolean ShouldEnter();

	// Called when game enters the given stage
	public void Enter();
	// Called when game exits the given stage
	public void Exit();

	// Messages/Data/Events from client in JSON object format...
//	public void HandleClientData(JsonNode data);
	public void HandleClientData(JSONObject data);
}