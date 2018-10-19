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
                "Detailed changelog can be found on https://github.com/Mazawrath/beanBOT/releases/tag/v2.4.0\n" +
                "**v2.4.0**\n" +
                "**New**\n" +
                "\t- beanCoin now supports decimals.\n" +
                "\t- Added currency symbol for beanCoin.\n" +
                "\t- beanCoin is correctly spaced by commas.\n" +
                "\t- Added Bean Market investing.\n" +
                "**Changes**\n" +
                "\t- Changed the chances of `.givemod` giving mod to 100%.\n" +
                "\t- Changed amount of beanCoin that `.beanfree` gives from 25 beanCoin to 25.69 beanCoin.\n" +
                "**Bug Fixes**\n" +
                "\t- Many backend bug fixes and spell corrections have been done.";
    }
}
