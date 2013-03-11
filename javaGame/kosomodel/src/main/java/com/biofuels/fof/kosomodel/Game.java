package com.biofuels.fof.kosomodel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;




public class Game {

  private final String roomName;
  private final boolean hasPassword;
  private boolean contracts;
  private boolean management;
  private final String password;
  private ConcurrentHashMap<Integer, Farm> farms;  //used because doesn't allow annoying null mappings
  private long maxPlayers;
  private RoundManager roundManager;

  /*  private class RoundManager{


    public RoundManager(boolean contracts, boolean management){
      this.contracts = contracts;
      this.management = management;
    }
  }*/


  public Game(String name, long maxPlayers) {
    roomName = name;
    farms = new ConcurrentHashMap<>();
    hasPassword = false;
    password = "";
    roundManager = new RoundManager();
    roundManager.Init(this);
    this.maxPlayers = maxPlayers;
    // TODO Auto-generated constructor stub
  }

  public Game(String name, String pass, long maxPlayers) {
    roomName = name;
    farms = new ConcurrentHashMap<>();
    hasPassword = true;
    password = pass;
    this.maxPlayers = maxPlayers;
    roundManager = new RoundManager();
    roundManager.Init(this);
    // TODO Auto-generated constructor stub
  }

  public String getRoomName() {
    return roomName;
  }

  public boolean hasFarmer(String name) {
    // TODO Auto-generated method stub
    for(Farm f:farms.values()){
      if (f.getName().equals(name))
        return true;
    }
    return false;
  }

  public void addFarmer(String newPlayer, int clientID) {
    // TODO Auto-generated method stub
    Farm f = new Farm(newPlayer, 1000);
    f.setClientID(clientID);
    f.getFields().add(new Field());
    f.getFields().add(new Field());
    farms.put(clientID, f);

  }

  public Boolean hasPassword(){
    return hasPassword;
  }

  public String getPassword(){
    return password;
  }

  public long getMaxPlayers() {
    return maxPlayers;
  }

  public boolean isContracts() {
    return contracts;
  }

  public void setContracts(boolean contracts) {
    this.contracts = contracts;
  }

  public boolean isManagement() {
    return management;
  }

  public void setManagement(boolean management) {
    this.management = management;
  }

  public boolean isFull(){
    return(farms.size() >= maxPlayers);
  }

  public void setField(int clientID, int field, Crop crop){
    farms.get(clientID).getFields().get(field).setCrop(crop);
  }

  public ArrayList<Crop> getFieldsFor(Integer clientID) {
    // TODO Auto-generated method stub
    ArrayList<Crop> cropList = new ArrayList<>();
    for(Field f:farms.get(clientID).getFields()){
      cropList.add(f.getCrop());
    }
    return cropList;
  }

  public ArrayList<Farm> getFarms() {
    // TODO Auto-generated method stub
    return new ArrayList<>(farms.values());
  }

  public Farm getFarm(String name) {
    // TODO Auto-generated method stub
    for(Farm f:farms.values()){
      if (f.getName().equals(name))
        return f;
    }
    return null;
  }

  public void rejoinFarmer(String farmerName, Integer clientID) {
    // TODO Auto-generated method stub
    Farm farm = getFarm(farmerName);
    farms.remove(getFarm(farmerName).getClientID());
    farm.setClientID(clientID);
    farms.put(clientID, farm);
  }

  public Farm getFarm(Integer clientID) {
    // TODO Auto-generated method stub
    return farms.get(clientID);
  }

  public void changeSettings(int fields, boolean contracts, boolean management) {
    // TODO Auto-generated method stub
    int currFields = ((Farm)farms.values().toArray()[0]).getFields().size();
    if(fields < currFields){
      System.out.println("destroying fields not implemented yet");
    }
    else if(fields > currFields){
      for(Farm f:farms.values()){
        for(int i = 0;i<fields - currFields;i++){
          f.getFields().add(new Field());
        }
      }
    }
    this.contracts = contracts;
    this.management = management;
  }


}
