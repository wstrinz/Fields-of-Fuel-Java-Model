package com.biofuels.fof.kosomodel;
import java.util.Random;


public class Field {



  private Crop crop;
  private ManagementOptions management;

  public Field() {
    setCrop(randomCrop());
    management = new ManagementOptions();
    // TODO Auto-generated constructor stub
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


}
