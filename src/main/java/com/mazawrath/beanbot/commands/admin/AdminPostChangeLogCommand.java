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
            serverTextChannel2.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v3.5.0\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v3.4.0...v3.5.0\n" +
                "\n" +
                "**v3.5.0**\n" +
                "**New**\n" +
                "\t- Added photo manipulation.\n" +
                "\t\t- Added `.deepfry`.\n" +
                "\t\t- Added `.emboss`.\n" +
                "\t\t- Added `.invert`.\n" +
                "\t\t- Added `.diffuse`.\n" +
                "\t\t- Added `.mosaic`.\n" +
                "\t\t- Added `.sepia`.\n" +
                "\t- Added polling.\n" +
                "\t\t- Added `.poll`.\n" +
                "\t\t- Added `.strawpoll`.\n" +
                "**Changes**\n" +
                "\t- Changed limit to how many lottery tickets can be bought at the start of a new drawing from 200 to 100.\n" +
                "\t\t- After every drawing without a winner users will be able to buy up to 50 more tickets.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed a file reading issue with `.analyze`.\n" +
                "\t- Fixed a issue with records getting added to the lottery database when they did not buy a ticket.";
    }
}