package com.kaya.credit.datastructures.portfolio;

import com.kaya.credit.datastructures.portfolio.sector.Sector;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Portfolio {
    private final ReentrantLock portfolioLOCK = new ReentrantLock();

    private double portfolioMaxValue;
    private int portfolioID;
    private HashMap<String, Sector> sectorLimits=new HashMap<>();

    public Portfolio(int portfolioID,double portfolioMaxValue,  HashMap<String,Double> sectorExposureInfo) {
        this.portfolioMaxValue = portfolioMaxValue;
        this.portfolioID = portfolioID;
        // can be added
        for (Map.Entry<String, Double> entry : sectorExposureInfo.entrySet()) {
            String sectorcode = entry.getKey();
            Double percent = entry.getValue();
            Sector sector=new Sector(sectorcode,portfolioMaxValue*percent/100);
            sectorLimits.put(sectorcode,sector);
        }
        System.out.println("\nPortfolio ["+portfolioID+"] Initialized With:");
        System.out.println("Portfolio Max Value:"+portfolioMaxValue);
        System.out.println("Portfolio Sector Credit Values:");
        for(Map.Entry<String, Sector> entry : sectorLimits.entrySet())
        {
            System.out.println(entry.getValue().toString());
        }
    }
    public ReentrantLock getPortfolioLOCK() {
        return portfolioLOCK;
    }

    public double getPortfolioMaxValue() {
        return portfolioMaxValue;
    }

    public void setPortfolioMaxValue(double portfolioMaxValue) {
        this.portfolioMaxValue = portfolioMaxValue;
    }

    public int getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(int portfolioID) {
        this.portfolioID = portfolioID;
    }

    public HashMap<String, Sector> getSectorLimits() {
        return sectorLimits;
    }

    public void setSectorLimits(HashMap<String, Sector> sectorLimits) {
        this.sectorLimits = sectorLimits;
    }

    public String getPortfolioInfo()
    {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("Portfolio [%d] Info:", portfolioID));
        sb.append(String.format("Portfolio Max Value:%s", portfolioMaxValue));
        sb.append(" - Portfolio Sector Credit Values:");
        for(Map.Entry<String, Sector> entry : sectorLimits.entrySet())
        {
            sb.append("|"+entry.getValue().toString()+"|");
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("\n----------------\n");
        sb.append(String.format("Portfolio [%d] Info:\n", portfolioID));
        sb.append(String.format("Portfolio Max Value:%s\n", portfolioMaxValue));
        sb.append("Portfolio Sector Credit Values:\n");
        for(Map.Entry<String, Sector> entry : sectorLimits.entrySet())
        {
            sb.append(entry.getValue().toString()+"\n");
        }
        sb.append("----------------\n");
        return sb.toString();
    }
}
