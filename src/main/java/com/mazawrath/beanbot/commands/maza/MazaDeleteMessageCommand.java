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

    public void onCommand(String[] args, ServerTextChannel serverTextChannel2, User author, Server server) {
        if (!author.isBotOwner()) {
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel2.sendMessage("Only Mazawrath can use this command.");
            return;
        }

        serverTextChannel2.sendMessage(args[1] + " deleted.");
        server.getTextChannelById(args[0]).ifPresent(serverTextChannel -> serverTextChannel.deleteMessages(args[1]));
    }
}