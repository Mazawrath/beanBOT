package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

public class AdminPostMessageCommand implements CommandExecutor {
    @Command(
            aliases = {"adminpostmessage"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) throws ExecutionException, InterruptedException {
        SentryLog.addContext(args, author, server);

        if (!author.isBotOwner()) {
            serverTextChannel2.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " can use this command.");
            return;
        }
        StringBuilder message = new StringBuilder();
        server.getTextChannelById(args[0]).ifPresent(serverTextChannel -> {
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            serverTextChannel.sendMessage(message.toString());
        });

        Sentry.clearContext();
    }
}