package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.vdurmont.emoji.EmojiParser;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class ReactCommand implements CommandExecutor {
    private Points points;

    public ReactCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"react"},
            usage = "react [text]",
            description = "Rolls a 10,000 sided dice to randomly give mod.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            Message messsageBefore = message.getMessagesBefore(1).join().first();
            message.delete();
            for (int i = 0; i < args.length; i++) {
                for (int j = 0; j < args[i].length(); j++) {
                    if (StringUtils.isAlpha(String.valueOf(args[i].toLowerCase().charAt(j))))
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":regional_indicator_symbol_" + args[i].toLowerCase().charAt(j) + ":"));
                }
                if (args.length == i + 2) {
                    if (i == 0) {
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":large_blue_circle:"));
                    } else if (i == 1) {
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":black_circle:"));
                    } else if (i == 2) {
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":red_circle:"));
                    }
                } else
                    break;
            }
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin to use this command.");

        Sentry.clearContext();
    }
}
