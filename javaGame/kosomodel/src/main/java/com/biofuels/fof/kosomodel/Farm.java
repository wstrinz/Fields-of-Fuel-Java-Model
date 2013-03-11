package com.biofuels.fof.kosomodel;

public class Farm {

  private String name;
  private int clientID;
  private Field[] fields;
  private int capital;
  private int envScore;
  private int socScore;
  private int econScore;
  private String currentUser;
  private boolean acceptCornContract;
  private boolean acceptSwitchgrassContract;


  public Farm(String name, int capital) {
    this.name = name;
    this.capital = capital;
    fields = new Field[2];
    //    fields[0].setCrop(Crop.GRASS);
    // TODO Auto-generated constructor stub
  }

  public Field[] getFields(){
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
    System.out.println("planting " + crop + " on field " + fieldNum);
    switch (crop){
    case "grass":
      fields[fieldNum].setCrop(Crop.GRASS);
      break;
    case "corn":
      fields[fieldNum].setCrop(Crop.CORN);
      break;
    case "none":
      fields[fieldNum].setCrop(Crop.FALLOW);
      break;
    }
  }


}
