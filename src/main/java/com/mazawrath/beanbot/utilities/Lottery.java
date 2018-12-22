package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

import static com.rethinkdb.RethinkDB.r;

public class Lottery {
    public static final int AMOUNT_DRAWN = 3;
    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 15;
    private static final String DB_NAME = "beanBotLottery";

    private Connection conn;

    public Lottery(Connection conn) {
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
                    r.hashMap("id", userId))).run(conn);
    }

    public void clearTickets(String serverId) {
        r.db(DB_NAME).table(serverId).delete().run(conn);
    }

    public ArrayList<ArrayList<Integer>> addEntry(String userId, String serverId, int amount) {
        checkUser(userId, serverId);
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
            r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId))
                    .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(ticketArray.get(finalI)))).run(conn);
        }
        return ticketArray;
    }

    public void addEntry(String userId, String serverId, int[] numbers) {
        checkUser(userId, serverId);
        r.db(DB_NAME).table(serverId).filter(r.hashMap("id", userId))
                .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(r.array(numbers[0], numbers[1], numbers[2])))).run(conn);
    }

    public ArrayList getWinner(String serverId, int[] winningNumbers) {
        ArrayList<Integer> ticket = new ArrayList<>();

        for (int i = 0; i < winningNumbers.length; i++)
            ticket.add(winningNumbers[i]);

        ArrayList users = r.db(DB_NAME).table(serverId)
                .map(row -> r.object("id", row.getField("id"), "Lottery ticket", row.getField("Lottery ticket")))
                .filter(row -> row.g("Lottery ticket").contains(ticket))
                .without("Lottery ticket")
                .orderBy(r.asc("id"))
                .run(conn);

        return users;
        //users.forEach(user -> System.out.println(((HashMap)user).get("id")));
    }

    private void getUserTickets(String userId, String serverId) {
        ArrayList<ArrayList<Long>> tickets = r.db(DB_NAME).table(serverId).get(userId).getField("Lottery ticket").run(conn);
        ArrayList<String> ticketsTrans = new ArrayList<String>();

        tickets.forEach(ticket -> {
            String ticketTrans = ticket.stream().map(Object::toString).collect(Collectors.joining("-"));
            ticketsTrans.add(ticketTrans);
        });

        ticketsTrans.forEach(ticket -> System.out.println(ticket));
    }

    public int[] getWinningNumbers() {
        return generateNumbers();
    }

    private int[] generateNumbers() {
        Random r = new Random();
        int[] numbers = new int[AMOUNT_DRAWN];
        for (int i = 0; i < AMOUNT_DRAWN; i++) {
            numbers[i] = r.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
        }
        return numbers;
    }
}
