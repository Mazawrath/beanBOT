package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

public class AdminPostChangeLogCommand implements CommandExecutor {
    @Command(
            aliases = {"adminpostchangelog"},
            usage = "adminpostchangelog [Channel ID]",
            description = "Posts the changelog to the specified channel.",
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) throws ExecutionException, InterruptedException {
        SentryLog.addContext(args, author, server);

        if (!author.isBotOwner() && !server.isOwner(author)) {
            // There is no better var name than this and if you think otherwise you're wrong.
//            serverTextChannel2.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        server.getTextChannelById(args[0]).ifPresent(serverTextChannel -> {
            serverTextChannel.sendMessage(getRecentChangeLog());
            serverTextChannel2.sendMessage("Changelog sent to " + serverTextChannel.getName() + ".");
        });

        Sentry.clearContext();
    }

    private String getRecentChangeLog() {
        return "**New beanBOT update released.**\n" +
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v3.6.0\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v3.5.0...v3.6.0\n" +
                "\n" +
                "**v3.6.0**\n" +
                "**New**\n" +
                "\t- Added `.beantrivia`.\n" +
                "\t\t- Earn beanCoin by answering trivia questions! After using the command you will have 8 seconds to react to the correct answer, anyone can participate and win beanCoin. Don't try to cheat, you will regret it!\n" +
                "\t- Added `.toxic`.\n" +
                "\t\t- Analyze messages to measure their toxicity. Leave the command blank to analyze the most recent message. Enter text in to analyze that text. Or mention a user with `@` to analyze their most recent message.\n" +
                "**Changes**\n" +
                "\t- Disabled `.beanbet`.\n" +
                "\t- User now start with ß1000.00 when they use a command that uses beanCoin.\n" +
                "\t- Changed amount users received from `.beanfree` from ß25.69 to ß50.00.\n" +
                "\t- Changed the cooldown of `.beanfree` from 24 hours to 7 days.\n" +
                "\t- Changed price of standard commands from ß2.00 to ß10.00.\n" +
                "\t- Changed price of special commands from ß10 to ß15.\n" +
                "\t- Changed price of bean lottery tickets from ß40 to ß45.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed an issue with bean lottery crashing if someone tried buying too many tickets when they didn't have any in the first place.";
    }
}