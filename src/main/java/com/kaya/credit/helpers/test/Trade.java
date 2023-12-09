package com.kaya.credit.helpers.test;

import com.kaya.credit.engine.CreditCheckType;
import com.kaya.credit.engine.TradeType;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;

import java.util.UUID;
//used for tests
public class Trade {

    private int portfolioID;
    private UUID tradeID;
    private double amount;
    private TradeType tradeType;

    private String tradeSectorOrSecurityName;
    private CreditCheckType creditCheckType;

    public Trade(CreditCheckType creditCheckType, int portfolioID,UUID tradeID, double amount, TradeType tradeType, String tradeSector_securityname) {
        this.portfolioID=portfolioID;
        this.tradeID = tradeID;
        this.amount = amount;
        this.tradeType = tradeType;
        this.tradeSectorOrSecurityName = tradeSector_securityname;
        this.creditCheckType=creditCheckType;
    }
    public int getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(int portfolioID) {
        this.portfolioID = portfolioID;
    }
    public UUID getTradeID() {
        return tradeID;
    }

    public void setTradeID(UUID tradeID) {
        this.tradeID = tradeID;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeSectorOrSecurityName() {
        return tradeSectorOrSecurityName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public CreditCheckType getCreditCheckType() {
        return creditCheckType;
    }

    public void setCreditCheckType(CreditCheckType creditCheckType) {
        this.creditCheckType = creditCheckType;
    }
}
