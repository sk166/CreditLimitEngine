package com.kaya.credit.engine;
import com.kaya.credit.datastructures.portfolio.Portfolio;
import com.kaya.credit.datastructures.portfolio.sector.Sector;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;

import java.util.HashMap;
import java.util.Map;

public class CreditLimitCheckEngine {

    private HashMap<Integer, Portfolio> portfolioHashMap;
    private HashMap<String,String> securitySectorMap;
    public CreditLimitCheckEngine(HashMap<Integer, Portfolio> portfolioHashMap,HashMap<String, String> securitySectorMap) {
        this.portfolioHashMap=portfolioHashMap;
        this.securitySectorMap=securitySectorMap;
    }
    public CreditCheckResponse creditCheck(CreditCheckType creditCheckType,int portfolioID, TradeType type, String tradeSector_or_security, double tradeAmount) {
        String tradeSector=TradeSector.NOTSECTOR;
        if(creditCheckType==CreditCheckType.SECURITY)
        {
            if(securitySectorMap.containsKey(tradeSector_or_security))
            {
                tradeSector=securitySectorMap.get(tradeSector_or_security);
            }else {
                return CreditCheckResponse.SECURITY_SECTOR_NOT_DEFINED;
            }
        }else {
            tradeSector=tradeSector_or_security;
        }
        // Check if sector exposure limit is defined
        if(!portfolioHashMap.containsKey(portfolioID))
        {
            return CreditCheckResponse.PORTFOLIO_NOT_AVAILABLE;
        }
        Portfolio portfolio=portfolioHashMap.get(portfolioID);

        if (!portfolio.getSectorLimits().containsKey(tradeSector)) {
            return CreditCheckResponse.SECTOR_LIMIT_NOT_DEFINED;
        }
        //access with lock to each portfolio
        {
            portfolio.getPortfolioLOCK().lock();
            Sector sector = portfolio.getSectorLimits().get(tradeSector);
            double sectorLimit = sector.getLimit();


            double usedLimit = sector.getUsedlimit();

            // Calculate new exposure after the trade
            double newSectorExposure;
            switch (type) {
                case BUY:
                    newSectorExposure = usedLimit + tradeAmount;
                    break;
                case SELL:
                    newSectorExposure = usedLimit - tradeAmount;
                    break;
                default: {
                    portfolio.getPortfolioLOCK().unlock();
                    return CreditCheckResponse.INVALID_TRADE_ACTION_TYPE;
                }
            }

            // Check if the trade breaches the sector exposure limit
            if (newSectorExposure > sectorLimit) {
                portfolio.getPortfolioLOCK().unlock();
                return CreditCheckResponse.TRADE_EXCEEDS_SECTOR_LIMIT; // Trade exceeds sector exposure limit
            }
            if (newSectorExposure < 0) {
                portfolio.getPortfolioLOCK().unlock();
                return CreditCheckResponse.PORTFOLIO_NOT_HAVE_ENOUGH_AMOUNT_TO_SELL;
            }

            // Update sector exposure
            sector.setUsedlimit(newSectorExposure);
            portfolio.getPortfolioLOCK().unlock();
            return CreditCheckResponse.TRUE; // Trade is approved
        }
    }
    public Portfolio getPortfolioInfo(int portfolioID)
    {
        return portfolioHashMap.get(portfolioID);
    }
    public String showPortfolioInfo(int portfolioID)
    {
        StringBuilder sb=new StringBuilder();
        if(!portfolioHashMap.containsKey(portfolioID))
        {
            sb.append("PORTFOLIO NOT AVAILABLE");
            return sb.toString();
        }else {
            sb.append(String.format("PORTFOLIO [%d] INFO", portfolioID));
            sb.append(portfolioHashMap.get(portfolioID).toString()).append("\n");
            return sb.toString();
        }
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("ACTIVE PORTFOLIO LIST\n");
        for(Map.Entry<Integer, Portfolio> entry : portfolioHashMap.entrySet())
        {
            sb.append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}