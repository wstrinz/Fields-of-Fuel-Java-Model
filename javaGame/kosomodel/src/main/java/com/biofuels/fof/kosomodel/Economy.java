package com.biofuels.fof.kosomodel;

public class Economy {
	private boolean complex;
	private int pEnergy;
	private int energyPerUnitCorn;
	private int energyPerUnitGrass;
	private int fixedCost;
	private double variableAlpha;
	public Economy(){
		complex = true;
	}
	public int getpEnergy() {
		return pEnergy;
	}
	public void setpEnergy(int pEnergy) {
		this.pEnergy = pEnergy;
	}
	public int getEnergyPerUnitCorn() {
		return energyPerUnitCorn;
	}
	public void setEnergyPerUnitCorn(int energyPerUnitCorn) {
		this.energyPerUnitCorn = energyPerUnitCorn;
	}
	public int getEnergyPerUnitGrass() {
		return energyPerUnitGrass;
	}
	public void setEnergyPerUnitGrass(int energyPerUnitGrass) {
		this.energyPerUnitGrass = energyPerUnitGrass;
	}
	public int getFixedCost() {
		return fixedCost;
	}
	public void setFixedCost(int fixedCost) {
		this.fixedCost = fixedCost;
	}
	public double getVariableAlpha() {
		return variableAlpha;
	}
	public void setVariableAlpha(float variableAlpha) {
		this.variableAlpha = variableAlpha;
	}
	public boolean isComplex() {
		return complex;
	}
	
	public double computeQforP(int p, Crop crop){
		int g;
		if(crop == Crop.CORN)
			g = energyPerUnitCorn;
		else
			g = energyPerUnitGrass;
		
		return Math.pow(((pEnergy * g - p)/(variableAlpha+1)),(1/variableAlpha));
	}
	
	public double computePforQ(Integer q, Crop crop){
		int g;
		if(crop == Crop.CORN)
			g = energyPerUnitCorn;
		else
			g = energyPerUnitGrass;
		return ((pEnergy * g - Math.pow(q.doubleValue(), variableAlpha) - variableAlpha * Math.pow(q.doubleValue(), variableAlpha)));
	}
	

}
