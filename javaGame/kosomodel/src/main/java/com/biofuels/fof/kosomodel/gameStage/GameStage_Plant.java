package com.biofuels.fof.kosomodel.gameStage;

import org.json.simple.*;

//------------------------------------------------------------------------------
public class GameStage_Plant implements GameStage {

	public boolean ShouldEnter() {return true; }
	public void Enter() {}
	public void Exit() {}
	public void HandleClientData(JSONObject data) {}
}