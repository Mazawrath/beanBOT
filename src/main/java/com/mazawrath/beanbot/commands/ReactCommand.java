package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.Points;
import com.vdurmont.emoji.EmojiParser;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
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
            description = "Rolls a 10,000 sided die to randomly give mod.",
            privateMessages = false
    )

    public void onCommand (String[] command, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
            if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 2)) {
                Message messsageBefore = message.getMessagesBefore(1).join().first();
                message.delete();
                for (int i = 0; i < command.length; i++) {
                    for (int j = 0; j < command[i].length(); j++) {
                        if (StringUtils.isAlpha(String.valueOf(command[i].toLowerCase().charAt(j))))
                            messsageBefore.addReaction(EmojiParser.parseToUnicode(":regional_indicator_symbol_" + command[i].toLowerCase().charAt(j) + ":"));
                    }
                        if (i == 0) {
                            messsageBefore.addReaction(EmojiParser.parseToUnicode(":large_blue_circle:"));
                        }
                        else if (i == 1) {
                            messsageBefore.addReaction(EmojiParser.parseToUnicode(":black_circle:"));
                        }
                        else if (i == 2) {
                            messsageBefore.addReaction(EmojiParser.parseToUnicode(":red_circle:"));
                        }
                }
            } else
                serverTextChannel.sendMessage("You do not have enough beanCoin to use this command.");
    }
}
