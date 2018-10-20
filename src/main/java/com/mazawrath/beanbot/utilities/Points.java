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

public class Points {
    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_VALUE_PREFIX = "P_";
    public static final int SCALE = 2;
    public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    public static final BigDecimal ZERO_POINTS = (BigDecimal.ZERO).setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal FREE_POINTS = new BigDecimal("25.69").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST = new BigDecimal("2.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST_SPECIAL = new BigDecimal("10.00").setScale(SCALE, ROUNDING_MODE);
    private Connection conn;

    public void connectDatabase() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
    }

    private boolean checkTable(Connection conn) {
        if (r.dbList().contains("beanBotPoints").run(conn)) {
            return true;
        } else {
            r.dbCreate("beanBotPoints").run(conn);
            return true;
        }
    }

    private void checkServer(String serverID) {
        if (r.db("beanBotPoints").tableList().contains(serverID).run(conn)) {
        } else
            r.db("beanBotPoints").tableCreate(serverID).run(conn);

    }

    private void checkUser(String userID, String serverID) {
        checkServer(serverID);

        if (r.db("beanBotPoints").table(serverID).getField("id").contains(userID).run(conn)) {
        } else
            r.db("beanBotPoints").table(serverID).insert(r.array(
                    r.hashMap("id", userID)
                            .with("Points", buildValueForDB(ZERO_POINTS))
                            .with("Last Received Free Points", 0)
            )).run(conn);
    }

    public ArrayList getLeaderboard(String serverID) {
        return r.db("beanBotPoints").table(serverID).map(doc ->
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

        return new BigDecimal(parseValueFromDB(r.db("beanBotPoints").table(serverID).get(userID).getField("Points").run(conn))).setScale(SCALE, ROUNDING_MODE);
    }

    public void addPoints(String userID, String serverID, BigDecimal points) {
        checkUser(userID, serverID);

        r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).add(points)))).run(conn);
    }

    public boolean removePoints(String userID, String botUserID, String serverID, BigDecimal points) {
        checkUser(userID, serverID);
        if (botUserID != null && !botUserID.isEmpty()) {
            checkUser(botUserID, serverID);
        }

        if (points.compareTo(getBalance(userID, serverID)) <= 0) {
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).subtract(points)))).run(conn);
            if (botUserID != null && !botUserID.isEmpty()) {
                addPoints(botUserID, serverID, points);
            }
            return true;
        } else
            return false;
    }

    public long giveFreePoints(String userID, String serverID) {
        checkUser(userID, serverID);
        long timeLeft = r.db("beanBotPoints").table(serverID).get(userID).getField("Last Received Free Points").run(conn);

        if (System.currentTimeMillis() - timeLeft > 24 * 60 * 60 * 1000) {
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", buildValueForDB(getBalance(userID, serverID).add(FREE_POINTS)))).run(conn);
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Last Received Free Points", System.currentTimeMillis())).run(conn);
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
