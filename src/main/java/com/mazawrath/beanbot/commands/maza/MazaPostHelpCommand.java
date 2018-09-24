package com.mazawrath.beanbot.commands.maza;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.awt.*;

public class MazaPostHelpCommand implements CommandExecutor {

    private final CommandHandler cmdHandler;

    public MazaPostHelpCommand(CommandHandler commandHandler) {
        this.cmdHandler = commandHandler;
    }

    @Command(
            aliases = {"mazaposthelp"},
            usage = "mazaposthelp [channel]",
            description = "Posts the help page to the specified channel.",
            async = true,
            showInHelpPage = false
    )
    public void onCommand(String[] args, DiscordApi api, ServerTextChannel channel, Server server) {

        String prefix = cmdHandler.getDefaultPrefix();
        buildDefaultHelp(api, server, args[0], prefix);
        channel.sendMessage("Help command sent to " + channel);
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
        server.getTextChannelById(channel).ifPresent(serverTextChannel -> {
            serverTextChannel.sendMessage(embed);
        });
    }
}
