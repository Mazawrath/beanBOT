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
        Points dbConn = new Points();

        dbConn.connectDatabase();
        String token = args[0];

        new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
            // Instantiate command handler
            CommandHandler cmdHandler = new JavacordHandler(api);

            // Set the default prefix
            cmdHandler.setDefaultPrefix(".");

            // Register commands

            // Standard
            cmdHandler.registerCommand(new HelpCommand(cmdHandler));
            cmdHandler.registerCommand(new UserinfoCommand(dbConn));
            cmdHandler.registerCommand(new ServerInfoCommand());
            cmdHandler.registerCommand(new ReactCommand(dbConn));
            cmdHandler.registerCommand(new SourceCommand());
            // beanCoin
            cmdHandler.registerCommand(new BeanbalanaceCommand(dbConn));
            cmdHandler.registerCommand(new BeanFreeCommand(dbConn));
            cmdHandler.registerCommand(new BeanbetCommand(dbConn));
            cmdHandler.registerCommand(new BeanTransferCommand(dbConn));
            cmdHandler.registerCommand(new BeanboardCommand(dbConn));
            // Mazawrath commands
            cmdHandler.registerCommand(new MazapostchangelogCommand());
            cmdHandler.registerCommand(new MazaDeleteMessageCommand());
            cmdHandler.registerCommand(new MazaAddBeanCoinCommand(dbConn));
            // Copypasta
            cmdHandler.registerCommand(new Top500Command(dbConn));
            cmdHandler.registerCommand(new GivemodCommand(dbConn));
            cmdHandler.registerCommand(new ThirtyPercentWinrateCommand(dbConn));
            cmdHandler.registerCommand(new CodeRedCommand(dbConn));
            cmdHandler.registerCommand(new BlessedCommand(dbConn));
            cmdHandler.registerCommand(new StfuCommand(dbConn));
            cmdHandler.registerCommand(new LossCommand(dbConn));
        });
    }
}
