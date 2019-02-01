package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import io.sentry.Sentry;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class HelpCommand implements CommandExecutor {

    private final CommandHandler cmdHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.cmdHandler = commandHandler;
    }

    @Command(
            aliases = {"help", "commands", "usage"},
            usage = "help [command]",
            description = "Displays a list of commands and usages, or details, if a command is specified.",
            async = true,
            showInHelpPage = true
    )
    public void onCommand(String[] args, ServerTextChannel channel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        String prefix = cmdHandler.getDefaultPrefix();

        if (args.length == 0) {
            buildDefaultHelp(channel, prefix);
        }
        if (args.length == 1) {
            buildCommandHelp(channel, prefix, args[0]);
        }

        Sentry.clearContext();
    }

    public void buildDefaultHelp(ServerTextChannel channel, String prefix) {
        StringBuilder builder = new StringBuilder();
        builder.append("```yml");

        for (CommandHandler.SimpleCommand simpleCommand : cmdHandler.getCommands()) {

            String commandUsage = simpleCommand.getCommandAnnotation().usage();

            // Skip hidden commands:
            if (!simpleCommand.getCommandAnnotation().showInHelpPage()) {
                continue;
            }
            builder.append("\n - ");
            builder.append(commandUsage);
        }

        builder.append("```");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .addField("Prefix: ", String.format("```%s```", prefix))
                .addField("Command usage", builder.toString())
                .setFooter("Target a command as an argument to see details");
        channel.sendMessage(embed);
    }

    public void buildCommandHelp(ServerTextChannel channel, String prefix, String command) {

        String targetedCommand = null;
        String[] targetedAliases = null;
        String targetedDescription = null;
        String targetedUsage = null;
        String targetedPermissions = null;

        for (CommandHandler.SimpleCommand simpleCommand : cmdHandler.getCommands()) {
            if (simpleCommand.getCommandAnnotation().aliases()[0].equals(command)) {
                targetedCommand = simpleCommand.getCommandAnnotation().aliases()[0];
                targetedAliases = simpleCommand.getCommandAnnotation().aliases();
                targetedDescription = simpleCommand.getCommandAnnotation().description();
                targetedUsage = simpleCommand.getCommandAnnotation().usage();
                targetedPermissions = simpleCommand.getCommandAnnotation().requiredPermissions();
            }
        }

        if (targetedCommand == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .addField("ERROR:", "That command does not exist");
            channel.sendMessage(embed);
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("```yml\n");

        if (targetedAliases.length != 0) {
            builder.append(String.format("- Aliases: %s\n", String.join(", ", targetedAliases)));
        }
        if (!targetedDescription.equals("none")) {
            builder.append(String.format("- Description: %s\n", targetedDescription));
        }
        if (!targetedUsage.equals("")) {
            builder.append(String.format("- Usage: %s\n", targetedUsage));
        }
        builder.append(String.format("- Permissions: %s\n", targetedPermissions));

        builder.append("```");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(String.format("%s%s", prefix, targetedCommand))
                .setDescription(builder.toString());
        channel.sendMessage(embed);
    }
}
