package com.mazawrath.beanbot.utilities;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.util.ArrayList;

public class Points {
    private static final RethinkDB r = RethinkDB.r;
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
                            .with("Points", 0)
                            .with("Last Received Free Points", 0)
            )).run(conn);
    }

    public ArrayList getLeaderboard(String serverID) {
        return r.db("beanBotPoints").table(serverID).orderBy(r.desc("Points")).limit(10).run(conn);
    }

    public long getBalance(String userID, String serverID) {
        checkUser(userID, serverID);

        return r.db("beanBotPoints").table(serverID).get(userID).getField("Points").run(conn);
    }

    public void addPoints(String userID, String serverID, long points) {
        checkUser(userID, serverID);

        r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", getBalance(userID, serverID) + points)).run(conn);
    }

    public boolean removePoints(String userID, String serverID, long points) {
        checkUser(userID, serverID);

        if (getBalance(userID, serverID) >= points) {
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", getBalance(userID, serverID) - points)).run(conn);
            addPoints("481912112969678868", serverID, points);
            return true;
        } else
            return false;
    }

    public boolean removePointsExcludeBeanbot(String userID, String serverID, long points) {
        checkUser(userID, serverID);

        if (getBalance(userID, serverID) >= points) {
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", getBalance(userID, serverID) - points)).run(conn);
            return true;
        } else
            return false;
    }

    public long giveFreePoints(String userID, String serverID) {
        checkUser(userID, serverID);
        long currentPoints = r.db("beanBotPoints").table(serverID).get(userID).getField("Last Received Free Points").run(conn);

        if (System.currentTimeMillis() - currentPoints > 24 * 60 * 60 * 1000) {
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Points", getBalance(userID, serverID) + 25)).run(conn);
            r.db("beanBotPoints").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Last Received Free Points", System.currentTimeMillis())).run(conn);
            return 0;
        }
        return currentPoints;
    }
}
