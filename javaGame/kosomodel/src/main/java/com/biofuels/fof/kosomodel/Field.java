package com.biofuels.fof.kosomodel;
import java.util.Random;


public class Field {



  private Crop crop;
  private ManagementOptions management;
  private double SOC;
  private FieldHistory history;
  private double lastYield;

  public Field() {
    setCrop(Crop.CORN);
    management = new ManagementOptions();
    history = new FieldHistory();
    this.SOC=50;
  }

  private Crop randomCrop() {
    Random r = new Random();
    if(r.nextInt(2)==0)
      return Crop.CORN;
    return Crop.GRASS;
  }

  private class ManagementOptions{

    public ManagementOptions(boolean ... opts){
      if(opts.length > 0)
        till = opts[0];
      if(opts.length > 1)
        pesticide = opts[1];
      if(opts.length > 2)
        fertilize = opts[2];
    }

    private boolean till=true; //TODO does this make sense?
    private boolean pesticide=false;
    private boolean fertilize=false;
  }

  public Crop getCrop() {
    return crop;
  }

  public void setCrop(Crop crop) {
    this.crop = crop;
  }

  public boolean isTill() {
    return management.till;
  }
  public void setTill(boolean till) {
    management.till = till;
  }

  public boolean isPesticide() {
    return management.pesticide;
  }

  public void setPesticide(boolean pesticide) {
    management.pesticide = pesticide;
  }

  public boolean isFertilize() {
    return management.fertilize;
  }
  public void setFertilize(boolean fertilize) {
    management.fertilize = fertilize;
  }
  public ManagementOptions getManagement() {
    return management;
  }

  public void setManagement(ManagementOptions management) {
    this.management = management;
  }

  public void updateSOM() {
    int cornVal = this.crop == Crop.CORN ? 1 : 0;
    int grassVal = this.crop == Crop.GRASS ? 1 : 0;
    int coverVal = (this.crop == Crop.COVER || this.crop == Crop.FALLOW) ? 1 : 0;
    int noTill = this.management.till ? 0 : 1;
    double B0 = 0.8;
    double B1 = 1.17;
    double B2 = 1.04;
    double B3 = 1.1;
    SOC = SOC * (((B0 * cornVal) + (B1 * grassVal) + (B2 * coverVal) + (B3 * noTill))); /// 20);

    int MAXSOC = 300 ;
    if (SOC > MAXSOC) //set max of 150 for now (note: not in official model spec)
      SOC = MAXSOC;
  }

  public double getSOC() {
    return SOC;
  }

  public void setSOM(float SOC) {
    this.SOC = SOC;
  }

  public FieldHistory getHistory() {
    return history;
  }

  public void addHistoryYear() {
    history.addYear(SOC, crop, calculateYield(), isFertilize(), isPesticide(), isTill());
  }

  public double getLastYield() {
    return lastYield;
  }

  public void setLastYield(double lastYield) {
    this.lastYield = lastYield;
  }

  public double calculateYield() {
    double B0Corn = 0.1377;
    double B0Grass = -0.9556;
    double B0Cover = -0.9556;

    double B1Corn = 3.4142;
    double B1Grass = 2.4093;
    double B1Cover = 2.40141;

    int cornVal = this.getCrop() == Crop.CORN ? 1 : 0;
    int grassVal = this.getCrop() == Crop.GRASS ? 1 : 0;
    int coverVal = (this.getCrop() == Crop.COVER || this.getCrop() == Crop.FALLOW) ? 1 : 0;

    double B0 = B0Corn * cornVal + B0Grass * grassVal + B0Cover * coverVal;
    double B1 = B1Corn * cornVal + B1Grass * grassVal + B1Cover * coverVal;

    return B0 + B1 * Math.log(this.getSOC());
  }

}
