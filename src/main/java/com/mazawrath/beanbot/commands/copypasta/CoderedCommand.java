package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.net.MalformedURLException;
import java.net.URL;

public class CoderedCommand implements CommandExecutor{
    private Points points;

    public CoderedCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"codered"},
            usage = "codered",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 2)) {
            try {
                new MessageBuilder()
                        .addAttachment(new URL("https://cdn.discordapp.com/attachments/480959729330290688/487007609883197471/Halo_label_fuel.png"))
                        .send(serverTextChannel);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
    }
}
