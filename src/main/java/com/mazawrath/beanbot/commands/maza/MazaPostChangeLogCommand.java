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
                "Release can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.6.2\n" +
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/compare/v2.6.1...v2.6.2\n" +
                "\n" +
                "**v2.6.2**\n" +
                "**New**\n" +
                "\t- Bean Market\n" +
                "\t\t- Added `WEEB` (MicroWeeb).\n" +
                "**Changes**\n" +
                "\t- Added extra info when using `.beaninvest` without any arguments/";
                //"**Bug Fixes**\n" +
                //"\t- Fixed an issue with selling shares.";
    }
}
