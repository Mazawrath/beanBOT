package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class AsgCommand implements CommandExecutor {
    private Points points;

    public AsgCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"asg"},
            usage = "asg",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(null, author, server);

        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("asg", null, "https://cdn.discordapp.com/attachments/480959729330290688/492393589939109935/asg.png")
                    .setDescription("asg")
                    .setThumbnail("https://cdn.discordapp.com/attachments/480959729330290688/492393589939109935/asg.png")
                    .addInlineField("asg", "asg")
                    .addInlineField("asg", "asg")
                    .setImage("https://cdn.discordapp.com/attachments/480959729330290688/492393589939109935/asg.png")
                    .setFooter("asg", "https://cdn.discordapp.com/attachments/480959729330290688/492393589939109935/asg.png");
            serverTextChannel.sendMessage(embed);
        } else
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");

        Sentry.clearContext();
    }
}
