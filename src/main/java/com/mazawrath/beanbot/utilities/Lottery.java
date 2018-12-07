package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;

import java.util.ArrayList;
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

    public ArrayList<ArrayList<Integer>> addEntry(String userID, String serverID, int amount) {
        checkUser(userID, serverID);
        ArrayList<ArrayList<Integer>> ticketArray = new ArrayList<>();
        int[] generatedNumbers;

        for (int i = 0; i < amount; i++) {
            ArrayList<Integer> singleTicket = new ArrayList<>();
            generatedNumbers = generateNumbers();
            for (int j = 0; j < AMOUNT_DRAWN; j++)
                singleTicket.add(generatedNumbers[j]);
            ticketArray.add(singleTicket);
        }

        for (int i = 0; i < ticketArray.size(); i++) {
            int finalI = i;
            r.db("beanBotLottery").table(serverID).filter(r.hashMap("id", userID))
                    .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(ticketArray.get(finalI)))).run(conn);
        }
        return ticketArray;
    }

    public void addEntry(String userID, String serverID, int[] numbers) {
        r.db("beanBotLottery").table(serverID).filter(r.hashMap("id", userID)).update(r.hashMap("Lottery ticket", r.array(r.array(numbers[0], numbers[1], numbers[2])))
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
