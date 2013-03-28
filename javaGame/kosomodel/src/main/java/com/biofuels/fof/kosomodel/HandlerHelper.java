package com.biofuels.fof.kosomodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import akka.actor.ActorRef;

public class HandlerHelper {

  public Map<String, Game> games = new HashMap<>();
  private ActorRef listener;
  private ActorRef handler;

  public HandlerHelper() {
    // TODO Auto-generated constructor stub
  }

  public HandlerHelper(ActorRef listener, ActorRef handler) {
    // TODO Auto-generated constructor stub
    this.listener = listener;
    this.handler = handler;
  }

  @SuppressWarnings("unchecked")
  public String[] handle(String event){
    //    System.out.println("Handling " + event);
    ArrayList<String> replies = new ArrayList<>();

    JSONObject eventObj = (JSONObject) JSONValue.parse(event);


    Integer clientID = -1;
    if(eventObj.get("clientID") != null) {
      clientID = Integer.parseInt((String) eventObj.get("clientID"));
    }
    String roomName = (String) eventObj.get("roomName");
    String roomID = (String) eventObj.get("roomID");
    String farmerName = (String) eventObj.get("userName");
    String deviseName = (String) eventObj.get("deviseName");

    switch (eventObj.get("event").toString()){

    case "validateRoom":
      if(games.get(roomName) != null){
        sendMessage(buildJson(clientID.toString(), "validateRoom", "result", false));
      }
      else{
        // System.out.println("j tell success " + event + " to " + listener);
        sendMessage(buildJson(clientID.toString(), "validateRoom", "result", true));
      }
      break;

    case "globalValidateRoom":
      boolean roomResult = false;
      boolean needsPass = false;
      boolean correctPass = false;
      if(games.get(roomName) != null){
        roomResult = true;
        if(games.get(roomName).hasPassword()){
          needsPass = true;
          if(games.get(roomName).getPassword().equals(eventObj.get("password")))
            correctPass = true;
        }
      }

      sendMessage(buildJson(clientID.toString(), "globalValidateRoom","roomResult",roomResult,"needsPassword",needsPass,
          "passwordResult",correctPass));
      break;

    case "globalJoinRoom":
      boolean joinResult = false;
      if(games.get(roomName) != null){
        if(games.get(roomName).hasPassword()){
          if(games.get(roomName).getPassword().equals(eventObj.get("password")))
            joinResult = true;
        }
        else
          joinResult = true;
      }
      sendMessage(buildJson(clientID.toString(), "globalJoinRoom","result",joinResult));
      break;

    case "changeSettings":
      //replies.add(event);
      games.get(roomID).changeSettings(((Long)eventObj.get("fieldCount")).intValue(),
          (boolean) eventObj.get("contractsOn"), (boolean)eventObj.get("mgmtOptsOn"));
      break;

    case "createRoom":

      if(games.get(roomName) != null){
        sendMessage(buildJson(clientID.toString(), "createRoom", "result", false));

      }
      else if(((String)eventObj.get("password")).length()>0){
        games.put(roomName, new Game(roomName, (String)eventObj.get("password"), (long)eventObj.get("playerCount")));
        sendMessage(buildJson(clientID.toString(), "createRoom","result",true));
      }
      else{
        games.put(roomName, new Game(roomName, (long)eventObj.get("playerCount")));
        sendMessage(buildJson(clientID.toString(), "createRoom","result",true));
      }

      //replies.add("{\"event\":\"createRoom\",\"result\":true}");
      break;

    case "validateUserName":
      roomResult = (roomExists(roomName) && !games.get(roomName).isFull());
      boolean nameResult = false;
      needsPass = false;
      correctPass = false;
      if(roomResult){
        //        System.out.println("user " + eventObj.get("userName"));
        nameResult = !eventObj.get("userName").equals("");
        nameResult = nameResult && ((!farmerExistsInRoom(farmerName, roomName)
            || (deviseName != null && games.get(roomName).getFarm(farmerName).getCurrentUser().equals(deviseName))));
        needsPass = games.get(roomName).hasPassword();
        if(needsPass){
          correctPass = games.get(roomName).getPassword().equals(eventObj.get("password"));
        }
      }
      sendMessage(buildJson(clientID.toString(), "validateUserName","roomResult",roomResult,"needsPassword",needsPass,
          "passwordResult",correctPass,"userNameResult",nameResult));
      break;

    case "loadFromServer":


      JSONArray list = new JSONArray();
      JSONObject msg = new JSONObject();
      //JSONObject fields = new JSONObject();
      msg.put("event", "loadFromServer");
      msg.put("clientID", clientID);
      for(Field f:games.get(roomName).getFields(clientID)){
        JSONObject thisfield = new JSONObject();
        thisfield.put("crop",f.getCrop().toString());
        thisfield.put("fertilizer", f.isFertilize());
        thisfield.put("pesticide", f.isPesticide());
        thisfield.put("tillage",f.isTill());
        thisfield.put("SOM",f.getSOC());
        list.add(thisfield);
      }

      msg.put("fields", list);
      sendMessage(msg.toJSONString());

      break;

    case "getFarmerList":
      list = new JSONArray();
      //JSONObject farmers = new JSONObject();
      msg = new JSONObject();
      for(Farm f:games.get(roomID).getFarms()){
        JSONObject farm = new JSONObject();
        farm.put("name", f.getName());
        farm.put("ready", f.isReady());
        //        list.add("\""+f.getName()+"\"");
        //        list.add(false);
        list.add(farm);
      }
      msg.put("event", "farmerList");
      msg.put("clientID", clientID);
      msg.put("Farmers", list);
      //      System.out.println(msg.toJSONString());
      sendMessage(msg.toJSONString());
      break;

    case "plantField":
      //System.out.println("planting");
      games.get(roomID).getFarm(clientID).setField(((Long)eventObj.get("field")).intValue(),(String) eventObj.get("crop"));
      break;

    case "setFieldManagement":
      int field = ((Long)eventObj.get("field")).intValue();
      String technique = (String) eventObj.get("technique");
      boolean value = (boolean) eventObj.get("value");
      games.get(roomID).getFarm(clientID).changeFieldManagement(field, technique, value);
      break;

    case "getGameInfo":
      sendGetGameInfo(roomID, clientID);
      break;

    case "advanceStage":
      doAdvanceStage(roomID);
      break;

    case "farmerReady":
      Game g = games.get(roomID);
      g.getFarm(clientID).setReady(true);
      g.farmerReady();
      if(g.getReadyFarmers() == g.getFarms().size())
        doAdvanceStage(roomID);
      break;

    case "getFarmInfo":
      sendGetFarmInfo(clientID, roomID, clientID);
      //      int earnings = games.get(roomID).getFarm(clientID).getCapital();
      //      int earningsRank = games.get(roomID).getCapitalRank(clientID);
      //      JSONObject reply = new JSONObject();
      //      reply.put("event", "getFarmInfo");
      //      reply.put("capital", earnings);
      //      reply.put("capitalRank", earningsRank);
      //      sendMessage(reply.toJSONString());
      break;

    case "getFarmHistory":
      sendGetFarmHistory(clientID, roomID, clientID);
      break;

    case "getCurrentSettings":
      sendCurrentSettings(clientID, roomID, clientID);
      break;

    case "getFarmerHistory":
      this.sendGetFarmerHistory(clientID, roomID, clientID);
      break;

    case "getLatestFarmerHistory":
      this.sendLatestFarmerHistory(clientID, roomID, clientID);
      break;

    case "getLatestFieldHistory":
      this.sendLatestFieldHistory(clientID, roomID, clientID);
      break;

    case "joinRoom":
      boolean roomExist = roomExists(roomName);
      boolean shouldMakeNew = false;
      boolean shouldRejoin = false;
      if(roomExist){
        shouldMakeNew = !farmerExistsInRoom(farmerName, roomName) && !games.get(roomName).isFull();
        if(!shouldMakeNew){
          shouldRejoin = deviseName != null && games.get(roomName).getFarm(farmerName).getCurrentUser().equals(deviseName);
        }
      }
      if(roomExist && (shouldMakeNew || shouldRejoin) && games.get(roomName).getPassword().equals(eventObj.get("password")))
      {
        if(shouldMakeNew){
          games.get(roomName).addFarmer(farmerName, clientID);
          games.get(roomName).getFarm(farmerName).setCurrentUser(deviseName);
        }
        else if(shouldRejoin){
          games.get(roomName).rejoinFarmer(farmerName, clientID);
        }
        sendMessage(buildJson(clientID.toString(), "joinRoom","result",true,"roomName",roomName,"userName",(String)eventObj.get("userName")));
        sendCurrentSettings(clientID, roomID, clientID);


        list = new JSONArray();
        msg = new JSONObject();
        for(Farm f:games.get(roomName).getFarms()){
          JSONObject farm = new JSONObject();
          farm.put("name", f.getName());
          farm.put("ready", true);
          list.add(farm);
        }
        msg.put("event", "farmerList");
        msg.put("clientID", roomName);
        msg.put("Farmers", list);
        sendMessage(msg.toJSONString());
      }
      else
        sendMessage(buildJson(clientID.toString(), "joinRoom","result",false));
      break;
    default:
    }
    String[] ret = new String[replies.size()];
    replies.toArray(ret);
    return ret;
  }

  /*private boolean roomValid(String room){
      return (room.length()>0 && roomExists(room));
    }*/



  @SuppressWarnings("unchecked")
  private void doAdvanceStage(String roomID) {
    // TODO Auto-generated method stub
    //    System.out.println("advancing");
    games.get(roomID).advanceStage();

    int stage = games.get(roomID).getStageNumber();
    String roundName = games.get(roomID).getStageName();
    int year = games.get(roomID).getYear();

    JSONObject replyAdvanceStage = new JSONObject();
    replyAdvanceStage.put("event", "advanceStage");
    replyAdvanceStage.put("stageNumber", stage);
    replyAdvanceStage.put("stageName", roundName);
    replyAdvanceStage.put("year", year);
    replyAdvanceStage.put("clientID", roomID);
    sendMessage(replyAdvanceStage.toJSONString());



    if (stage == 0){
      for(Farm fa:games.get(roomID).getFarms()){
        JSONArray list = new JSONArray();
        JSONObject msg = new JSONObject();
        //JSONObject fields = new JSONObject();
        msg.put("event", "loadFromServer");
        msg.put("clientID", fa.getClientID());

        for(Field f:fa.getFields()){
          JSONObject thisfield = new JSONObject();
          thisfield.put("crop",f.getCrop().toString());
          thisfield.put("fertilizer", f.isFertilize());
          thisfield.put("pesticide", f.isPesticide());
          thisfield.put("tillage",f.isTill());
          list.add(thisfield);
        }

        msg.put("fields", list);
        sendGetFarmInfo(fa.getClientID(), roomID, fa.getClientID());

        sendMessage(msg.toJSONString());

      }
      sendGetGameInfo(roomID, roomID);
    }
  }

  private void sendMessage(String message) {
    EventMessage msg = new EventMessage(message);
    //    System.out.println("sending " + msg.message);
    listener.tell(msg, handler);
  }

  private boolean roomExists(String room){
    return games.get(room) != null;
  }

  private boolean farmerExistsInRoom(String farmer, String room){
    if(roomExists(room)){
      return games.get(room).hasFarmer(farmer);
    }
    return false;
  }

  private String buildJson(String clientID, String event, Object ... arguments){
    String start = "{\"event\":\""+event+"\",\"clientID\":\"" + clientID + "\",";
    StringBuilder sb = new StringBuilder(start);
    if(!(arguments.length % 2 == 0)){
      System.out.println("bad argument list; not an even number");
      return (sb.append("}")).toString();
    }
    for(int i = 0;i<arguments.length;i+=2){
      String str1 = arguments[i].toString();
      if(arguments[i] instanceof String){
        str1 = "\"" + arguments[i] + "\"";
      }
      String str2 = arguments[i+1].toString();
      if(arguments[i+1] instanceof String){
        str2 = "\"" + arguments[i+1] + "\"";
      }
      sb.append(str1);
      sb.append(":");
      sb.append(str2);

      if(i+2 == arguments.length){
        sb.append("}");
      }
      else{
        sb.append(",");
      }
    }
    return(sb.toString());
    //return(sb.toString());

  }

  public void setListener(ActorRef actor) {
    // TODO Auto-generated method stub
    this.listener = actor;
  }

  @SuppressWarnings("unchecked")
  public void sendGetFarmInfo(int clientID, String roomID,  Object sendAddr){
    //    if(roomID == null)
    //      System.out.println("null room id");
    //    else
    //      System.out.println(roomID);
    //
    //    System.out.println(clientID);
    Game game = games.get(roomID);
    Farm farm = game.getFarm(clientID);
    int earnings = game.getFarm(clientID).getCapital();
    int earningsRank = game.getCapitalRank(game.getFarm(clientID));


    //double economicsScore = farm.getEconScore();
    Double b = new Double(3.112);

    double economicsScore = farm.getCapital();
    int economicsRank = farm.getEconRank();
    double energyScore = (double)Math.round(farm.getEnergyScore() * 10000) / 1000.0;
    int energyRank = farm.getEnergyRank();
    double environmentScore = (double)Math.round(farm.getEnvScore() * 10000) / 1000.0;
    int environmentRank = farm.getEnvRank();
    double sustainabilityScore = (double)Math.round(farm.getOverallScore() * 10000) / 1000.0;
    int sustainabilityRank = farm.getOverallRank();

    double phos = games.get(roomID).getFarm(clientID).getPhosphorous();

    JSONObject reply = new JSONObject();
    reply.put("event", "getFarmInfo");
    reply.put("phosphorous", phos);
    reply.put("capital", earnings);
    reply.put("capitalRank", earningsRank);
    reply.put("sustainabilityScore", sustainabilityScore);
    reply.put("sustainabilityRank", sustainabilityRank);
    reply.put("economicsScore", economicsScore);
    reply.put("economicsRank", economicsRank);
    reply.put("energyScore", energyScore);
    reply.put("energyRank", energyRank);
    reply.put("environmentScore", environmentScore);
    reply.put("environmentRank", environmentRank);
    reply.put("clientID", sendAddr);
    sendMessage(reply.toJSONString());
  }

  @SuppressWarnings("unchecked")
  public void sendGetGameInfo(String roomID, Object sendAddr){
    int year = games.get(roomID).getYear();
    int stage = games.get(roomID).getStageNumber();
    List<String> enabledStages = games.get(roomID).getEnabledStages();
    JSONArray stages = new JSONArray();
    stages.addAll(enabledStages);
    //    System.out.println(enabledStages.toString());


    JSONObject replyGameInfo = new JSONObject();
    replyGameInfo.put("event", "getGameInfo");
    replyGameInfo.put("year", year);
    replyGameInfo.put("stage", stage);
    replyGameInfo.put("enabledStages", stages);
    replyGameInfo.put("clientID", sendAddr);
    sendMessage(replyGameInfo.toJSONString());
  }

  @SuppressWarnings("unchecked")
  private void sendGetFarmHistory(Integer clientID, String roomID, Object sendAddr) {
    //    List<Field> fields = games.get(roomID).getFarm(clientID).getFields();

    for(int i = 0;i<games.get(roomID).getYear();i++){
      JSONObject reply = new JSONObject();
      JSONArray fields = new JSONArray();
      for(Field f:games.get(roomID).getFarm(clientID).getFields()){
        FieldHistory.HistoryYear y = f.getHistory().getHistory().get(i);
        JSONObject field = new JSONObject();
        field.put("SOM", y.SOM);
        field.put("crop", y.crop.toString());
        field.put("yield", y.yield);
        field.put("fertilizer", y.fertilizer);
        field.put("pesticide", y.pesticide);
        field.put("till", y.till);
        field.put("year", i);
        fields.add(field);
      }
      reply.put("event", "getLatestFieldHistory");
      reply.put("clientID", sendAddr);
      reply.put("fields", fields);
      sendMessage(reply.toJSONString());
    }

    /*for(Field f:games.get(roomID).getFarm(clientID).getFields()){
      JSONObject reply = new JSONObject();
      JSONArray fields = new JSONArray();
      FieldHistory history = f.getHistory();
      if(history == null){
        System.out.println("history null!");
      }
      FieldHistory.HistoryYear y = history.getHistory().getLast()
        JSONObject year = new JSONObject();
        year.put("SOM", y.SOM);
        year.put("crop", y.crop.toString());
        year.put("yield", y.yield);
        year.put("fertilizer", y.fertilizer);
        year.put("pesticide", y.pesticide);
        year.put("till", y.till);
        seasons.add(year);

      fields.add(seasons);
      reply.put("event", "getFarmHistory");
      reply.put("clientID", sendAddr);
      reply.put("fields", fields);
      sendMessage(reply.toJSONString());
    }*/

  }

  @SuppressWarnings("unchecked")
  private void sendLatestFieldHistory(Integer clientID, String roomID, Object sendAddr){
    JSONObject reply = new JSONObject();
    JSONArray fields = new JSONArray();
    for(Field f:games.get(roomID).getFarm(clientID).getFields()){

      FieldHistory history = f.getHistory();
      if(history == null){
        System.out.println("history null!");
      }
      FieldHistory.HistoryYear y = history.getHistory().getLast();
      JSONObject year = new JSONObject();
      year.put("year", y.year);
      year.put("SOM", y.SOM);
      year.put("crop", y.crop.toString());
      year.put("yield", y.yield);
      year.put("fertilizer", y.fertilizer);
      year.put("pesticide", y.pesticide);
      year.put("till", y.till);
      //seasons.add(year);
      fields.add(year);

    }
    reply.put("event", "getLatestFieldHistory");
    reply.put("clientID", sendAddr);
    reply.put("fields", fields);
    sendMessage(reply.toJSONString());
  }

  @SuppressWarnings("unchecked")
  private void sendGetFarmerHistory(Integer clientID, String roomID, Object sendAddr){
//
    Game game = games.get(roomID);
    Farm farm = game.getFarm(clientID);
//    JSONArray years = new JSONArray();

    for(FarmHistory.HistoryYear y: farm.getHistory()){
      JSONObject reply = new JSONObject();
      JSONObject year = new JSONObject();
      year.put("year", y.year);
      year.put("earnings", y.earnings);
      year.put("soilSubscore", y.soilSubscore);
      year.put("waterSubscore", y.waterSubscore);
      year.put("sustainabilityScore", y.sustainabilityScore);
      year.put("environmentScore", y.environmentScore);
      year.put("switchgrassIncome", y.switchgrassIncome);
      year.put("energyScore", y.energyScore);
      year.put("economicsScore", y.economicsScore);
      year.put("cornIncome", y.cornIncome);
      year.put("switchgrassIncome", y.switchgrassIncome);
      year.put("cornYield", y.cornYield);
      year.put("grassYield", y.grassYield);
      year.put("sustainabilityRank", y.sustainabilityRank);
      year.put("economicsRank", y.economicsRank);
      year.put("environmentRank", y.environmentRank);
      year.put("energyRank", y.energyRank);
      reply.put("event", "getLatestFarmerHistory");
      reply.put("yearInfo", year);
      reply.put("clientID", sendAddr);
      sendMessage(reply.toJSONString());
    }


//    sendMessage(reply.toJSONString());


  }

  @SuppressWarnings("unchecked")
  private void sendLatestFarmerHistory(Integer clientID, String roomID, Object sendAddr){
    JSONObject reply = new JSONObject();
    Game game = games.get(roomID);
    Farm farm = game.getFarm(clientID);
    FarmHistory.HistoryYear y = farm.getHistory().getLast();
    JSONObject year = new JSONObject();
    year.put("year", y.year);
    year.put("earnings", y.earnings);
    year.put("soilSubscore", y.soilSubscore);
    year.put("waterSubscore", y.waterSubscore);
    year.put("sustainabilityScore", y.sustainabilityScore);
    year.put("environmentScore", y.environmentScore);
    year.put("switchgrassIncome", y.switchgrassIncome);
    year.put("energyScore", y.energyScore);
    year.put("economicsScore", y.economicsScore);
    year.put("cornIncome", y.cornIncome);
    year.put("switchgrassIncome", y.switchgrassIncome);
    year.put("cornYield", y.cornYield);
    year.put("grassYield", y.grassYield);
    year.put("sustainabilityRank", y.sustainabilityRank);
    year.put("economicsRank", y.economicsRank);
    year.put("environmentRank", y.environmentRank);
    year.put("energyRank", y.energyRank);
    reply.put("event", "getLatestFarmerHistory");
    reply.put("yearInfo", year);
    reply.put("clientID", sendAddr);
    sendMessage(reply.toJSONString());
  }

  @SuppressWarnings("unchecked")
  private void sendCurrentSettings(Integer clientID, String roomID, Object sendAddr){
    JSONObject reply = new JSONObject();
    reply.put("event", "changeSettings");
    reply.put("contractsOn", games.get(roomID).isContracts());
    reply.put("mgmtOptsOn", games.get(roomID).isManagement());
    reply.put("fields", games.get(roomID).getFieldsPerFarm());
    reply.put("clientID", sendAddr);
    sendMessage(reply.toJSONString());

    int stage = games.get(roomID).getStageNumber();
    String roundName = games.get(roomID).getStageName();
    JSONObject replyAdvanceStage = new JSONObject();
    replyAdvanceStage.put("event", "advanceStage");
    replyAdvanceStage.put("stageNumber", stage);
    replyAdvanceStage.put("stageName", roundName);
    replyAdvanceStage.put("clientID", sendAddr);
    sendMessage(replyAdvanceStage.toJSONString());

  }


}