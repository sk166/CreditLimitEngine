package com.kaya.credit.clientserver;

import com.kaya.credit.datastructures.portfolio.Portfolio;
import com.kaya.credit.engine.CreditCheckType;
import com.kaya.credit.engine.CreditLimitCheckEngine;
import com.kaya.credit.engine.CreditCheckResponse;
import com.kaya.credit.datastructures.portfolio.sector.TradeSector;
import com.kaya.credit.engine.TradeType;
import com.kaya.credit.helpers.test.Trade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server {
    private int port;
    private CreditLimitCheckEngine engine;
    private TradeSector tradeSector;
    public Server(int port,CreditLimitCheckEngine engine,TradeSector tradeSector)
    {
        this.port=port;
        this.engine=engine;
        this.tradeSector=tradeSector;
    }
    public void init()
    {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress()+":"+clientSocket.getPort());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                // Perform login
                String loginCommand = reader.readLine();
                if ("LOGIN".equals(loginCommand)) {
                    writer.println("LOGIN_SUCCESSFULL");
                } else {
                    writer.println("LOGIN_FAILED");
                    return; // Exit the thread if login fails
                }

                // Continue handling commands until the client disconnects or sends an "EXIT" command
                while (true) {
                    // Execute command with data
                    String executeCommand = reader.readLine();

                    if (executeCommand == null || "EXIT".equals(executeCommand)) {
                        // Client disconnected or sent EXIT command
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        break;
                    }

                    String[] commandParts = executeCommand.toUpperCase().trim().split(",");

                    if (commandParts.length == 6 && "EXEC".equals(commandParts[0].trim())) {
                        // Process the command data
                        int portfolioID=-1;
                        TradeType tradeType=TradeType.INVALID;
                        String sector=TradeSector.NOTSECTOR;
                        String security="";
                        double amount=-1D;
                        CreditCheckType creditCheckType=CreditCheckType.SECTOR;
                        try {
                            if(commandParts[1].trim().compareTo("S")==0) {
                                if(tradeSector.contains(commandParts[4].trim()))
                                {
                                    sector = commandParts[4].trim();
                                }else {
                                    throw new Exception("SECTOR_NOT_AVAILABLE");
                                }
                                creditCheckType=CreditCheckType.SECTOR;
                            }else if(commandParts[1].trim().compareTo("C")==0)
                            {
                                security=commandParts[4].trim();
                                creditCheckType=CreditCheckType.SECURITY;
                            }
                            portfolioID = Integer.parseInt(commandParts[2].trim());
                            tradeType = TradeType.valueOf(commandParts[3].trim());
                            amount = Double.parseDouble(commandParts[5].trim());
                            if(portfolioID>-1 && tradeType!=TradeType.INVALID) {
                                String response="COMMAND_PROCESS_ERROR";
                                if(creditCheckType==CreditCheckType.SECTOR)
                                {
                                    response = processCommand(creditCheckType,portfolioID, tradeType, sector, amount);
                                }else {
                                    response = processCommand(creditCheckType,portfolioID, tradeType, security, amount);
                                }
                                // Send the response back to the client
                                writer.println(response);
                            }
                        }catch (Exception ex)
                        {
                            writer.println("COMMAND_CREATE_ERROR_EXEC");
                        }

                    }else if (commandParts.length == 3 && "SHOW".equals(commandParts[0].trim()) && "PORTFOLIO".equals(commandParts[1].trim()))
                    {
                        try {
                            int portfolioId = Integer.parseInt(commandParts[2].trim());
                            Portfolio portfolio = engine.getPortfolioInfo(portfolioId);
                            writer.println(portfolio.getPortfolioInfo());
                        }catch (Exception ex)
                        {
                            writer.println("COMMAND_CREATE_ERROR_SHOW");
                        }
                    }
                    else {
                        writer.println("INVALID_COMMAND");
                    }
                }
            } catch (IOException e) {
                System.out.printf("CONNECTION_TERMINATED [%s]%n", e.getMessage());
            }
        }

        private String processCommand(CreditCheckType creditCheckType,int portfolioID, TradeType tradeType, String tradeSector_or_security, double tradeAmount) {
            CreditCheckResponse response = engine.creditCheck(creditCheckType,portfolioID, tradeType, tradeSector_or_security, tradeAmount);
            if(creditCheckType==CreditCheckType.SECTOR)
            {
                System.out.printf("Trade Type:%s- Trade Sector:%s- Amount:%s%n", tradeType, tradeSector_or_security, tradeAmount);
            }else {
                System.out.println("Trade Type:" + tradeType + "- Trade Security:" + tradeSector_or_security + "- Amount:" + tradeAmount);
            }
            System.out.printf("CreditCheck Result: %s%n", response.toString());
            System.out.printf("Portfolio Status: %s%n", engine.showPortfolioInfo(portfolioID));
            return response.toString();
        }
    }
}
