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
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.0.0\n" +
                "**v2.0.0**\n" +
                "\n" +
                "**New**\n" +
                "\t- beanBOT has been reworked from the ground up.\n" +
                "\t\t- beanBOT now uses sdcf4j for super easy command management and command parsing.\n" +
                "\t- beanBOT is now open source on https://github.com/Mazawrath/beanBOT\n" +
//                "\t- Created the Bean Market.\n" +
//                "\t\t*Use `.beanmarket` to get started.*\n" +
//                "\t- Added `.beaninvest`.\n" +
                "\t- Added `.blessed`.\n" +
                "\t- Added `.stfu`\n" +
                "\t- Added `.source`\n" +
                "\n" +
                "**Changes**\n" +
                "\t- Changed the chances of winning on `.beanbet` from 25% to 35%.\n" +
                "\t- Updated `.loss`\n";
//                "**Bug Fixes**\n" +
    }
}
