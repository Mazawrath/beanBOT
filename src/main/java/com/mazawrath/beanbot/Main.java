package com.mazawrath.beanbot;

import com.mazawrath.beanbot.commands.GivemodCommand;
import com.mazawrath.beanbot.commands.ReactCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanFreeCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbalanaceCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbetCommand;
import com.mazawrath.beanbot.commands.copypasta.ThirtyPercentWinrateCommand;
import com.mazawrath.beanbot.commands.copypasta.Top500Command;
import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.javacord.api.DiscordApiBuilder;

public class Main {
    public static void main(String[] args) {
        Points points = new Points();

        points.connectDatabase();
        String token = args[0];

        new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
            // Instantiate command handler
            CommandHandler cmdHandler = new JavacordHandler(api);

            // Set the default prefix
            cmdHandler.setDefaultPrefix(".");

            // Register commands

            // Copypasta
            cmdHandler.registerCommand(new Top500Command(points));
            cmdHandler.registerCommand(new GivemodCommand(points));
            cmdHandler.registerCommand(new ThirtyPercentWinrateCommand(points));
            // beanCoin
            cmdHandler.registerCommand(new BeanbalanaceCommand(points));
            cmdHandler.registerCommand(new BeanFreeCommand(points));
            cmdHandler.registerCommand(new BeanbetCommand(points));
            // Other
            cmdHandler.registerCommand(new ReactCommand(points));
        });
    }
}
