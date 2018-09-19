package com.mazawrath.beanbot;

import com.mazawrath.beanbot.commands.*;
import com.mazawrath.beanbot.commands.GivemodCommand;
import com.mazawrath.beanbot.commands.beancoin.*;
import com.mazawrath.beanbot.commands.copypasta.*;
import com.mazawrath.beanbot.commands.maza.MazaAddBeanCoinCommand;
import com.mazawrath.beanbot.commands.maza.MazaDeleteMessageCommand;
import com.mazawrath.beanbot.commands.maza.MazapostchangelogCommand;
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

            // Standard
            cmdHandler.registerCommand(new HelpCommand(cmdHandler));
            cmdHandler.registerCommand(new UserinfoCommand(points));
            cmdHandler.registerCommand(new ServerInfoCommand());
            cmdHandler.registerCommand(new ReactCommand(points));
            cmdHandler.registerCommand(new SourceCommand());
            // beanCoin
            cmdHandler.registerCommand(new BeanbalanaceCommand(points));
            cmdHandler.registerCommand(new BeanFreeCommand(points));
            cmdHandler.registerCommand(new BeanbetCommand(points));
            cmdHandler.registerCommand(new BeanTransferCommand(points));
            cmdHandler.registerCommand(new BeanboardCommand(points));
            // Mazawrath commands
            cmdHandler.registerCommand(new MazapostchangelogCommand());
            cmdHandler.registerCommand(new MazaDeleteMessageCommand());
            cmdHandler.registerCommand(new MazaAddBeanCoinCommand(points));
            // Copypasta
            cmdHandler.registerCommand(new Top500Command(points));
            cmdHandler.registerCommand(new GivemodCommand(points));
            cmdHandler.registerCommand(new ThirtyPercentWinrateCommand(points));
            cmdHandler.registerCommand(new CodeRedCommand(points));
            cmdHandler.registerCommand(new BlessedCommand(points));
            cmdHandler.registerCommand(new StfuCommand(points));
            cmdHandler.registerCommand(new LossCommand(points));
        });
    }
}
