package com.mazawrath.beanbot.commands.maza;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class MazaPostMessageCommand implements CommandExecutor {
    @Command(
            aliases = {"mazapostmessage"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] command, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) {
        if (author.isBotOwner()) {
            StringBuilder message = new StringBuilder();
            server.getTextChannelById(command[0]).ifPresent(serverTextChannel -> {
                for (int i = 1; i < command.length; i++){
                    message.append(command[i]).append(" ");
                }
                serverTextChannel.sendMessage(message.toString());
            });
        } else
        serverTextChannel2.sendMessage("Only Mazawrath can send this message.");
    }
}
