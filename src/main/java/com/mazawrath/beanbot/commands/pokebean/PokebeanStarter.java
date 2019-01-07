package com.mazawrath.beanbot.commands.pokebean;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import java.util.concurrent.atomic.AtomicInteger;

public class PokebeanStarter implements CommandExecutor {
    private Points points;

    public PokebeanStarter(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"PokebeanStarter"},
            usage = "PokebeanStarter",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {

        MessageBuilder messageBuilder = new MessageBuilder()
                .append("Welcome to Pokebean! Select a starter Pokebean to get started!");
        serverTextChannel.sendMessage("Welcome to b");
        AtomicInteger number = new AtomicInteger();

        ListenerManager<MessageCreateListener> listenerManager = null;
        ListenerManager[] listenerArray = new ListenerManager[1];
        listenerArray[0] = listenerManager;
        listenerArray[0] = api.addMessageCreateListener(event -> {
            Message message = event.getMessage();
            String messageContent = message.getContent();
            String fruit;

            if (messageContent.equalsIgnoreCase("1") && message.getAuthor().getId() == author.getId()) {
                number.set(1);
                serverTextChannel.sendMessage(number.toString() + "Select fruit");
                listenerArray[0].remove();
            } else if (messageContent.equalsIgnoreCase("2") && message.getAuthor().getId() == author.getId() && message.getChannel().getId() == serverTextChannel.getId()) {
                number.set(2);
                serverTextChannel.sendMessage(number.toString() + "Select fruit");
                listenerArray[0].remove();
            } else if (messageContent.equalsIgnoreCase("3") && message.getAuthor().getId() == author.getId() && message.getChannel().getId() == serverTextChannel.getId()) {
                number.set(3);
                serverTextChannel.sendMessage(number.toString() + "Select fruit");
                listenerArray[0].remove();
            }
        });
        serverTextChannel.sendMessage(number.toString() + "Select fruit");
    }
}
