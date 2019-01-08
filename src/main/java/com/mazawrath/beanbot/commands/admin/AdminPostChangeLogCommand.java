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
        if (!author.isBotOwner() && !server.isOwner(author)) {
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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v3.1.1\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v3.1.0...v3.1.1\n" +
                "\n" +
                "**v3.1.1**\n" +
//                "**New**\n" +
//                "\t- Twitch Update\n" +
//                "\t\t- beanBOT can now notify servers when a Twitch channel goes live.\n" +
//                "\t\t- Server owners can now type `.admintwitch add [twitch channel name]` to subscribe to live notifications for a twitch channel.\n" +
                "**Changes**\n" +
                "\t- Set up Twitch to only notify servers if streamer was previously offline and has not been online within the past 10 minutes.\n" +
                "\t- Changed price of `.beanlottery draw` from 400 beanCoin to 20,000 beanCoin.\n" +
                "\t- Lowered time of bean lottery drawing from 7 days to 3 days.";
//                "**Bug Fixes**\n" +
//                "\t- Fixed an issue with `.beanlottery` not giving help information.\n";
    }
}