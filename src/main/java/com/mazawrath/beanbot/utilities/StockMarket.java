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
    private static final String DB_NAME = "beanBotStock";
    public static final String[] COMPANIES = {"BEAN", "FBI", "SHTEB", "BEZFF", "ABD", "BNTC", "BETHS", "BEAB", "WEEB"};
    private static final String DB_VALUE_PREFIX = "P_";
    Connection conn;

    public StockMarket() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
    }

    private void checkTable(Connection conn) {
        if (r.dbList().contains(DB_NAME).run(conn)) {
        } else {
            r.dbCreate(DB_NAME).run(conn);
        }
    }

    private void checkServer(String serverID) {
        if (r.db(DB_NAME).tableList().contains(serverID).run(conn)) {
        } else
            r.db(DB_NAME).tableCreate(serverID).run(conn);

    }

    private void checkUser(String userID, String serverID) {
        checkServer(serverID);

        if (r.db(DB_NAME).table(serverID).getField("id").contains(userID).run(conn)) {
        } else
            r.db(DB_NAME).table(serverID).insert(r.array(
                    r.hashMap("id", userID)
            )).run(conn);
    }

    private void checkCompany(String userID, String serverID, String symbol) {
        if (r.db(DB_NAME).table(serverID).get(userID).hasFields(symbol + " shares bought").run(conn)) {
        } else {
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(new BigDecimal(0)))
            ).run(conn);
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(new BigDecimal(0)))
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
                case "BEAB":
                    companyInfo.put("Name", "Blissful Beans");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/489203676863397889/513049848258363403/unknown.png");
                    companyInfo.put("Symbol", symbol);
                    symbol = getSymbol(symbol);
                    break;
                case "WEEB":
                    companyInfo.put("Name", "MicroWeeb");
                    companyInfo.put("Logo", "https://cdn.discordapp.com/attachments/480959729330290688/519694521144049674/Untitled.png");
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

    public String getCompanyName(String beanSymbol) {
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
            case "BEAB":
                return "Blissful Beans";
            case "WEEB":
                return "MicroWeeb";
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
            case "BEAB":
                return "SBUX";
            case "WEEB":
                return "MSFT";
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
        checkUser(userID, serverID);
        checkCompany(userID, serverID, symbol);
        return new BigDecimal(parseValueFromDB(r.db(DB_NAME).table(serverID).get(userID).getField(symbol + " shares bought").run(conn))).setScale(Points.SCALE, Points.ROUNDING_MODE);
    }

    public BigDecimal getBeanCoinSpent(String userID, String serverID, String symbol) {
        checkUser(userID, serverID);
        checkCompany(userID, serverID, symbol);
        return new BigDecimal(parseValueFromDB(r.db(DB_NAME).table(serverID).get(userID).getField(symbol + " beanCoin spent").run(conn))).setScale(Points.SCALE, Points.ROUNDING_MODE);
    }

    public BigDecimal buyShares(String userID, String serverID, String symbol, BigDecimal investAmount) {
        BigDecimal retVal = new BigDecimal(-1).setScale(Points.SCALE, Points.ROUNDING_MODE);
        symbol = getSymbol(symbol);

        if (symbol != null) {
            checkUser(userID, serverID);
            checkCompany(userID, serverID, symbol);

            retVal = investAmount.divide(getStockPrice(symbol, false), 2, RoundingMode.HALF_UP);

            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(retVal.add(getShareInvested(userID, serverID, symbol))))).run(conn);
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(getBeanCoinSpent(userID, serverID, symbol).add(investAmount)))).run(conn);
        }

        return retVal;
    }

    public boolean sellShares(String userID, String serverID, String symbol) {
        boolean retVal = false;

        symbol = getSymbol(symbol);

        if (symbol != null) {
            checkUser(userID, serverID);
            checkCompany(userID, serverID, symbol);

            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " shares bought", buildValueForDB(new BigDecimal(0).setScale(Points.SCALE, Points.ROUNDING_MODE)))
            ).run(conn);
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap(symbol + " beanCoin spent", buildValueForDB(new BigDecimal(0).setScale(Points.SCALE, Points.ROUNDING_MODE)))
            ).run(conn);

            retVal = true;
        }

        return retVal;
    }

    public ArrayList getPortfolio(String userID, String serverID) {
        checkUser(userID, serverID);

        return r.db(DB_NAME).table(serverID).get(userID).run(conn);
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
