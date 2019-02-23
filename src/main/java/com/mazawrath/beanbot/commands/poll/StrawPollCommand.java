package com.mazawrath.beanbot.commands.poll;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.samuelmaddock.strawpollwrapper.StrawPoll;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class StrawPollCommand implements CommandExecutor {
    private Points points;

    public StrawPollCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"strawpoll"},
            usage = "strawpoll",
            privateMessages = false,
            async = true
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        StringBuilder question = new StringBuilder();
        List<String> options = new ArrayList<>();
        boolean isMulti = false;
        int i = 0;

        if (args[i].toLowerCase().equals("m")) {
            isMulti = true;
            i++;
        }

        for (; i < args.length; i++) {
            if (args[i].equals("|")) {
                i++;
                break;
            }
            if (args[i].startsWith("|") && args[i].endsWith("|")) {
                break;
            } if (args[i].startsWith("|")) {
                break;
            }
            if (args[i].endsWith("|")) {
                question.append(args[i], 0, args[i].length() - 1).append(" ");
                i++;
                break;
            }
            question.append(args[i]).append(" ");
        }

        int k = i;
        while (k < args.length) {
            StringBuilder option = new StringBuilder();
            while (k < args.length) {
                if (args[k].equals("|")) {
                    k++;
                    break;
                } else if (args[k].startsWith("|") && args[k].endsWith("|")) {
                    if (option.toString().isEmpty()) {
                        option.append(args[k], 1, args[k].length() - 1).append(" ");
                        k++;
                        break;
                    } else
                        break;
                } else if (args[k].startsWith("|"))
                    if (option.toString().isEmpty()) {
                        option.append(args[k], 1, args[k].length()).append(" ");
                    } else
                        break;
                else if (args[k].endsWith("|")) {
                    option.append(args[k], 0, args[k].length() - 1).append(" ");
                    k++;
                    break;
                } else
                    option.append(args[k]).append(" ");
                k++;
            }
            System.out.println(option.toString());
            options.add(option.toString());
        }

        if (options.size() < 2) {
            serverTextChannel.sendMessage("You must have at least two options. Options are separated by `|`\n" +
                    "`.strawpoll Question | Option 1 | Option 2 | Option 3`\n" +
                    "If you would like to be able to select multiple options, put a `m` before your question\n" +
                    "`.strawpoll m Question | Option 1 | Option 2 | Option 3`");
            return;
        }
        if (options.size() > 30) {
            serverTextChannel.sendMessage("There is a maximum of 30 options for one poll.");
            return;
        }

        StrawPoll strawPoll = new StrawPoll(question.toString(), options)
                .setIsMulti(isMulti);
        strawPoll.create();

        serverTextChannel.sendMessage("Strawpoll created: " + strawPoll.getPollURL());

        Sentry.clearContext();
    }
}
