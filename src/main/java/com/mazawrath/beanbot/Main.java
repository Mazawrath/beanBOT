package com.mazawrath.beanbot;

import com.mazawrath.beanbot.commands.*;
import com.mazawrath.beanbot.commands.GivemodCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanFreeCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbalanaceCommand;
import com.mazawrath.beanbot.commands.beancoin.BeanbetCommand;
import com.mazawrath.beanbot.commands.copypasta.CoderedCommand;
import com.mazawrath.beanbot.commands.copypasta.ThirtyPercentWinrateCommand;
import com.mazawrath.beanbot.commands.copypasta.Top500Command;
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
            // beanCoin
            cmdHandler.registerCommand(new BeanbalanaceCommand(dbConn));
            cmdHandler.registerCommand(new BeanFreeCommand(dbConn));
            cmdHandler.registerCommand(new BeanbetCommand(dbConn));
            // Other
            cmdHandler.registerCommand(new ReactCommand(dbConn));
            cmdHandler.registerCommand(new UserinfoCommand(dbConn));
            cmdHandler.registerCommand(new ServerInfoCommand(dbConn));
            cmdHandler.registerCommand(new HelpCommand(cmdHandler));
        });
    }
}
