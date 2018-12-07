package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;

import java.util.Random;

import static com.rethinkdb.RethinkDB.r;

public class Lottery {
    public static final int AMOUNT_DRAWN = 3;
    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 20;

    private Connection conn;

    public void connectDatabase() {
        conn = r.connection().hostname("localhost").port(28015).connect();
        checkTable(conn);
    }

    private void checkTable(Connection conn) {
        if (r.dbList().contains("beanBotLottery").run(conn)) {
        } else {
            r.dbCreate("beanBotLottery").run(conn);
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
        int[][] retNumbers = new int[amount][AMOUNT_DRAWN];

        for (int i = 0; i < amount; i++) {
            retNumbers[i] = generateNumbers();
            r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Lottery ticket", generateNumbers())
            ).run(conn);
        }

        return retNumbers;
    }

    public void addEntry(String userID, String serverID, int[] numbers) {
        r.db("beanBotStock").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Lottery ticket", numbers)
        ).run(conn);
    }

    private int[] generateNumbers() {
        Random random = new Random();
        int[] numbers = new int[AMOUNT_DRAWN];
        for (int i = 0; i < AMOUNT_DRAWN; i++) {
            numbers[i] = random.nextInt(MAX_NUMBER) + 1;
        }
        return numbers;
    }
}
