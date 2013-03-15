package com.biofuels.fof.kosomodel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.biofuels.fof.kosomodel.gameStage.GameStage;




public class Game {

  private final String roomName;
  private final boolean hasPassword;
  private boolean contracts=false;
  private boolean management=false;
  private final String password;
  private ConcurrentHashMap<Integer, Farm> farms;  //used because doesn't allow annoying null mappings
  private long maxPlayers;
  private RoundManager roundManager;
  private int gameYear=0;

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
    roundManager.AdvanceStage();
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
    roundManager.AdvanceStage();
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

  public ArrayList<String> getFieldsFor(Integer clientID) {
    // TODO Auto-generated method stub
    ArrayList<String> cropList = new ArrayList<>();
    for(Field f:farms.get(clientID).getFields()){
      cropList.add(f.getCrop().toString());
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
    int currFields = 2;
    if(farms.size()>0){
      currFields = ((Farm)farms.values().toArray()[0]).getFields().size();
    }

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
    roundManager.resetStages();
  }

  public List<Field> getFields(Integer clientID) {
    // TODO Auto-generated method stub
    return farms.get(clientID).getFields();
  }

  public int getYear() {
    // TODO Auto-generated method stub
    return gameYear;
  }

  public int getStageNumber() {
    // TODO Auto-generated method stub
    return roundManager.getCurrentStageNumber();
  }

  public List<String> getEnabledStages() {
    // TODO Auto-generated method stub
    ArrayList<String> ret = new ArrayList<String>();
    List<GameStage> stages = roundManager.getEnabledStages();
    for (GameStage s:stages){
      ret.add(s.getName());
    }
    return ret;
  }

  public void advanceStage() {
    // TODO Auto-generated method stub
    roundManager.AdvanceStage();
  }

  public boolean isFinalRound() {
    // TODO Auto-generated method stub
    return false;
  }

  public String getStageName() {
    // TODO Auto-generated method stub
    return roundManager.getCurrentStageName();
  }

  public int getCapitalRank(Integer clientID) {
    // TODO Auto-generated method stub
    return -1;
  }

  public void sellFarmerCrops() {
    // TODO Auto-generated method stub
    for(Farm f:farms.values()){
      int profit = 0;
      for(Field fi:f.getFields()){
        if(fi.getCrop().equals(Crop.CORN)){
          profit += 2000;
        }
        else if(fi.getCrop().equals(Crop.GRASS)){
          profit += 300;
        }
      }
      f.setCapital(f.getCapital()+profit);
    }
  }

  public void clearFields() {
    // TODO Auto-generated method stub
    for(Farm f:farms.values()){
      for(Field fi:f.getFields()){
        if(fi.getCrop().equals(Crop.CORN))
          fi.setCrop(Crop.FALLOW);
      }
    }
  }


}
