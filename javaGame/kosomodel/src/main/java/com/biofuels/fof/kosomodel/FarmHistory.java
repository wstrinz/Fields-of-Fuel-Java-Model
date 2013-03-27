package com.biofuels.fof.kosomodel;

import java.util.LinkedList;

public class FarmHistory {
  class HistoryYear {
    public int year;
    public int earnings;
    public double soilSubscore;
    public double waterSubscore;
//    public double energySubscore;
//    public double economicsSubscore;
    public double sustainabilityScore;
    public double economicsScore;
    public double environmentScore;
    public double energyScore;
    public double sustainabilityRank;
    public double economicsRank;
    public double environmentRank;
    public double energyRank;
    public double cornIncome;
    public double switchgrassIncome;
    public double cornYield;
    public double grassYield;
  }

  private LinkedList<HistoryYear> history;
  public FarmHistory(){
    history = new LinkedList<>();
  }
  public LinkedList<HistoryYear> getHistory() {
    return history;
  }

  public void addYear(int earnings, double soil, double water, double sustainability, double economics, double environment,
      double energy, double sustainabilityRank, double economicsRank, double environmentRank,
      double energyRank, double cornIncome, double grassIncome, double cornYield, double grassYield){

    HistoryYear newYear = new HistoryYear();
    newYear.year = history.size();
    newYear.earnings = earnings;
    newYear.soilSubscore = soil;
    newYear.waterSubscore = water;
    newYear.sustainabilityScore = sustainability;
    newYear.environmentScore = environment;
    newYear.switchgrassIncome = grassIncome;
    newYear.energyScore = energy;
    newYear.economicsScore = economics;
    newYear.cornIncome = cornIncome;
    newYear.switchgrassIncome = grassIncome;
    newYear.cornYield = cornYield;
    newYear.grassYield = grassYield;
    newYear.sustainabilityRank = sustainabilityRank;
    newYear.economicsRank = economicsRank;
    newYear.environmentRank = environmentRank;
    newYear.energyRank = energyRank;

    this.history.add(newYear);
  }
}
