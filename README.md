This is an IntelliJ Idea Project. 

How to use server/client application
----------------------------
Please be sure that; <b><i>trade_sector.json, portfolio_info.json, security_sector_info.json</b></i> and <b><i>simulation_data.csv</i></b> files are at the same directory with <i>CreditLimitEngine.jar</i> file.<br/>

to see server application parameters<br/>
<b>java -jar CreditLimitEngine.jar</b>

run server application only with simulation data. Simulation data will be read from <i><b>simulation_data.csv</b></i>. So you need to put this file to same directory.<br/>
<b>java -jar CreditLimitEngine.jar single</b>

run server application with 127.0.0.1:12345 as a server<br/>
<b>java -jar CreditLimitEngine.jar multi 12345</b>

to see client application parameters<br/>
<b>java -jar CreditLimitEngineClient.jar</b>

run client application to connect 127.0.0.1:12345<br/>
<b>java -jar CreditLimitEngineClient.jar 127.0.0.1 12345</b>



How to init server application
----------------------
Server application uses 3 files. These files are needed to init server application to run as a server or as a simulation from file.<br/>

<i><b>portfolio_info.json</b></i> loads portfolio information<br/>
<i><b>security_sector_info.json</b></i> loads securities' sector information. These information will be used for security sector finding for trades that is listed in simulation file or client trade info.<br/>
<i><b>simulation_data.csv</b></i> includes simulation data.<br/> 
<i><b>trade_sector.json</b></i> includes sector names which will supported by engine<br/>

How to use client application
---------------------
When you run client application you will see the prompt below, enter LOGIN to connect server app <br/>
Enter LOGIN command to connect: <b>LOGIN</b><br/>

you can use <b>EXIT</b> command to exit from client app<br/>

you can use <b>HELP</b> command to show available commands<br/>

When you connect to the server, you will see an example command that the server executes.<br/>
<b>
EXEC,S/C,PORTFOLIO_ID,BUY/SELL,SECTOR/SECURITY,AMOUNT<br/>

S for Sector,C for Company/Security<br/>

ex. (EXEC,S,1,BUY,TECH,1000) or (e.g., EXEC,C,1,BUY,ING,1000)<br/>
</b>
<b><br/>
Other avaible commands:
</b>
<br/>
To show portfolio info enter <b>SHOW,PORTFOLIO,PORTFOLIO_ID</b><br/>
ex. <b>SHOW,PORTFOLIO,1</b><br/>

Please check supported  security names from <b>security_sector_info.json</b> file.<br/>

Supported Sector names are -> <b>METAL,FINANCE,TECH,FINTECH,FOOD,RETAIL,HEALTHCARE</b> - these data is defined @ enumeration.<br/> 

Sample commands for client application
--------------------------------------
EXEC,S,1,BUY,TECH,1000 <br/>
EXEC,S,1,BUY,HEALTHCARE,1000<br/>
EXEC,S,1,SELL,HEALTHCARE,1000<br/>
EXEC,C,1,BUY,NOVARTIS,1000<br/>
EXEC,C,1,BUY,ROCHE,1000<br/>
EXEC,C,1,SELL,ROCHE,1000<br/>
EXEC,C,1,BUY,ING,1000<br/>
EXEC,C,1,BUY,FACEBOOK,1000<br/>
EXEC,C,1,SELL,FACEBOOK,1000<br/>
EXEC,C,1,BUY,ING,2000<br/>
EXEC,S,1,BUY,TECH,1000<br/>
EXEC,S,1,BUY,FINANCE,1000<br/>

if there is 2 portfolio defined in <b>portfolio_file.json</b>, you can use 3rd parameter also with <b>2</b>.<br/>
EXEC,S,1,BUY,TECH,1000 <br/>
EXEC,S,2,BUY,HEALTHCARE,1000<br/>
EXEC,S,2,SELL,HEALTHCARE,1000<br/>
EXEC,C,1,BUY,NOVARTIS,1000<br/>
EXEC,C,2,BUY,ROCHE,1000<br/>
EXEC,C,2,SELL,ROCHE,1000<br/>
EXEC,C,1,BUY,ING,1000<br/>
EXEC,C,1,BUY,FACEBOOK,1000<br/>
EXEC,C,2,SELL,FACEBOOK,1000<br/>
EXEC,C,2,BUY,ING,2000<br/>
EXEC,S,1,BUY,TECH,1000<br/>
EXEC,S,1,BUY,FINANCE,1000<br/>

IMAGES
------
![alt text](/images/image_1.png)
![alt text](/images/image_2.png)
![alt text](/images/image_3.png)
![alt text](/images/image_4.png)
