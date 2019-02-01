package com.mazawrath.beanbot.commands.admin;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class AdminLookupUser implements CommandExecutor {

    @Command(
            aliases = {"AdminLookupUser"},
            privateMessages = false,
            showInHelpPage = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) throws ExecutionException, InterruptedException {
        SentryLog.addContext(args, author, server);

        if (!author.isBotOwner()) {
            serverTextChannel.sendMessage("Only " + api.getOwner().get().getDiscriminatedName() + " can use this command.");
            return;
        }

        User lookup = api.getUserById(args[0]).get();
        EmbedBuilder messageBuilder = new EmbedBuilder();
        if (lookup != null) {
            messageBuilder.setTitle("Username: " + lookup.getDiscriminatedName());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < lookup.getMutualServers().size(); i++) {
                Server[] server1 = new Server[0];
                 server1 = lookup.getMutualServers().toArray(server1);
                stringBuilder.append(server1[i].getIdAsString() + ",");
            }
            messageBuilder.addField("Mutal servers", stringBuilder.toString());

            serverTextChannel.sendMessage(messageBuilder);

            Sentry.clearContext();
        }
    }
}
