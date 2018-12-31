package com.mazawrath.beanbot.utilities;

import com.rethinkdb.net.Connection;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public void clearTickets(String serverID) {
        r.db(DB_NAME).table(serverID).delete().run(conn);
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
        checkUser(userID, serverID);
        r.db(DB_NAME).table(serverID).filter(r.hashMap("id", userID))
                .update(row -> r.hashMap("Lottery ticket", row.g("Lottery ticket").default_(r.array()).append(r.array(numbers[0], numbers[1], numbers[2])))).run(conn);
    }

    public ArrayList getWinner(String serverID, int[] winningNumbers) {
        ArrayList<Integer> ticket = new ArrayList<>();

        for (int i = 0; i < winningNumbers.length; i++)
            ticket.add(winningNumbers[i]);

        ArrayList users = r.db(DB_NAME).table(serverID)
                .map(row -> r.object("id", row.getField("id"), "Lottery ticket", row.getField("Lottery ticket")))
                .filter(row -> row.g("Lottery ticket").contains(ticket))
                .without("Lottery ticket")
                .orderBy(r.asc("id"))
                .run(conn);

        return users;
        //users.forEach(user -> System.out.println(((HashMap)user).get("id")));
    }

    private void getUserTickets(String userID, String serverID) {
        ArrayList<ArrayList<Long>> tickets = r.db(DB_NAME).table(serverID).get(userID).getField("Lottery ticket").run(conn);
        ArrayList<String> ticketsTrans = new ArrayList<String>();

        tickets.forEach(ticket -> {
            String ticketTrans = ticket.stream().map(Object::toString).collect(Collectors.joining("-"));
            ticketsTrans.add(ticketTrans);
        });

        ticketsTrans.forEach(ticket -> System.out.println(ticket));
    }

    public void drawNumbers(Points points, Server server, DiscordApi api, ServerTextChannel serverTextChannel) {
        int[] winningNumbers = generateNumbers();

        ArrayList winners = getWinner(server.getIdAsString(), winningNumbers);
        MessageBuilder message = new MessageBuilder();

        message.append("The numbers drawn were:\n");
        for (int i = 0; i < winningNumbers.length; i++)
            message.append(winningNumbers[i] + " ");
        message.append("\n");
        if (winners.size() == 0)
            message.append("No one has won. All bean lottery tickets have been saved until the next drawing.");
        else {
            BigDecimal prizePool = points.getBalance(api.getYourself().getIdAsString(), server.getIdAsString());
            BigDecimal amountWon = prizePool.divide(new BigDecimal(winners.size())).setScale(Points.SCALE, Points.ROUNDING_MODE);
            points.removePoints(api.getYourself().getIdAsString(), null, server.getIdAsString(), points.getBalance(api.getYourself().getIdAsString(), server.getIdAsString()));

            winners.forEach(winner -> points.addPoints(((HashMap) winner).get("id").toString(), server.getIdAsString(), amountWon));

            message.append("The following users have won:\n");
            winners.forEach(winner ->
                    api.getCachedUserById((((HashMap) winner).get("id").toString())).ifPresent(user ->
                            message.append(user.getMentionTag() + " has won!\n")));

            message.append("The prize pool was " + Points.pointsToString(prizePool) + " and divided between " + winners.size());
            if (winners.size() == 1)
                message.append(" winner, they get the entire prize pool!");
            else
                message.append(" winners, each gets " + Points.pointsToString(amountWon) + "!");
            message.append("\nAll bean lottery tickets have been deleted for the next bean lottery drawing.");
            clearTickets(server.getIdAsString());
        }
        message.send(serverTextChannel);
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
