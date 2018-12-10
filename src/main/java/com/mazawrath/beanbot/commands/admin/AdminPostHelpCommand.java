package com.mazawrath.beanbot.commands.admin;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.concurrent.ExecutionException;

public class AdminPostHelpCommand implements CommandExecutor {

    private final CommandHandler cmdHandler;

    public AdminPostHelpCommand(CommandHandler commandHandler) {
        this.cmdHandler = commandHandler;
    }

    @Command(
            aliases = {"adminposthelp"},
            usage = "adminposthelp [Channel ID]",
            description = "Posts the help info to the specified channel.",
            privateMessages = false,
            showInHelpPage = false
    )
    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        if (!author.isBotOwner() || !server.isOwner(author)) {
            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " can use this command.");
            return;
        }

        String prefix = cmdHandler.getDefaultPrefix();
        buildDefaultHelp(api, server, args[0], prefix);
        serverTextChannel.sendMessage("Help command sent to " + serverTextChannel);
    }

    public void buildDefaultHelp(DiscordApi api, Server server, String channel, String prefix) {
        StringBuilder builder = new StringBuilder();
        builder.append("```yml");

        for (CommandHandler.SimpleCommand simpleCommand : cmdHandler.getCommands()) {

            String commandUsage = simpleCommand.getCommandAnnotation().usage();

            // Skip hidden commands:
            if (!simpleCommand.getCommandAnnotation().showInHelpPage())
                continue;
            builder.append("\n - ");
            builder.append(commandUsage);
        }

        builder.append("```");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .addField("Prefix: ", String.format("```%s```", prefix))
                .addField("Command usage", builder.toString())
                .setFooter("Target a command as an argument to see details");
        server.getTextChannelById(channel).ifPresent(serverTextChannel -> serverTextChannel.sendMessage(embed));
    }
}