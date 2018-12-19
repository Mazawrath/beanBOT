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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.8.0\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v2.7.0...v2.8.0\n" +
                "\n" +
                "**v2.8.0**\n" +
                "**New**\n" +
                "\t- Beanmas\n" +
                "\t\t- Beanmas is here! Every user in the server has been awarded with a free credit the amount of their current balance to give to other users on the server without effecting their balance!\n" +
                "\t\t- But it will only be here for a limited time! So give while you can!\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed an issue where the server owner or bot owner couldn't use `.adminposthelp` or `.adminpostchangelog`.";
    }
}