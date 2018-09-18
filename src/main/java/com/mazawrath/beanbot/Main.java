package com.mazawrath.beanbot;

import com.mazawrath.beanbot.commands.*;
import com.mazawrath.beanbot.commands.GivemodCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanboardCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanfreeCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbalanaceCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbetCommand;
import com.mazawrath.beanbot.commands.copypasta.*;
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

            // Copypasta
            cmdHandler.registerCommand(new Top500Command(dbConn));
            cmdHandler.registerCommand(new GivemodCommand(dbConn));
            cmdHandler.registerCommand(new ThirtyPercentWinrateCommand(dbConn));
            cmdHandler.registerCommand(new CoderedCommand(dbConn));
            cmdHandler.registerCommand(new BlessedCommand(dbConn));
            cmdHandler.registerCommand(new StfuCommand(dbConn));
            // beanCoin
            cmdHandler.registerCommand(new BeanbalanaceCommand(dbConn));
            cmdHandler.registerCommand(new BeanfreeCommand(dbConn));
            cmdHandler.registerCommand(new BeanbetCommand(dbConn));
            cmdHandler.registerCommand(new BeanboardCommand(dbConn));
            // Mazawrath commands
            cmdHandler.registerCommand(new MazapostchangelogCommand());
            // Other
            cmdHandler.registerCommand(new ReactCommand(dbConn));
            cmdHandler.registerCommand(new UserinfoCommand(dbConn));
            cmdHandler.registerCommand(new ServerInfoCommand(dbConn));
            cmdHandler.registerCommand(new HelpCommand(cmdHandler));
        });
    }
}
