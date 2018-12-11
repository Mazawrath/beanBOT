package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
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
        if (!author.isBotOwner() || !server.isOwner(author)) {
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel2.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " or " + server.getOwner().getDisplayName(server) + " can use this command.");
            return;
        }

        server.getTextChannelById(args[0]).ifPresent(serverTextChannel -> {
            serverTextChannel.sendMessage(getRecentChangeLog());
            serverTextChannel2.sendMessage("Changelog sent to " + serverTextChannel.getName() + ".");
        });
    }

    private String getRecentChangeLog() {
        return "**New beanBOT update released.**\n" +
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.7.0\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v2.6.2...v2.7.0\n" +
                "\n" +
                "**v2.7.0**\n" +
                "**New**\n" +
                "\t- Added the Bean Lottery.\n" +
                "\t\t- Added `.beanlottery`.\n" +
                "\t\t- *For a limited time `.beanlottery` tickets only cost " + Points.pointsToString(Points.LOTTERY_TICKET_COST) + "!*\n" +
                "\t\t- Added `.adminforcelotterydrawing`.\n" +
                "\t- Added `.adminremovebeancoin`.\n" +
                "**Changes**\n" +
                "\t- Re-enabled `.givemod`.\n" +
                "\t- Renamed all `.maza` commands to `.admin`.\n" +
                "\t- `.admin` commands now tell users that only the bot owner can use commands instead of only Mazawrath.\n" +
                "\t- Server owners can now use `.adminpostchangelog`.\n" +
                "\t- Server owners can now use `.adminposthelp`.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed an issue where normal users had access to `.adminposthelp`.\n" +
                "\t- Fixed an issue where `.adminposthelp` could be used in a private message.";
    }
}