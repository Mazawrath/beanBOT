package com.mazawrath.beanbot.commands.admin;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

public class AdminDeleteMessageCommand implements CommandExecutor {
    @Command(
            aliases = {"admindeletemessage"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) throws ExecutionException, InterruptedException {
        if (!author.isBotOwner()) {
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel2.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " can use this command.");
            return;
        }

        serverTextChannel2.sendMessage(args[1] + " deleted.");
        server.getTextChannelById(args[0]).ifPresent(serverTextChannel -> serverTextChannel.deleteMessages(args[1]));
    }
}