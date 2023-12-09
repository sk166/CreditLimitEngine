package com.kaya.credit.datastructures.portfolio.sector;

public class Sector {
    private double limit;
    private String sector;

    public double getUsedlimit() {
        return usedlimit;
    }

    public void setUsedlimit(double usedlimit) {
        this.usedlimit = usedlimit;
    }

    public double getRemainingLimit()
    {
        return limit - usedlimit;
    }
    public boolean checkThereIsLimit()
    {
        return limit>usedlimit;
    }

    private double usedlimit;

    public Sector(String sector,double limit) {
        this.limit = limit;
        this.sector=sector;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return sector.toString()+" : "+usedlimit+"/"+limit;
    }
}
