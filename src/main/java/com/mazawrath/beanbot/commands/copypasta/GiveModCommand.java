package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Random;

public class GiveModCommand implements CommandExecutor {
    private Points points;

    public GiveModCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"givemod"},
            usage = "givemod",
            description = "Rolls a 10,000 sided die to randomly give mod.",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), server.getIdAsString(), 10)) {
            final String[] messsage = {""};
                if (author.getIdAsString().equals("112653978432503808")) {
                    messsage[0] = "You rolled a 10000. You have to get 10,000. Congrats, you're now a mod!";
                }
                if (messsage[0].equalsIgnoreCase("")) {
                    Random rand = new Random();
                    int n = rand.nextInt(9999) + 1;
                    messsage[0] = "You rolled a " + n + ". You have to get 10,000. Sorry, try again!";
                }
                serverTextChannel.sendMessage(messsage[0]);
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command.");
    }
}
