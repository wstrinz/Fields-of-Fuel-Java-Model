package com.biofuels.fof.kosomodel;
import java.util.Random;


public class Field {



  private Crop crop;
  private ManagementOptions management;
  private double SOC;
  private FieldHistory history;

  public Field() {
    setCrop(Crop.CORN);
    management = new ManagementOptions();
    history = new FieldHistory();
    this.SOC=50;
  }

  private Crop randomCrop() {
    // TODO Auto-generated method stub
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

    private boolean till;
    private boolean pesticide;
    private boolean fertilize;
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
    int cornVal = this.crop == crop.CORN ? 1 : 0;
    int grassVal = this.crop == crop.GRASS ? 1 : 0;
    int coverVal = (this.crop == crop.COVER || this.crop == crop.FALLOW) ? 1 : 0;
    int noTill = this.management.till ? 0 : 1;
    double B0 = 0.8;
    double B1 = 1.17;
    double B2 = 1.04;
    double B3 = 0; //1.1;
    SOC = SOC * (((B0 * cornVal) + (B1 * grassVal) + (B2 * coverVal) + (B3 * noTill))); /// 20);
    if (SOC > 150) //set max of 150 for now (note: not in official model spec)
      SOC = 150;
  }

  public double getSOM() {
    return SOC;
  }

  public void setSOM(float SOC) {
    this.SOC = SOC;
  }

  public FieldHistory getHistory() {
    return history;
  }

  public void addHistoryYear() {
    history.addYear(SOC, crop, 1, isFertilize(), isPesticide(), isTill()); //note yield is a placeholder for now
  }

}
