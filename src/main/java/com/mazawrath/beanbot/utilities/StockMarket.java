package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.json.JSONArray;
import org.json.JSONObject;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class StockMarket {
    public static final RethinkDB r = RethinkDB.r;
    Connection conn;

    public Connection connectDatabase() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
        return conn;
    }

    private boolean checkTable(Connection conn) {
        if (r.dbList().contains("beanBotStock").run(conn)) {
            return true;
        } else {
            r.dbCreate("beanBotStock").run(conn);
            return true;
        }
    }

    private void checkServer(String serverID) {
        if (r.db("beanBotStock").tableList().contains(serverID).run(conn)) {
        } else
            r.db("beanBotStock").tableCreate(serverID).run(conn);

    }

    private void checkUser(String userID, String serverID) {
        checkServer(serverID);

        if (r.db("beanBotStock").table(serverID).getField("id").contains(userID).run(conn)) {
        } else
            r.db("beanBotStock").table(serverID).insert(r.array(
                    r.hashMap("id", userID)
            )).run(conn);
    }

    private void checkCompany(String userID, String serverID, String symbol) {
        if (r.db("beanBotStock").table(serverID).getField(symbol).contains(userID).run(conn)) {
        } else {
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + " shares bought", 0)
            )).run(conn);
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + "beanCoin spent", 0)
            )).run(conn);
        }
    }

    public boolean checkMarketStatus() {
        try {
            Stock thing = YahooFinance.get("APPL");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONArray getCompanies(String[] symbol) {
        String jsonString = "[";

        for (int i = 0; i < symbol.length; i++) {
            try {
                if (i == 0) {
                    jsonString += getCompanyInfo(symbol[i]).toString();
                } else {
                    jsonString += "," + getCompanyInfo(symbol[i]).toString();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        jsonString += "]";

        return new JSONArray(jsonString);
    }

    public static JSONObject getCompanyInfo(String symbol) {
        try {
            JSONObject companyInfo = new JSONObject();
            switch (symbol) {
                case "BEAN":
                    companyInfo.put("Name", "Bean");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489204157442424833/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "AAPL";
                    break;
                case "FBI":
                    companyInfo.put("Name", "Fortnite Burger, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/492901413299552289/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "MCD";
                    break;
                case "SHTEB":
                    companyInfo.put("Name", "shteeeb, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/492901390763556864/483457233943003148.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "ATVI";
                    break;
                case "BEZFF":
                    companyInfo.put("Name", "Beanzer Inc");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489211335830405120/Untitled-1.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "RAZFF";
                    break;
                case "ABD":
                    companyInfo.put("Name", "Advanced Bean Devices, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489254732251136012/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "AMD";
                    break;
                case "BNTC":
                    companyInfo.put("Name", "Beantel Corporation");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489277320205565952/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "INTC";
                    break;
                case "BETHS":
                    companyInfo.put("Name", "Papa BEETHS");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/493885223826751508/Papa_BEETHS.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = "PZZA";
                    break;
                default:
                    return null;
            }

            Stock company = YahooFinance.get(symbol);

            companyInfo.put("Price", company.getQuote().getPrice());
            companyInfo.put("Previous Close", company.getQuote().getPreviousClose());
            companyInfo.put("Year High", company.getQuote().getYearHigh());
            companyInfo.put("Year Low", company.getQuote().getYearLow());
            companyInfo.put("50 Day Percentage Change", company.getQuote().getChangeFromAvg50InPercent());
            companyInfo.put("Percentage Change", company.getQuote().getChangeInPercent());

            return companyInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getStockPrice(String Symbol) {
        try {
            Stock stock = YahooFinance.get(Symbol);
            return stock.getQuote().getPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getShareInvested(String userID, String serverID, String symbol) {


        return null;
    }

    public void buyShares(String userID, String serverID, String symbol, int investAmount) {
        checkUser(userID, serverID);
        checkCompany(userID, serverID, symbol);

        r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + " shares bought", getStockPrice(symbol).divide(new BigDecimal(investAmount), 2, RoundingMode.HALF_UP).add(getShareInvested(userID, serverID, symbol))))).run(conn);
        //TODO Add calculations to adding up how much beanCoin is spent on shares
        r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + " beanCoin spent", investAmount))).run(conn);
    }

    public void sellShares(String userID, String serverID, String symbol, int investAmount) {
        checkUser(userID, serverID);
        checkCompany(userID, serverID, symbol);

        r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + " shares bought", getStockPrice(symbol).divide(new BigDecimal(investAmount), 2, RoundingMode.HALF_UP).add(getShareInvested(userID, serverID, symbol))))).run(conn);
        //TODO Add calculations to adding up how much beanCoin is spent on shares
        r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Stock", r.hashMap(symbol + " beanCoin spent", investAmount))).run(conn);
    }

    public ArrayList getPortfolio(String userID, String serverID) {
        checkUser(userID, serverID);

        return r.db("beanBotStock").table(serverID).get(userID).run(conn);
    }
}
