package com.mazawrath.beanbot.commands.poll;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.vdurmont.emoji.EmojiParser;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class PollCommand implements CommandExecutor {
    private Points points;

    public PollCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"poll"},
            usage = "poll [question]",
            description = "Adds a thumbs up and thumbs down emoji to the message.",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, Message message, User author, Server server) {
        SentryLog.addContext(null, author, server);

        message.addReactions(EmojiParser.parseToUnicode(":thumbsup:"), EmojiParser.parseToUnicode(":thumbsdown:"), EmojiParser.parseToUnicode(":shrug:"));

        Sentry.clearContext();
    }
}
