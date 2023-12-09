package com.kaya;

import com.kaya.credit.engine.CreditCheckType;
import com.kaya.credit.engine.CreditLimitCheckEngine;
import com.kaya.credit.clientserver.Server;
import com.kaya.credit.engine.CreditCheckResponse;
import com.kaya.credit.datastructures.portfolio.Portfolio;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;
import com.kaya.credit.engine.TradeType;
import com.kaya.credit.helpers.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Portfolio> portfolioHashMap=new HashMap<>();
        HashMap<String, String> securitySectorMap = new HashMap<>();
        TradeSector tradeSectorValues=new TradeSector();

        if(args.length==0)
        {
            noargs_info();
            return;
        }else if(args.length==1 && args[0].compareTo("--help")==0)
        {
            one_args_or_help();
            return;
        }
        loadConfiguration(tradeSectorValues, securitySectorMap, portfolioHashMap);
        if(portfolioHashMap.isEmpty())
        {
            System.out.println("Please add at least 1 portfolio to portfolio_info.json file");
            System.exit(1);
        }
        CreditLimitCheckEngine engine = new CreditLimitCheckEngine(portfolioHashMap,securitySectorMap);
        if(args[0].compareTo("single") == 0)//single threaded test from simulation csv
        {
            // Example usage with simulation data from a CSV file
            System.out.println(engine.toString());

            try (BufferedReader reader = new BufferedReader(new FileReader("simulation_data.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] params = line.split(",");
                    int portfolioID = Integer.parseInt(params[0].trim());
                    String tradeSectorOrSecurity=TradeSector.NOTSECTOR;
                    String securityName="";

                    TradeType tradeType = TradeType.valueOf(params[2].trim());
                    double tradeAmount = Double.parseDouble(params[4].trim());
                    CreditCheckResponse response;
                    CreditCheckType creditCheckType;
                    if(params[1].trim().compareTo("S")==0)
                    {
                        creditCheckType=CreditCheckType.SECTOR;
                    }else {
                        creditCheckType=CreditCheckType.SECURITY;
                    }

                    tradeSectorOrSecurity = params[3].trim();
                    response = engine.creditCheck(creditCheckType,portfolioID, tradeType, tradeSectorOrSecurity, tradeAmount);
                    if(creditCheckType==CreditCheckType.SECTOR) {
                        System.out.println("Portfolio:" + portfolioID + " Trade Type:" + tradeType + "- Trade Sector:" + tradeSectorOrSecurity + "- Amount:" + tradeAmount);
                    }else {
                        System.out.println("Portfolio:" + portfolioID + " Trade Type:" + tradeType + "- Security Name:" + tradeSectorOrSecurity + "- Amount:" + tradeAmount);
                    }

                    System.out.println("CreditCheck Result: " + response.toString());
                    System.out.println("Portfolio Status: " + engine.showPortfolioInfo(portfolioID));
                }
            } catch (IOException e) {
                System.out.println("simulation_data.csv not loaded correctly. Please check the file");
                e.printStackTrace();
                System.exit(1);
            }
        }else if(args[0].compareTo("multi")==0) //multithreaded app
        {
            int port=12345;
            if(args.length==2) //argument 2 is for port information
            {
                try
                {
                    port=Integer.parseInt(args[1]);
                }catch (Exception ex)
                {
                    System.out.println("PORT parsing error using default port: "+port);
                }
            }
            runServer(port,engine,tradeSectorValues);
        }
    }

    private static void loadConfiguration(TradeSector tradeSectorValues, HashMap<String, String> securitySectorMap, HashMap<Integer, Portfolio> portfolioHashMap) {
        Utils.loadTradeSector(tradeSectorValues);
        Utils.loadSecuritySector(securitySectorMap, tradeSectorValues);
        Utils.loadPortfolio(portfolioHashMap);
    }

    private static void one_args_or_help() {
        System.out.println("Application needs [simulation_data.csv] for single mode");
        System.out.println("Application needs [portfolio_info.json] for both single/multi mode");
        System.out.println("Application need [trade_sector.json] for both single/multi node");
        System.out.println("Available parameters:");
        System.out.println("CreditLimitEngine [single] is to use the app with simulation_data.csv file");
        System.out.println("CreditLimitEngine [multi] is to run the app as server mode");
        System.out.println("CreditLimitEngine [multi] [port] is to run app as server with specified port");
    }

    private static void noargs_info() {
        System.out.println("Available parameters:");
        System.out.println("CreditLimitEngine [single] is to use the app with simulation_data.csv file");
        System.out.println("CreditLimitEngine [multi] is to run the app as server mode");
        System.out.println("CreditLimitEngine [multi] [port] is to run app as server with specified port");
        System.out.println("------------------------------------------------------------");
        System.out.println("Application needs [simulation_data.csv] for single mode");
        System.out.println("Application needs [portfolio_info.json] for both single/multi mode");
    }

    private static void runServer(int port,CreditLimitCheckEngine engine,TradeSector tradeSector)
    {
        Server server=new Server(port,engine,tradeSector);
        server.init();
    }


}