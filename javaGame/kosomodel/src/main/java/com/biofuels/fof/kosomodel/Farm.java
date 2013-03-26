package com.biofuels.fof.kosomodel;

import java.util.LinkedList;

public class Farm {

  private String name;
  private int clientID;
//  private Field[] fields;
  private LinkedList<Field> fields;
  private int capital=10000;
  private double envScore;
  private double energyScore;
  private double econScore;
  private int envRank;
  private int energyRank;
  private int econRank;
  private String currentUser;
  private boolean acceptCornContract;
  private boolean acceptSwitchgrassContract;
  private boolean ready;
  private double phosphorous;
  private double GBI; //Grassland Bird Index


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
    this.energyScore = calcEnergyScore();
  }

  public double getEnvScore() {
    return envScore;
  }

  public double calcEnvScore() {
    double avgSoc = 0;
    for(Field f:fields){
      avgSoc += f.getSOC();
    }
    avgSoc /= fields.size();
    double c_score = avgSoc / 190;
    double p_score = 0.20151 - ((this.getPhosphorous() - 0.02239) / 0.20151);

    return c_score * .5 + p_score * .5;
  }

  public int getCapital() {
    return capital;
  }

  public double getEnergyScore() {
    return energyScore;
  }

  public double calcEnergyScore() {
    return this.energyScore;
  }

  public double getEconScore() {
    return econScore;
  }

  public int calcEconScore() {
    return this.capital;
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
    capital = i;
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  public double getPhosphorous() {
    return phosphorous;
  }

  public void updatePhosphorous() {
    double cornCount =0;
    for(Field f:this.fields){
      if(f.getCrop() == Crop.CORN){
        cornCount ++;
      }
    }
    double cornRatio = cornCount / this.fields.size();
    this.phosphorous = Math.pow(10, (.79 * cornRatio) - 1.44);

  }

  public double getGBI() {
    return GBI;
  }

  public void updateGBI() {
    //this is going to take some spatial specificty, so save for later
    //Fields are about 65 meters on each side if they are each an acre, so for now maybe
  }

  public int getEnvRank() {
    return envRank;
  }

  public void setEnvRank(int envRank) {
    this.envRank = envRank;
  }

  public int getEnergyRank() {
    return energyRank;
  }

  public void setEnergyRank(int energyRank) {
    this.energyRank = energyRank;
  }

  public int getEconRank() {
    return econRank;
  }

  public void setEconRank(int econRank) {
    this.econRank = econRank;
  }

}
