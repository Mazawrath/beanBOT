package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Arrays;
import java.util.Random;

public class MinesweeperCommand implements CommandExecutor {
    private Points points;

    public MinesweeperCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"minesweeper"},
            usage = "minesweeper [size] [mines]",
            description = "Creates a minesweeper field with custom size options.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

        int size;
        int mines;

        if (args.length >= 2) {
            if (StringUtils.isNumeric(args[0]) && StringUtils.isNumeric(args[1])) {
                size = Integer.parseInt(args[0]);
                mines = Integer.parseInt(args[1]);
            } else {
                serverTextChannel.sendMessage("Invalid numbers.");
                return;
            }
        } else {
            size = 10;
            mines = size * size / 6;
        }

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            serverTextChannel.sendMessage(new MinesweeperField(ensureInRange(size, 1,14), ensureInRange(mines, 1, size*size)).toDiscordString());
        }

        Sentry.clearContext();
    }

    private int ensureInRange(int value, int min, int max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    private static class MinesweeperField {
        private final char[][] field;

        public MinesweeperField(int size, int mines) {
            if (size * size < mines) {
                throw new IllegalArgumentException("too many mines");
            }
            this.field = new char[size][size];
            for (char[] row : field) {
                Arrays.fill(row, ' ');
            }
            placeMines(mines);
            calculateFields();
        }

        private void placeMines(int mines) {
            Random random = new Random();
            for (int i = 0; i < mines; ) {
                int x = random.nextInt(field.length);
                int y = random.nextInt(field.length);
                if (field[y][x] == ' ') {
                    field[y][x] = 'B';
                    i++;
                }
            }
        }

        private void calculateFields() {
            for (int y = 0; y < field.length; y++) {
                for (int x = 0; x < field[y].length; x++) {
                    if (field[y][x] == ' ') field[y][x] = (char) ('0' + countMinesAround(x, y));
                }
            }
        }

        private int countMinesAround(int xCenter, int yCenter) {
            int numMines = 0;
            for (int y = yCenter - 1; y <= yCenter + 1; y++) {
                if (y < 0 || y == field.length) continue;
                for (int x = xCenter - 1; x <= xCenter + 1; x++) {
                    if (x < 0 || x == field[y].length) continue;
                    if (field[y][x] == 'B') numMines++;
                }
            }
            return numMines;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (char[] row : field) {
                builder.append(new String(row)).append("\n");
            }
            return builder.toString();
        }

        public String toDiscordString() {
            StringBuilder builder = new StringBuilder();
            for (char[] row : field) {
                for (char c : row) {
                    builder.append("||");
                    builder.append((c == 'B') ? "\uD83D\uDCA3" : c + "\u20E3");
                    builder.append("||");
                }
                builder.append("\n");
            }
            return builder.toString();
        }
    }
}
