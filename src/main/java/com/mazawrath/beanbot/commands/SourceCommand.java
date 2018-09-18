package com.mazawrath.beanbot.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class SourceCommand implements CommandExecutor {
    @Command(
            aliases = {"source"},
            description = "Gives a link to beanBOT's source code",
            usage = "top500",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        serverTextChannel.sendMessage("https://github.com/Mazawrath/beanBOT");
    }
}
