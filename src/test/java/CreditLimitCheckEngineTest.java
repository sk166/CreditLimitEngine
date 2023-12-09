import com.kaya.credit.datastructures.portfolio.Portfolio;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;
import com.kaya.credit.engine.CreditCheckResponse;
import com.kaya.credit.engine.CreditCheckType;
import com.kaya.credit.engine.CreditLimitCheckEngine;
import com.kaya.credit.engine.TradeType;
import com.kaya.credit.helpers.test.Trade;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreditLimitCheckEngineTest {

    @Test
    public void testCreditCheck() {

        ArrayList<Trade> testdata=new ArrayList<Trade>();
        UUID trade1=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade1,5000D, TradeType.BUY, "TECH"));
        UUID trade2=UUID.randomUUID();//causes negative exposure
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade2,4000D,TradeType.SELL,"FINANCE"));
        UUID trade2_1=UUID.randomUUID();//increases exposure of FINANCE
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade2_1,4000D,TradeType.BUY,"FINANCE"));
        UUID trade2_2=UUID.randomUUID();//decreases exposure of FINANCE
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade2_2,2000D,TradeType.SELL,"FINANCE"));
        UUID trade3=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade3,4000D,TradeType.BUY,"TECH"));
        UUID trade4=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade4,8000D,TradeType.BUY,"HEALTHCARE"));
        UUID trade5=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade5,10000D,TradeType.SELL,"NOTSECTOR"));
        UUID trade6=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECTOR,1,trade6,10000D,TradeType.INVALID,"FINANCE"));
        UUID trade7=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECURITY,1,trade7,2000D,TradeType.BUY,"ING"));
        UUID trade8=UUID.randomUUID();
        testdata.add(new Trade(CreditCheckType.SECURITY,1,trade8,2000D,TradeType.SELL,"GOOGLE"));

        HashMap<Integer, Portfolio> portfolioHashMap=new HashMap<>();
        HashMap<String, Double> sectorLimitInfo=new HashMap<>();
        sectorLimitInfo.put("TECH",40D);
        sectorLimitInfo.put("FINANCE",60D);
        Portfolio newPortfolio=new Portfolio(1,20000D,sectorLimitInfo);
        portfolioHashMap.put(1,newPortfolio);

        HashMap<String, String> securitySectorMap=new HashMap<>();
        securitySectorMap.put("ING", "FINANCE");
        securitySectorMap.put("GOOGLE","TECH");
        CreditLimitCheckEngine engine = new CreditLimitCheckEngine(portfolioHashMap,securitySectorMap);

        for (Trade trd:testdata) {
            System.out.println("Checking trade id: "+trd.getTradeID());
            CreditCheckResponse response;

            response = engine.creditCheck(trd.getCreditCheckType(),trd.getPortfolioID(), trd.getTradeType(), trd.getTradeSectorOrSecurityName(), trd.getAmount());

            boolean result=false;
            switch (response)
            {
                case TRUE:
                    result=true;
                    break;
                case SECTOR_LIMIT_NOT_DEFINED:
                case INVALID_TRADE_ACTION_TYPE:
                case BIGGER_THAN_PORTFOLIO_MAX_VALUE:
                case TRADE_EXCEEDS_SECTOR_LIMIT:
                    result=false;
                    break;
            }
            //System.out.println's are added to see result text
            System.out.println("RESULT="+response.toString());
            if(trade1.equals(trd.getTradeID())) //BUY 5000 TECH
            {
                assertTrue(result); //TECH BUY TRADE 5000 - limit is 8000 - 5000 = 3000
            }else if(trade2.equals(trd.getTradeID())) //SELL 4000 FINANCE
            {
                assertFalse(result); //FINANCE position is 0 - sell causes creditlimit return false
            }else if(trade2_1.equals(trd.getTradeID()))//BUY 4000 FINANCE
            {
                assertTrue(result); // FINANCE BUY 4000  - limit is 12000 - 4000 = 8000
            }else if(trade2_2.equals(trd.getTradeID()))
            {
                assertTrue(result); // FINANCE SELL 2000 - limit is 8000 + 2000 = 10000
            }
            else if(trade3.equals(trd.getTradeID()))
            {
                assertFalse(result);//TECH BUY 4000 - limit is 3000 - return false because of no limit
            }else if(trade4.equals(trd.getTradeID()))
            {
                assertFalse(result); //HEATHCARE not included @ credit definition
            }else if(trade5.equals(trd.getTradeID()))
            {
                assertFalse(result);//SECTOR NOT DEFINED
            }else if(trade6.equals(trd.getTradeID()))
            {
                assertFalse(result);//INVALID TRADE TYPE
            }else if(trade7.equals(trd.getTradeID()))//Security BUY trade
            {
                assertTrue(result);//FINANCE BUY 2000 - limit is 10000 - 2000 = 8000
            }else if(trade8.equals(trd.getTradeID()))//Security SELL trade
            {
                assertTrue(result); //TECH SELL 2000 - limit is 3000 + 2000 = 5000
            }
        }
        assertEquals(3000D, engine.getPortfolioInfo(1).getSectorLimits().get("TECH").getUsedlimit());
        assertEquals(4000D, engine.getPortfolioInfo(1).getSectorLimits().get("FINANCE").getUsedlimit());
        assertEquals(5000D, engine.getPortfolioInfo(1).getSectorLimits().get("TECH").getRemainingLimit());
        assertEquals(8000D, engine.getPortfolioInfo(1).getSectorLimits().get("FINANCE").getRemainingLimit());

    }
}