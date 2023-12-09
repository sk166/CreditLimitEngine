package com.kaya.credit.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1";

        int serverPort = 12345;
        if(args.length==0)
        {
            System.out.println("Default server ip is 127.0.0.1 and default port is 12345");
            System.out.println("Available parameters:");
            System.out.println("CreditLimitEngineClient [serverip] [port] is to run app as client to connect a server ip with specified port");
            System.exit(0);
        }
        if(args.length==2)
        {
            try
            {
                serverIp=args[0];
                serverPort=Integer.parseInt(args[1]);
                System.out.println("Using IP "+serverIp+"-"+serverPort+" port to connect SERVER");
            }catch (Exception ex)
            {
                System.out.println("Using default IP/port to connect SERVER :"+serverIp+"/"+serverPort);
            }
        }
        try (
                Socket socket = new Socket(serverIp, serverPort);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            // Send login command
            System.out.print("Enter LOGIN command to connect: ");
            String loginCommand = consoleReader.readLine();
            writer.println(loginCommand.toUpperCase());

            // Wait for the server's response
            String loginResponse = reader.readLine();
            System.out.println("Server response: " + loginResponse);

            if ("LOGIN_SUCCESSFULL".equals(loginResponse.trim())) {
                System.out.println("Enter EXIT to close connection");
                System.out.println("Enter HELP to show available commands");
                System.out.println("AVAILABLE COMMANDS");
                System.out.println("------------------");
                System.out.println("EXEC,S/C,PORTFOLIO_ID,BUY/SELL,SECTOR/SECURITY,AMOUNT");
                System.out.println("S for Sector,C for Company/Security");
                System.out.println("ex. (EXEC,S,1,BUY,TECH,1000) or (e.g., EXEC,C,1,BUY,ING,1000)");
                System.out.println("-------------------------------------------------------------");
                System.out.println("To show portfolio enter SHOW,PORTFOLIO,PORTFOLIO_ID");
                System.out.println("ex. SHOW,PORTFOLIO,1");
                System.out.println("--------------------");
                System.out.println("EXIT");
                System.out.println("----");
                System.out.println("HELP");
                System.out.println("----");
                while(true) {
                    // Send execute command with data

                    System.out.print("Enter execute command : ");
                    String executeCommand = consoleReader.readLine();
                    if(executeCommand.toUpperCase().startsWith("EXIT"))
                    {
                        System.out.println("Exiting...");
                        break;
                    }else if(executeCommand.toUpperCase().startsWith("HELP"))
                    {
                        System.out.println("AVAILABLE COMMANDS");
                        System.out.println("------------------");
                        System.out.println("EXEC,S/C,PORTFOLIO_ID,BUY/SELL,SECTOR/SECURITY,AMOUNT");
                        System.out.println("S for Sector,C for Company/Security");
                        System.out.println("ex. (EXEC,S,1,BUY,TECH,1000) or (e.g., EXEC,C,1,BUY,ING,1000)");
                        System.out.println("-------------------------------------------------------------");
                        System.out.println("To show portfolio enter SHOW,PORTFOLIO,PORTFOLIO_ID");
                        System.out.println("ex. ( SHOW,PORTFOLIO,1 )");
                        System.out.println("------------------------");
                        System.out.println("EXIT");
                        System.out.println("----");
                        System.out.println("HELP");
                        System.out.println("----");
                        continue;
                    }
                    writer.println(executeCommand.toUpperCase());

                    // Wait for the server's response
                    String executeResponse = reader.readLine();
                    System.out.println("---------------");
                    System.out.println("Server response: " + executeResponse);
                    System.out.println("---------------");
                }
            } else {
                System.out.println("Login failed. Exiting...");
            }
        } catch (IOException e) {
            System.out.println("Error :"+e.getMessage());
            e.printStackTrace();
        }
    }
}