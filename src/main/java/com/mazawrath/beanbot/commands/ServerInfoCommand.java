package com.mazawrath.beanbot.commands;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ServerInfoCommand implements CommandExecutor {
    private Points points;

    public ServerInfoCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"serverinfo"},
            usage = ".serverinfo",
            description = "DO NOT @ USER. Enter user's discriminated name (Example#XXXX). Pulls up information about a user on the server. Leave username blank to look up yourself.",
            privateMessages = false
    )

    public void onCommand(String command, String userName, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        Date date = new Date(server.getCreationTimestamp().getEpochSecond() * 1000L);
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a");
        format.setTimeZone(TimeZone.getTimeZone("EST"));
        String formatted = format.format(date);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(server.getName())
                .setDescription("Created at " + formatted)
                .addInlineField("Region", server.getRegion().getName())
                .addInlineField("Users", String.valueOf(server.getMemberCount()))
                .addInlineField("Roles", server.getRoles().size() + " roles")
                .setFooter("Server ID: " + server.getIdAsString());
        if (server.getIcon().isPresent())
            embed.setThumbnail(server.getIcon().get());
        serverTextChannel.sendMessage(embed);
    }
}
