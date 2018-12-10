package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;

import java.util.ArrayList;
import java.util.Random;

import static com.rethinkdb.RethinkDB.r;

public class Lottery {
    public static final int AMOUNT_DRAWN = 3;
    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 20;
    private static final String DB_NAME = "beanBotLottery";

    private Connection conn;

    public void connectDatabase() {
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
            r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID))
                    .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(ticketArray.get(finalI)))).run(conn);
        }
        return ticketArray;
    }

    public void addEntry(String userID, String serverID, int[] numbers) {
        r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID))
                .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(r.array(numbers[0], numbers[1], numbers[2])))).run(conn);
    }

    private int[] generateNumbers() {
        Random r = new Random();
        int[] numbers = new int[AMOUNT_DRAWN];
        for (int i = 0; i < AMOUNT_DRAWN; i++) {
            numbers[i] = r.nextInt(MAX_NUMBER - MIN_NUMBER) + MIN_NUMBER;
        }
        return numbers;
    }
}
