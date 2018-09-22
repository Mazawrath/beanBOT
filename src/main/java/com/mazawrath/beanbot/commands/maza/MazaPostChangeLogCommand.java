package com.mazawrath.beanbot.commands.maza;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class MazaPostChangeLogCommand implements CommandExecutor {
    @Command(
            aliases = {"mazapostchangelog"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String command, String id, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) {
        if (author.isBotOwner()) {
            server.getTextChannelById(id).ifPresent(serverTextChannel -> {
                serverTextChannel.sendMessage(getRecentChangeLog());
                serverTextChannel2.sendMessage("Changelog sent to " + serverTextChannel.getName() + ".");
            });
        } else
            // There is no better var name than this and if you think otherwise you're wrong.
            serverTextChannel2.sendMessage("Only Mazawrath can send this message.");
    }

    private String getRecentChangeLog() {
        return "**New beanBOT update released.**\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.2.0\n" +
                "**v2.2.0**\n" +
                "**New**\n" +
                "\t- Added start of the Bean Market.\n" +
                "\t- Added `.beanmarket`.\n" +
                "\t\t- Can only look at companies. Currently cannot invest any beanCoin.\n" +
                "\t- Added `.asg`.\n" +
                "\t- Added `.grind`.\n" +
                "**Changes**\n" +
                "\t- Looking up users with `.userinfo` is now free\n" +
                "\t- `.react` beanCoin cost changed from 5 beanCoin to 2 beanCoin\n" +
                "\t- `.beanfree` now tells you how long you have until you can use `.beanfree` again.\n" +
                "**Bug Fixes**\n" +
                "\t- `.react` now supports up to 4 words";
    }
}
