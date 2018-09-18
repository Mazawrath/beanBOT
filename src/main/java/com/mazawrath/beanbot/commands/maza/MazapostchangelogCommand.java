package com.mazawrath.beanbot.commands.maza;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class MazapostchangelogCommand implements CommandExecutor {
    @Command(
            aliases = {"mazapostchangelog"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String command, String id, DiscordApi api, ServerTextChannel serverTextChannel2, User author, Server server) {
        if (author.isBotOwner()) {
            server.getTextChannelById(id).ifPresent(serverTextChannel -> {
                serverTextChannel.sendMessage(getRecentChangeLog());
                serverTextChannel.sendMessage("Changelog sent to " + serverTextChannel.getName() + ".");
            });
        } else
            serverTextChannel2.sendMessage("Only Mazawrath can send this message.");
    }

    String getRecentChangeLog() {
        return "**New beanBOT update released.**\n" +
                "**2.0.0**\n" +
                "\n" +
                "**New**\n" +
                "\t- beanBOT has been reworked from the ground up.\n" +
                "\t\t- \n" +
//                "\t- Created the Bean Market.\n" +
//                "\t\t*Use `.beanmarket` to get started.*\n" +
//                "\t- Added `.beaninvest`.\n" +
                "\t- Added `.blessed`.\n" +
                "\n" +
                "**Changes**\n" +
                "\t- Changed the chances of winning on `.beanbet` from 25% to 35%.\n" +
                "\t- Updated `.loss`\n";
//                "**Bug Fixes**\n" +
    }
}
