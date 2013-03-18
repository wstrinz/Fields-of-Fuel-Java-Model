package com.biofuels.fof.kosomodel;

import java.util.LinkedList;

public class FieldHistory {
  class HistoryYear {
    public int year;
    public double SOM;
    public Crop crop;
    public double yield;
  }

  private LinkedList<HistoryYear> history;
  public FieldHistory(){
    history = new LinkedList<>();
  }
  public LinkedList<HistoryYear> getHistory() {
    return history;
  }

  public void addYear(double SOM, Crop crop, double yield){
    HistoryYear newYear = new HistoryYear();
    newYear.year = history.size();
    newYear.crop = crop;
    newYear.yield = yield;
    newYear.SOM = SOM;
    this.history.add(newYear);
  }

}
