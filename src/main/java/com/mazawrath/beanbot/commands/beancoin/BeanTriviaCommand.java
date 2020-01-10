package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.PointsUser;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.Trivia;
import com.vdurmont.emoji.EmojiParser;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BeanTriviaCommand implements CommandExecutor {
    private Points points;
    private Trivia trivia;

    public BeanTriviaCommand(Points points, Trivia trivia) {
        this.points = points;
        this.trivia = trivia;
    }

    @Command(
            aliases = {"beantrivia", "cointrivia"},
            usage = "beantrivia",
            description = "Get 25 beanCoin every 24 hours.",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, DiscordApi api, User author, Server server) {
        SentryLog.addContext(null, author, server);

        long timeLeft = points.useTriviaQuestion(new PointsUser(author, server));

        if (timeLeft == 0) {
            String emojiCorrectAnswer;

            JSONObject jsonObject = trivia.getTrivia();
            JSONArray results = (JSONArray) jsonObject.get("results");
            JSONObject questionObject = (JSONObject) results.get(0);

            String quetsion = new String(Base64.getDecoder().decode(questionObject.get("question").toString()));
            String difficulty = new String(Base64.getDecoder().decode(questionObject.get("difficulty").toString()));
            String category = new String(Base64.getDecoder().decode(questionObject.get("category").toString()));
            String correctAnswer = new String(Base64.getDecoder().decode(questionObject.get("correct_answer").toString()));

            // I'm honestly ashamed of this code, please forgive me if you're thinking about hiring me and run across this snippet.
            JSONArray incorrectAnswersObject = (JSONArray) questionObject.get("incorrect_answers");
            String[] incorrectAnswers = new String[incorrectAnswersObject.size()];
            for (int i = 0; i < incorrectAnswers.length; i++)
                incorrectAnswers[i] = new String(Base64.getDecoder().decode(incorrectAnswersObject.get(i).toString()));

            ArrayList<String> answers = new ArrayList<>();
            answers.add(correctAnswer);
            Collections.addAll(answers, incorrectAnswers);
            Collections.shuffle(answers);
            int correctAnswerIndex = answers.indexOf(correctAnswer);
            switch (correctAnswerIndex) {
                case 0:
                    emojiCorrectAnswer = ":one:";
                    break;
                case 1:
                    emojiCorrectAnswer = ":two:";
                    break;
                case 2:
                    emojiCorrectAnswer = ":three:";
                    break;
                case 3:
                    emojiCorrectAnswer = ":four:";
                    break;
                default:
                    serverTextChannel.sendMessage("Attempted to send trivia message but failed.");
                    return;
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(quetsion)
                    .setDescription("Category: " + category)
                    .setFooter("Difficulty: " + difficulty);
            for (int i = 0; i < answers.size(); i++)
                embed.addField("Answer " + (i + 1), answers.get(i));
            try {
                Message triviaMessage = serverTextChannel.sendMessage(embed).get();
                triviaMessage.addReaction(EmojiParser.parseToUnicode(":one:"));
                triviaMessage.addReaction(EmojiParser.parseToUnicode(":two:"));
                triviaMessage.addReaction(EmojiParser.parseToUnicode(":three:"));
                triviaMessage.addReaction(EmojiParser.parseToUnicode(":four:"));

                Thread.sleep(8000);
                List<Reaction> reactions = triviaMessage.getReactions();

                ArrayList<User> contestants = new ArrayList<>();
                ArrayList<User> winners = new ArrayList<>();
                ArrayList<User> cheaters = new ArrayList<>();

                // Get the winners
                for (int i = 0; i < reactions.size(); i++) {
                    if (reactions.get(i).getEmoji().equalsEmoji(EmojiParser.parseToUnicode(emojiCorrectAnswer))) {
                        Reaction correctEmoji = reactions.get(i);
                        for (int j = 0; j < correctEmoji.getUsers().get().size(); j++) {
                            if (correctEmoji.getUsers().get().get(j) != api.getYourself())
                                winners.add(correctEmoji.getUsers().get().get(j));
                        }
                    } else if (reactions.get(i).getUsers().get().contains(api.getYourself())) {
                        Reaction incorrectEmoji = reactions.get(i);
                        for (int j = 0; j < incorrectEmoji.getUsers().get().size(); j++) {
                            if (incorrectEmoji.getUsers().get().get(j) != api.getYourself())
                                contestants.add(incorrectEmoji.getUsers().get().get(j));
                        }
                    }
                }

                // Check for cheaters
                for (int i = 0; i < reactions.size(); i++) {
                    if (!reactions.get(i).getEmoji().equalsEmoji(EmojiParser.parseToUnicode(emojiCorrectAnswer)) && reactions.get(i).getUsers().get().contains(api.getYourself())) {
                        Reaction correctEmoji = reactions.get(i);
                        for (int j = 0; j < correctEmoji.getUsers().get().size(); j++) {
                            for (int k = 0; k < winners.size(); k++) {
                                // Found a cheater!
                                if (correctEmoji.getUsers().get().get(j) == winners.get(k)) {
                                    winners.remove(winners.get(k));
                                    cheaters.add(correctEmoji.getUsers().get().get(j));
                                }
                            }
                        }
                    }
                }
                // Anyone who reacted more then once but got it wrong also cheated
                ArrayList<User> duplicates = (ArrayList<User>) getDuplicate(contestants);
                for (User duplicate : duplicates) {
                    if (!cheaters.contains(duplicate))
                        cheaters.add(duplicate);
                }

                StringBuilder winnersMessage = new StringBuilder();

                if (winners.size() == 0) {
                    winnersMessage.append("No one got the answer correct!\n");
                } else {
                    winnersMessage.append("The following users have won:\n");
                    for (int i = 0; i < winners.size(); i++) {
                        winnersMessage.append(winners.get(i).getDisplayName(server)).append(" got the correct answer!\n");
                        points.depositCoins(new PointsUser(author, server), Points.TRIVIA_CORRECT_ANSWER);
                    }
                }
                winnersMessage.append("\n");
                for (int i = 0; i < cheaters.size(); i++) {
                    winnersMessage.append(cheaters.get(i).getDisplayName(server)).append(" has cheated and has been fined ").append(Points.pointsToString(Points.TRIVIA_CHEAT_FINE)).append("!\n");
                    if (points.checkBalance(new PointsUser(cheaters.get(i), server)).compareTo(Points.TRIVIA_CHEAT_FINE) <= 0)
                        points.makePurchase(new PointsUser(cheaters.get(i), server), new PointsUser(api.getYourself(), server), points.checkBalance(new PointsUser(cheaters.get(i), server)));
                    else
                        points.makePurchase(new PointsUser(cheaters.get(i), server), new PointsUser(api.getYourself(), server), Points.TRIVIA_CHEAT_FINE);
                }
                if (cheaters.size() != 0)
                    winnersMessage.append("\n");
                winnersMessage.append("The correct answer was: ").append(correctAnswer).append(".\nAnyone who answered correctly received ").append(Points.pointsToString(Points.TRIVIA_CORRECT_ANSWER)).append(".");

                serverTextChannel.sendMessage(winnersMessage.toString());
            } catch (Exception e) {
                serverTextChannel.sendMessage("Attempted to send trivia message but failed.");
                return;
            }
        } else {
            StringBuilder message = new StringBuilder();

            message.append("You have already did trivia today. You can use your trivia again in ");

            String dateStart = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(System.currentTimeMillis()));
            String dateStop = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(timeLeft + Points.FREE_COIN_TIME_LIMIT));

            //HH converts hour in 24 hours format (0-23), day calculation
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            Date d1;
            Date d2;

            try {
                d1 = format.parse(dateStart);
                d2 = format.parse(dateStop);

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();

                long days = TimeUnit.MILLISECONDS.toDays(diff);
                diff -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                diff -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                diff -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

                message.append(String.format("%d hours, %d mins, and %d seconds.",
                        hours, minutes, seconds));

                serverTextChannel.sendMessage(message.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Sentry.clearContext();
    }

    public static <T> List getDuplicate(Collection<T> list) {

        final List<T> duplicatedObjects = new ArrayList<T>();
        Set<T> set = new HashSet<T>() {
            @Override
            public boolean add(T e) {
                if (contains(e)) {
                    duplicatedObjects.add(e);
                }
                return super.add(e);
            }
        };
        for (T t : list) {
            set.add(t);
        }
        return duplicatedObjects;
    }
}