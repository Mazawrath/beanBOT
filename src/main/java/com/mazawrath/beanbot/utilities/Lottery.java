package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;

import java.util.Random;

import static com.rethinkdb.RethinkDB.r;

public class Lottery {
    Connection conn;

    public void connectDatabase() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
    }

    private boolean checkTable(Connection conn) {
        if (r.dbList().contains("beanBotLottery").run(conn)) {
            return true;
        } else {
            r.dbCreate("beanBotLottery").run(conn);
            return true;
        }
    }

    private void checkServer(String serverID) {
        if (r.db("beanBotLottery").tableList().contains(serverID).run(conn)) {
        } else
            r.db("beanBotLottery").tableCreate(serverID).run(conn);
    }

    private void checkUser(String userID, String serverID) {
        checkServer(serverID);

        if (r.db("beanBotLottery").table(serverID).getField("id").contains(userID).run(conn)) {
        } else
            r.db("beanBotLottery").table(serverID).insert(r.array(
                    r.hashMap("id", userID))).run(conn);
    }

    public int[][] addEntry(String userID, String serverID, int amount) {
        checkUser(userID, serverID);
        int[][] retNumbers = new int[amount][3];

        for (int i = 0; i < amount; i++) {
            retNumbers[i] = generateNumbers();
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Lottery ticket", generateNumbers())
            ).run(conn);
        }

        return retNumbers;
    }

    public void addEntry(String userID, String server, int[] numbers) {

    }

    private int[] generateNumbers() {
        Random random = new Random();
        int[] numbers = new int[3];
        for (int i = 0; i < 3; i++) {
            numbers[i] = random.nextInt(20) + 1;
        }
        return numbers;
    }
}
