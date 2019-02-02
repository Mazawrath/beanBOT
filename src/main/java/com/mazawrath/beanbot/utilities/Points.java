package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Points {
    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "beanBotPoints";
    private static final String DB_VALUE_PREFIX = "P_";
    public static final int SCALE = 2;
    public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    public static final BigDecimal ZERO_POINTS = (BigDecimal.ZERO).setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal FREE_POINTS = new BigDecimal("25.69").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST = new BigDecimal("2.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST_SPECIAL = new BigDecimal("10.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal LOTTERY_TICKET_COST = new BigDecimal("40.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal LOTTERY_DRAWING_COST = new BigDecimal("20000.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal GOOGLE_VISION_COST = new BigDecimal("50.00").setScale(SCALE, ROUNDING_MODE);
    private Connection conn;

    private static final BigDecimal NUMBER_TO_PERCENT = new BigDecimal(.01);

    public Points(Connection conn) {
        this.conn = conn;

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
                            .with("Points", buildValueForDB(ZERO_POINTS))
                            .with("Last Received Free Points", 0)
            )).run(conn);
    }

    public ArrayList getLeaderboard(String serverID) {
        return r.db(DB_NAME).table(serverID).map(doc ->
                r.object(
                        "id",
                        doc.getField("id"),
                        "Points",
                        doc.getField("Points").split("P_").nth(1).coerceTo("NUMBER"),
                        "Last Received Free Points",
                        doc.getField("Last Received Free Points")
                )
        ).orderBy(r.desc("Points")).limit(10).map(doc ->
                r.object(
                        "id",
                        doc.getField("id"),
                        "Points",
                        r.expr("P_").add(doc.getField("Points").coerceTo("STRING")),
                        "Last Received Free Points",
                        doc.getField("Last Received Free Points")
                )
        ).run(conn);
    }

    public BigDecimal getBalance(String userID, String serverID) {
        checkUser(userID, serverID);

        return new BigDecimal(parseValueFromDB(r.db(DB_NAME).table(serverID).get(userID).getField("Points").run(conn))).setScale(SCALE, ROUNDING_MODE);
    }

    public void addPoints(String userID, String serverID, BigDecimal points) {
        checkUser(userID, serverID);

        r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).add(points)))).run(conn);
    }

    public boolean removePoints(String userID, String botUserID, String serverID, BigDecimal points) {
        checkUser(userID, serverID);
        if (botUserID != null && !botUserID.isEmpty()) {
            checkUser(botUserID, serverID);
        }

        if (points.compareTo(getBalance(userID, serverID)) <= 0) {
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).subtract(points)))).run(conn);
            if (botUserID != null && !botUserID.isEmpty()) {
                Random r = new Random();
                BigDecimal blackHolePercent = new BigDecimal(r.nextInt(25 - 8 + 1) + 8).multiply(NUMBER_TO_PERCENT);
                BigDecimal mysteriousTax = points.multiply(blackHolePercent);

                addPoints(botUserID, serverID, points.subtract(mysteriousTax));
            }
            return true;
        } else
            return false;
    }

    public long giveFreePoints(String userID, String serverID) {
        checkUser(userID, serverID);
        long timeLeft = r.db(DB_NAME).table(serverID).get(userID).getField("Last Received Free Points").run(conn);

        if (System.currentTimeMillis() - timeLeft > 24 * 60 * 60 * 1000) {
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).add(FREE_POINTS)))).run(conn);
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Last Received Free Points", System.currentTimeMillis())).run(conn);
            return 0;
        }
        return timeLeft;
    }

    public static String parseValueFromDB(String value) {
        return value.substring(DB_VALUE_PREFIX.length());
    }

    public static String buildValueForDB(BigDecimal value) {
        return DB_VALUE_PREFIX + value.toString();
    }

    public static String pointsToString(BigDecimal points) {
        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.US);
        symbol.setCurrencySymbol("\u00DF");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setDecimalFormatSymbols(symbol);

        return formatter.format(points);
    }

    public static boolean isProperDecimal(String number) {
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
