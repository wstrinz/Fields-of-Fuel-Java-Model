package com.biofuels.fof.kosomodel.gameStage;

import org.json.simple.*;

import com.biofuels.fof.kosomodel.Farm;
import com.biofuels.fof.kosomodel.Field;
import com.biofuels.fof.kosomodel.Game;


//------------------------------------------------------------------------------
public class GameStage_RoundWrapUp extends GameStage {

  public GameStage_RoundWrapUp(Game g) {
    super(g);
    // TODO Auto-generated constructor stub
  }
  public boolean ShouldEnter() {return true; }
  public void Enter() {

    //compute new SOM for each field
    for (Farm fa:game.getFarms()){
      fa.updatePhosphorous();
      for(Field fi:fa.getFields()){
        fi.updateSOM();
        fi.addHistoryYear();
      }
    }

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