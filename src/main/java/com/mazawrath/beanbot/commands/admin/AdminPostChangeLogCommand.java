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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v3.3.1\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v3.3.0...v3.3.1\n" +
                "\n" +
                "**v3.3.1**\n" +
//                "**New**\n" +
//                "\t- Added `.adminlookupuser`.\n" +
                "**Changes**\n" +
                "\t- Set limit to how many lottery tickets can be bought in a single drawing to 200.\n" +
                "\t- Set max lottery number to be drawn from 40 to 20.\n" +
                "\t- Removed minimum amount required for automatic bean lottery drawings.\n" +
                "\t- Bean coin from `.beanbet` will only go to the bot when the bet loses.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed outdated info with `.beanlottery draw`.\n" +
                "\t- Fixed an issue with integer checks with `.beanlottery`.";
    }
}