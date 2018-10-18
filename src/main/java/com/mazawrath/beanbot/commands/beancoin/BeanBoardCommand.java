package com.mazawrath.beanbot.commands.beancoin;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;

public class BeanBoardCommand implements CommandExecutor{
    private Points points;

    public BeanBoardCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanboard", "coinboard"},
            description = "Gets a leaderboard of who has the most beanCoin on the server.",
            usage = "beanboard",
            privateMessages = false
    )

    public void onCommand(ServerTextChannel serverTextChannel, User author, Server server, DiscordApi api) {
        final String[] users = {""};
        final String[] beanBalance = {""};

        JSONArray mJSONArray = new JSONArray(points.getLeaderboard(server.getIdAsString()));

        for (int i = 0; i < mJSONArray.length(); i++) {
            JSONObject obj = new JSONObject(mJSONArray.get(i).toString());

            api.getCachedUserById(obj.getString("id")).ifPresent(user -> {
                users[0] += user.getDisplayName(server) + "\n";
				        BigDecimal userPoints = new BigDecimal(Points.parseValueFromDB(obj.getString("Points"))).setScale(Points.SCALE, Points.ROUNDING_MODE);
                beanBalance[0] += Points.pointsToString(userPoints) + "\n";
            });
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("beanCoin Leaderboard")
                .addInlineField("User", users[0])
                .addInlineField("beanCoinBalance", beanBalance[0])
                .setThumbnail("https://cdn.discordapp.com/attachments/260314299820408832/488478598467158016/beanCoin.png");
        serverTextChannel.sendMessage(embed);
    }
}
