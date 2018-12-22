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
    private static final String DB_NAME = "beanBotPoints";
    private static final String DB_VALUE_PREFIX = "P_";
    public static final int SCALE = 2;
    public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    public static final BigDecimal ZERO_POINTS = (BigDecimal.ZERO).setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal FREE_POINTS = new BigDecimal("25.69").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST = new BigDecimal("2.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal COMMAND_COST_SPECIAL = new BigDecimal("10.00").setScale(SCALE, ROUNDING_MODE);
    public static final BigDecimal LOTTERY_TICKET_COST = new BigDecimal("10.00").setScale(SCALE, ROUNDING_MODE);
    private Connection conn;

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

    private void checkServer(String serverId) {
        if (r.db(DB_NAME).tableList().contains(serverId).run(conn)) {
        } else
            r.db(DB_NAME).tableCreate(serverId).run(conn);
    }

    private void checkUser(String userId, String serverId) {
        checkServer(serverId);

        if (r.db(DB_NAME).table(serverId).getField("id").contains(userId).run(conn)) {
        } else
            r.db(DB_NAME).table(serverId).insert(r.array(
                    r.hashMap("id", userId)
                            .with("Points", buildValueForDB(ZERO_POINTS))
                            .with("Last Received Free Points", 0)
            )).run(conn);
    }

    private void checkBeanmas (String userId, String serverId) {
        if (r.db(DB_NAME).table(serverId).get(userId).hasFields("beanmas").run(conn)) {
        } else {
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("beanmas", buildValueForDB(getBalance(userId, serverId)))
            ).run(conn);
        }
    }

    public ArrayList getLeaderboard(String serverId) {
        return r.db(DB_NAME).table(serverId).map(doc ->
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

    public BigDecimal getBalance(String userId, String serverId) {
        checkUser(userId, serverId);

        return new BigDecimal(parseValueFromDB(r.db(DB_NAME).table(serverId).get(userId).getField("Points").run(conn))).setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getBeanmasBalance(String userId, String serverId) {
        checkUser(userId, serverId);
        checkBeanmas(userId, serverId);

        return new BigDecimal(parseValueFromDB(r.db(DB_NAME).table(serverId).get(userId).getField("beanmas").run(conn))).setScale(SCALE, ROUNDING_MODE);
    }

    public void addPoints(String userId, String serverId, BigDecimal points) {
        checkUser(userId, serverId);
        checkBeanmas(userId, serverId);

        r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("Points", buildValueForDB(getBalance(userId, serverId).add(points)))).run(conn);
    }

    public boolean removePoints(String userId, String botUserID, String serverId, BigDecimal points) {
        checkUser(userId, serverId);
        checkBeanmas(userId, serverId);
        if (botUserID != null && !botUserID.isEmpty()) {
            checkUser(botUserID, serverId);
        }

        if (points.compareTo(getBalance(userId, serverId)) <= 0) {
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("Points", buildValueForDB(getBalance(userId, serverId).subtract(points)))).run(conn);
            if (botUserID != null && !botUserID.isEmpty()) {
                addPoints(botUserID, serverId, points);
            }
            return true;
        } else
            return false;
    }

    public long giveFreePoints(String userId, String serverId) {
        checkUser(userId, serverId);
        checkBeanmas(userId, serverId);
        long timeLeft = r.db(DB_NAME).table(serverId).get(userId).getField("Last Received Free Points").run(conn);

        if (System.currentTimeMillis() - timeLeft > 24 * 60 * 60 * 1000) {
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("Points", buildValueForDB(getBalance(userId, serverId).add(FREE_POINTS)))).run(conn);
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("Last Received Free Points", System.currentTimeMillis())).run(conn);
            return 0;
        }
        return timeLeft;
    }

    public int removeBeanmas(String userId, String botUserID, String serverId, BigDecimal points) {
        checkUser(userId, serverId);
        checkBeanmas(userId, serverId);

        if (botUserID != null && !botUserID.isEmpty()) {
            checkUser(botUserID, serverId);
        }

        if (points.compareTo(getBeanmasBalance(userId, serverId)) <= 0) {
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("beanmas", buildValueForDB(getBeanmasBalance(userId, serverId).subtract(points)))).run(conn);
            if (botUserID != null && !botUserID.isEmpty()) {
                addPoints(botUserID, serverId, points);
            }
            return 1;
        } else {
            if (removePoints(userId, null, serverId, points.subtract(getBeanmasBalance(userId, serverId)))) {
                r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId)).update(r.hashMap("beanmas", buildValueForDB(BigDecimal.ZERO))
                ).run(conn);
                return 0;
            }
            else
                return -1;
        }
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
