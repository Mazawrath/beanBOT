package com.mazawrath.beanbot.commands.maza;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class MazaDeleteMessageCommand implements CommandExecutor {
    @Command(
            aliases = {"mazadeletemessage"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String command, String channelID, String messageId, ServerTextChannel serverTextChannel2, DiscordApi api, User author, Server server) {
        if (author.isBotOwner()) {
            serverTextChannel2.sendMessage(messageId + " deleted.");
                server.getTextChannelById(channelID).ifPresent(serverTextChannel -> {
                    serverTextChannel.deleteMessages(messageId);
                });
        } else
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel2.sendMessage("Only Mazawrath can send this message.");
    }
}
