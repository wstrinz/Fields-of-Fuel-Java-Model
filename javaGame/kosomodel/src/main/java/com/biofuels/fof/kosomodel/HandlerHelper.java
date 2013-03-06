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

  public HandlerHelper() {
    // TODO Auto-generated constructor stub
  }

  public HandlerHelper(ActorRef listener) {
    // TODO Auto-generated constructor stub
    this.listener = listener;
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
    String farmerName = (String) eventObj.get("userName");

    switch (eventObj.get("event").toString()){

    case "validateRoom":
      if(games.get(roomName) != null){
        listener.tell(new EventMessage(buildJson(clientID, "validateRoom", "result", false)));
        //        replies.add("{\"event\":\"validateRoom\",\"result\":false}");
      }
      else{
        //            System.out.println("j tell success " + event + " to " + listener);
        listener.tell(new EventMessage(buildJson(clientID, "validateRoom", "result", true)));
        //        replies.add("{\"event\":\"validateRoom\",\"result\":true}");
      }
      break;

    case "changeSettings":
      //replies.add(event);
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
              sendMessage(buildJson(clientID, "createRoom", "result", false));
              sendMessage(buildJson(clientID, "createRoom", "result", false));
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
            sendMessage(buildJson(clientID, "createRoom","result",true));
            //replies.add("{\"event\":\"createRoom\",\"result\":true}");
            break;

    case "validateUserName":
      boolean roomResult = (roomExists(roomName) && !games.get(roomName).isFull());
      boolean nameResult = false;
      boolean needsPass = false;
      boolean correctPass = false;
      if(roomResult){
        nameResult = !eventObj.get("userName").equals("") && !farmerExistsInRoom(farmerName, roomName);//!games.get(roomName).hasFarmer((String) eventObj.get(eventObj.get("userName")));
        needsPass = games.get(roomName).hasPassword();
        if(needsPass){
          correctPass = games.get(roomName).getPassword().equals(eventObj.get("password"));
        }
      }
      sendMessage(buildJson(clientID, "validateUserName","roomResult",roomResult,"needsPassword",needsPass,
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

    case "joinRoom":
      if(roomExists(roomName) && (!farmerExistsInRoom(farmerName, roomName) && !games.get(roomName).isFull())
          && games.get(roomName).getPassword().equals(eventObj.get("password")))
      {
        games.get(roomName).addFarmer(farmerName, clientID);
        sendMessage(buildJson(clientID, "joinRoom","result",true,"roomName",roomName,"userName",(String)eventObj.get("userName")));
      }
      else
        sendMessage(buildJson(clientID, "joinRoom","result",false));
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
    listener.tell(new EventMessage(message));
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
  private String buildJson(int clientID, String event, Object ... arguments){
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