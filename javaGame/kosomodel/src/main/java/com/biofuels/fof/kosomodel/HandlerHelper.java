package com.biofuels.fof.kosomodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import akka.actor.ActorRef;

public class HandlerHelper {

  public static Map<String, Game> games = new HashMap<>();
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

  @SuppressWarnings({ "unchecked", "deprecation" }) //not sure why a one-way send is deprecated...
  public String[] handle(String event){
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
      //uncomment to test concurrency
      /*System.out.print("sleeping\n");
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            System.out.print("waking\n");*/;
            if(games.get(roomName) != null){
              sendMessage(buildJson(clientID.toString(), "createRoom", "result", false));
              sendMessage(buildJson(clientID.toString(), "createRoom", "result", false));
              //        replies.add("{\"event\":\"createRoom\",\"result\":false}");
            }
            else if(((String)eventObj.get("password")).length()>0){
              //        String pass = (String)eventObj.get("password");
              //        long players = (long)eventObj.get("playerCount");
              //            System.out.println("pass " + pass + " players " + (long)eventObj.get("playerCount"));
              games.put(roomName, new Game(roomName, (String)eventObj.get("password"), (long)eventObj.get("playerCount")));
            }
            else{
              games.put(roomName, new Game(roomName, (long)eventObj.get("playerCount")));
            }
            sendMessage(buildJson(clientID.toString(), "createRoom","result",true));
            //replies.add("{\"event\":\"createRoom\",\"result\":true}");
            break;

    case "validateUserName":
       roomResult = (roomExists(roomName) && !games.get(roomName).isFull());
      boolean nameResult = false;
       needsPass = false;
       correctPass = false;
       if(roomResult){
        System.out.println("user " + eventObj.get("userName"));
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
      JSONObject fields = new JSONObject();
      msg.put("event", "loadFromServer");
      msg.put("clientID", clientID);
      list.addAll(games.get(roomName).getFieldsFor(clientID));
      fields.put("fields", JSONValue.toJSONString(list));
      msg.putAll(fields);
      System.out.println(msg.toJSONString());
      sendMessage(msg.toJSONString());

      break;

    case "getFarmerList":
      list = new JSONArray();
      //JSONObject farmers = new JSONObject();
      msg = new JSONObject();
      for(Farm f:games.get(roomID).getFarms()){
        JSONObject farm = new JSONObject();
        farm.put("name", f.getName());
        farm.put("ready", true);
//        list.add("\""+f.getName()+"\"");
//        list.add(false);
        list.add(farm);
      }
      msg.put("event", "farmerList");
      msg.put("clientID", clientID);
      msg.put("Farmers", list);
      System.out.println(msg.toJSONString());
      sendMessage(msg.toJSONString());
    break;

    case "plantField":
      //System.out.println("planting");
      games.get(roomID).getFarm(clientID).setField(((Long)eventObj.get("field")).intValue(),(String) eventObj.get("crop"));
    break;

    case "joinRoom":
      boolean roomExist = roomExists(roomName);
      boolean shouldMakeNew = false;
      boolean shouldRejoin = false;
      if(roomExist){
        shouldMakeNew = !farmerExistsInRoom(farmerName, roomName) && !games.get(roomName).isFull();
        if(!shouldMakeNew){

          shouldRejoin = deviseName != null && games.get(roomName).getFarm(farmerName).getCurrentUser().equals(deviseName);
          System.out.println("can rejoin? " + shouldRejoin);
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
        list = new JSONArray();
        //JSONObject farmers = new JSONObject();
        msg = new JSONObject();
        for(Farm f:games.get(roomName).getFarms()){
          JSONObject farm = new JSONObject();
          farm.put("name", f.getName());
          farm.put("ready", true);
//          list.add("\""+f.getName()+"\"");
//          list.add(false);
          list.add(farm);
        }
        msg.put("event", "farmerList");
        msg.put("clientID", roomName);
        msg.put("Farmers", list);
//        System.out.println(msg.toJSONString());
        sendMessage(msg.toJSONString());
        //sendMessage(buildJson(roomName,"farmerList"));
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

  private void sendMessage(String message) {
    // TODO Auto-generated method stub
    EventMessage msg = new EventMessage(message);
    System.out.println("sending " + msg.message);
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

  //for testing
  /*    public static void main(String[] args) {

      String teststr = new String("{\"event\":\"createRoom\",\"roomName\":\"room\"}");
      EventHandler meself = new EventHandler();
      meself.handle(teststr);

    }*/
}