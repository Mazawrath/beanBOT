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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StockMarket {
    private static final RethinkDB r = new RethinkDB();
    public static final String[] COMPANIES = {"BEAN", "FBI", "SHTEB", "BEZFF", "ABD", "BNTC", "BETHS"};
    private static final String DB_VALUE_PREFIX = "P_";
    Connection conn;

    public void connectDatabase() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
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
        if (r.db("beanBotStock").table(serverID).get(userID).hasFields(symbol + " shares bought").run(conn)) {
        } else {
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(new BigDecimal(0)))
            ).run(conn);
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(new BigDecimal(0)))
            ).run(conn);
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
                    symbol = getSymbol(symbol);
                    break;
                case "FBI":
                    companyInfo.put("Name", "Fortnite Burger, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/492901413299552289/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "SHTEB":
                    companyInfo.put("Name", "shteeeb, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/492901390763556864/483457233943003148.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "BEZFF":
                    companyInfo.put("Name", "Beanzer Inc");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489211335830405120/Untitled-1.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "ABD":
                    companyInfo.put("Name", "Advanced Bean Devices, Inc.");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489254732251136012/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "BNTC":
                    companyInfo.put("Name", "Beantel Corporation");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/489277320205565952/Untitled.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "BETHS":
                    companyInfo.put("Name", "Papa BEETHS");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/493885223826751508/Papa_BEETHS.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
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

    public String getComapanyName(String beanSymbol) {
        switch (beanSymbol) {
            case "BEAN":
                return "Bean";
            case "FBI":
                return "Fortnite Burger, Inc.";
            case "SHTEB":
                return "shteeeb, Inc.";
            case "BEZFF":
                return "Beanzer Inc";
            case "ABD":
                return "Advanced Bean Devices, Inc.";
            case "BNTC":
                return "Beantel Corporation";
            case "BETHS":
                return "Papa BEETHS";
            default:
                return null;
        }
    }

    public static String getSymbol(String beanSymbol) {
        switch (beanSymbol) {
            case "BEAN":
                return "AAPL";
            case "FBI":
                return "MCD";
            case "SHTEB":
                return "ATVI";
            case "BEZFF":
                return "RAZFF";
            case "ABD":
                return "AMD";
            case "BNTC":
                return "INTC";
            case "BETHS":
                return "PZZA";
            default:
                return null;
        }
    }

    public BigDecimal getStockPrice(String symbol, boolean beanSymbol) {
        if (beanSymbol) {
            symbol = getSymbol(symbol);
        }
        try {
            Stock stock = YahooFinance.get(symbol);
            return stock.getQuote().getPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getShareInvested(String userID, String serverID, String symbol) {
        return new BigDecimal(parseValueFromDB(r.db("beanBotStock").table(serverID).get(userID).getField(symbol + " shares bought").run(conn))).setScale(Points.SCALE, Points.ROUNDING_MODE);
    }

    public BigDecimal getBeanCoinSpent(String userID, String serverID, String symbol) {
        return new BigDecimal(parseValueFromDB(r.db("beanBotStock").table(serverID).get(userID).getField(symbol + " beanCoin spent").run(conn))).setScale(Points.SCALE, Points.ROUNDING_MODE);
    }

    public BigDecimal buyShares(String userID, String serverID, String symbol, BigDecimal investAmount) {
        BigDecimal retVal = new BigDecimal(-1).setScale(Points.SCALE, Points.ROUNDING_MODE);
        symbol = getSymbol(symbol);

        if (symbol != null) {
            checkUser(userID, serverID);
            checkCompany(userID, serverID, symbol);

            retVal = investAmount.divide(getStockPrice(symbol, false), 2, RoundingMode.HALF_UP);

            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(retVal.add(getShareInvested(userID, serverID, symbol))))).run(conn);
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(getBeanCoinSpent(userID, serverID, symbol).add(investAmount)))).run(conn);
        }

        return retVal;
    }

    public BigDecimal[] sellShares(String userID, String serverID, String symbol) {
        BigDecimal[] retVal = new BigDecimal[2];
        retVal[0] = new BigDecimal(-1).setScale(Points.SCALE, Points.ROUNDING_MODE);

        symbol = getSymbol(symbol);

        if (symbol != null) {
            checkUser(userID, serverID);
            checkCompany(userID, serverID, symbol);
            retVal[0] = getShareInvested(userID, serverID, symbol);
            retVal[1] = getBeanCoinSpent(userID, serverID, symbol);

            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(new BigDecimal(0).setScale(Points.SCALE, Points.ROUNDING_MODE)))
            ).run(conn);
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(new BigDecimal(0).setScale(Points.SCALE, Points.ROUNDING_MODE)))
            ).run(conn);
        }

        return retVal;
    }

    public ArrayList getPortfolio(String userID, String serverID) {
        checkUser(userID, serverID);

        return r.db("beanBotStock").table(serverID).get(userID).run(conn);
    }

    public static String parseValueFromDB(String value) {
        return value.substring(DB_VALUE_PREFIX.length());
    }

    public static String buildValueForDB(BigDecimal value) {
        return DB_VALUE_PREFIX + value.toString();
    }

    public String pointsToString(BigDecimal points) {
        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.US);
        symbol.setCurrencySymbol("\u00DF");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setDecimalFormatSymbols(symbol);

        return formatter.format(points);
    }

    public boolean isProperDecimal(String number) {
        boolean proper = true;
        try {
            BigDecimal decimal = new BigDecimal(number).setScale(Points.SCALE, Points.ROUNDING_MODE);
            if (decimal.compareTo(BigDecimal.ZERO) < 0)
                proper = false;
        } catch (ArithmeticException | NumberFormatException e) {
            proper = false;
        }

        return proper;
    }
}
