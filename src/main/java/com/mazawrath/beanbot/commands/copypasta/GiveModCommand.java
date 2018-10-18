package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
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

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST_SPECIAL)) {
            if (author.getIdAsString().equals("112653978432503808")) {
                serverTextChannel.sendMessage("You rolled a 10000. You have to get 10,000. Congrats, you're now a mod!");
            }
            else {
                Random rand = new Random();
                int n = rand.nextInt(9999) + 1;
                serverTextChannel.sendMessage("You rolled a " + n + ". You have to get 10,000. Sorry, try again!");
            }
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command.");
    }
}
