package com.biofuels.fof.kosomodel.gameStage;

import org.json.simple.*;

import com.biofuels.fof.kosomodel.Game;


//------------------------------------------------------------------------------
public class GameStage_RoundWrapUp extends GameStage {

  public GameStage_RoundWrapUp(Game g) {
    super(g);
    // TODO Auto-generated constructor stub
  }
  public boolean ShouldEnter() {return true; }
  public void Enter() {
    game.sellFarmerCrops();
    game.clearFields();

  }
  public void Exit() {}
  public void HandleClientData(JSONObject data) {}
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "Round Wrap Up";
  }
}