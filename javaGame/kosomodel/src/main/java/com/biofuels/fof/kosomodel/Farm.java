package com.biofuels.fof.kosomodel;

import java.util.LinkedList;

public class Farm {

  private String name;
  private int clientID;
//  private Field[] fields;
  private LinkedList<Field> fields;
  private int capital=10000;
  private int envScore;
  private int socScore;
  private int econScore;
  private String currentUser;
  private boolean acceptCornContract;
  private boolean acceptSwitchgrassContract;


  public Farm(String name, int capital) {
    this.name = name;
    this.capital = capital;
    fields = new LinkedList<Field>();
    //    fields[0].setCrop(Crop.GRASS);
    // TODO Auto-generated constructor stub
  }

  public LinkedList<Field> getFields(){
    return fields;
  }

  public void recomputeScores(){
    this.econScore = calcEconScore();
    this.envScore = calcEnvScore();
    this.socScore = calcSocScore();
  }

  public int getEnvScore() {
    return envScore;
  }

  public int calcEnvScore() {
    return this.envScore;
  }

  public int getCapital() {
    return capital;
  }

  public int getSocScore() {
    return socScore;
  }

  public int calcSocScore() {
    return this.socScore;
  }

  public int getEconScore() {
    return econScore;
  }

  public int calcEconScore() {
    return this.econScore;
  }

  public String getName() {
    return name;
  }

  public int getClientID() {
    return clientID;
  }

  public void setClientID(int clientID) {
    this.clientID = clientID;
  }

  public boolean isAcceptCornContract() {
    return acceptCornContract;
  }

  public void setAcceptCornContract(boolean acceptCornContract) {
    this.acceptCornContract = acceptCornContract;
  }

  public boolean isAcceptSwitchgrassContract() {
    return acceptSwitchgrassContract;
  }

  public void setAcceptSwitchgrassContract(boolean acceptSwitchgrassContract) {
    this.acceptSwitchgrassContract = acceptSwitchgrassContract;
  }

  public String getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

  public void setField(Integer fieldNum, String crop) {
    // TODO Auto-generated method stub
//    System.out.println("planting " + crop + " on field " + fieldNum);
    switch (crop){
    case "grass":
      fields.get(fieldNum).setCrop(Crop.GRASS);
      break;
    case "corn":
      fields.get(fieldNum).setCrop(Crop.CORN);
      break;
    case "none":
      fields.get(fieldNum).setCrop(Crop.FALLOW);
      break;
    }
  }

  public void changeFieldManagement(int fieldnum, String technique, boolean value) {
    // TODO Possibly add checks for if managment is on?
    Field field = fields.get(fieldnum);
    switch (technique){
    case "fertilizer":
      field.setFertilize(value);
      break;
    case "pesticide":
      field.setPesticide(value);
      break;
    case "tillage":
      field.setTill(value);
      break;

    }

  }

  public void setCapital(int i) {
    // TODO Auto-generated method stub
    capital = i;
  }


}
