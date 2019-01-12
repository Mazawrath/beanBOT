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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v3.2.1\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v3.2.0...v3.2.1\n" +
                "\n" +
                "**v3.2.1**\n" +
//                "**New**\n" +
//                "\t- Added `.analyze`.\n" +
//                "\t\t- Using Google Cloud Vision, beanBOT can now examine a photo for objects, faces, emotions, and more.\n" +
//                "\t\t- Server owners can now type `.admintwitch add [twitch channel name]` to subscribe to live notifications for a twitch channel.\n" +
                "**Changes**\n" +
                "\t- Cleaned up analyze command.\n" +
                "\t- Expanded lottery numbers that could be drawn from 20 to 40.\n" +
                "\t- Raised price of beanLottery tickets from 20 beanCoin to 40 beanCoin.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed an issue with Discord not creating new thumbnail previews.\n";
    }
}