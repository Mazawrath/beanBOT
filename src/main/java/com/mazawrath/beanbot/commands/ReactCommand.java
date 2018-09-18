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

    public void onCommand (String command, String argument, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
            if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 5)) {
                Message messsageBefore = message.getMessagesBefore(1).join().first();
                message.delete();
                for (int i = 0; i < argument.length(); i++) {
                    if (StringUtils.isAlpha(String.valueOf(argument.toLowerCase().charAt(i))))
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":regional_indicator_symbol_" + argument.toLowerCase().charAt(i) + ":"));
                    else if (StringUtils.isAlphaSpace(String.valueOf(argument.toLowerCase().charAt(i))))
                        messsageBefore.addReaction(EmojiParser.parseToUnicode(":large_blue_circle:"));
                }
            } else
                serverTextChannel.sendMessage("You do not have enough beanCoin to use this command.");
    }
}
