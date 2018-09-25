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
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.3.0\n" +
                "**v2.3.0**\n" +
                "**New**\n" +
                "\t- Added `.mazaposthelp`.\n" +
                "\t- Added `.banuser`.\n" +
                "\t- Added Papa BEETHS as a stock.\n" +
                "**Changes**\n" +
                "\t- `.beanfree` now tells you how many beanCoin you have if you received beanCoin from the command.\n" +
                "\t- `.beanfree` now uses singular or plural based on whether the amount of hours or minutes.\n" +
                "\t- Companies looked up with `.beanmarket` show more info.\n" +
                "**Bug Fixes**\n" +
                "\t- Fixed `Points.java` that arbitrarily added beanCoin to a specific user ID vs the user ID of the bot itself.\n" +
                "\t- Fixed `.source` not showing up on `.help`";
    }
}
