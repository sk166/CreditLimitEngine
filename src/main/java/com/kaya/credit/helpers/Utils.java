package com.kaya.credit.helpers;

import com.google.gson.*;
import com.kaya.credit.datastructures.portfolio.Portfolio;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;
import com.kaya.credit.datastructures.security.Security;
import com.kaya.credit.datastructures.security.SecurityData;
import com.kaya.credit.helpers.test.Trade;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static void loadTradeSector(TradeSector tradeSector)
    {
        try (Reader reader = new FileReader("trade_sector.json")) {
            Gson gson = new Gson();

            String[] stringArray = gson.fromJson(reader, String[].class);

            if(stringArray.length==0)
            {
                System.out.println("Please define at least 1 sector @ trade_sector file");
                System.exit(1);
            }
            // Converting the array into a HashSet
            tradeSector.init(new HashSet<>(Set.of(stringArray)));
            System.out.println("Sector info loaded");
            System.out.println("------------------------");
            System.out.println("Listing Sectors");
            for(String entry : tradeSector.getValues())
            {
                System.out.println("Sector:"+entry);
            }
            System.out.println("------------------------");
        } catch (IOException e) {
            System.out.println("trade_sector.json not loaded correctly. Please check the file");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void loadSecuritySector(Map<String, String> securitySectorMap,TradeSector tradeSector)
    {
        try (Reader reader = new FileReader("security_sector_info.json")) {
            Gson gson = new Gson();
            SecurityData[] securityDataArray = gson.fromJson(reader, SecurityData[].class);

            // Creating a HashMap to store securities and their sectors

            for (SecurityData sector : securityDataArray) {
                String sectorName = sector.getSector().toUpperCase();
                for (Security security : sector.getSecurities()) {
                    String securityName = security.getName().toUpperCase();
                    if(tradeSector.contains(sectorName)) {
                        securitySectorMap.put(securityName, sectorName);
                    }else {
                        System.out.println("TradeSector not available for "+sectorName);
                    }
                }
            }
            System.out.println("Security sector info loaded");
            System.out.println("Loaded Security Sectors");
            for(Map.Entry<String, String> entry : securitySectorMap.entrySet())
            {
                System.out.println("Security:"+entry.getKey()+"-"+entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("security_sector_info.json not loaded correctly. Please check the file");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void loadPortfolio(HashMap<Integer, Portfolio> portfolioHashMap)
    {
        try (FileReader fileReader = new FileReader("portfolio_info.json")) {
            JsonElement jsonElement = JsonParser.parseReader(fileReader);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                for (JsonElement jsonElementItem : jsonArray) {
                    JsonObject jsonObject = jsonElementItem.getAsJsonObject();

                    int portfolioID = jsonObject.get("portfolioID").getAsInt();
                    double portfolioMaxValue = jsonObject.get("portfolioMaxValue").getAsDouble();

                    JsonArray sectorArray = jsonObject.getAsJsonArray("sector");
                    HashMap<String, Double> sectorLimitInfo=new HashMap<>();
                    for (JsonElement sectorElement : sectorArray) {
                        JsonObject sectorObject = sectorElement.getAsJsonObject();

                        String sectorName = sectorObject.get("sectorname").getAsString().toUpperCase();
                        String tradeSector=sectorName;
                        double rate = sectorObject.get("rate").getAsDouble();
                        sectorLimitInfo.put(tradeSector,rate);
                    }
                    Portfolio newPortfolio=new Portfolio(portfolioID,portfolioMaxValue,sectorLimitInfo);
                    portfolioHashMap.put(portfolioID,newPortfolio);
                }
            }

        } catch (Exception e) {
            System.out.println("portfolio_info.json not loaded correctly. Please check the file");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Loaded Portfolios");
        for(Map.Entry<Integer, Portfolio> entry : portfolioHashMap.entrySet())
        {
            System.out.print(entry.getValue().toString());
        }
    }
}
