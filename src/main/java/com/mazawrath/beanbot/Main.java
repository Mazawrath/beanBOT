package com.mazawrath.beanbot;

import com.mazawrath.beanbot.commands.*;
import com.mazawrath.beanbot.commands.beanlottery.BeanLotteryCommand;
import com.mazawrath.beanbot.commands.beanmarket.BeanInvestCommand;
import com.mazawrath.beanbot.commands.beanmarket.BeanMarketCommand;
import com.mazawrath.beanbot.commands.copypasta.GiveModCommand;
import com.mazawrath.beanbot.commands.beancoin.*;
import com.mazawrath.beanbot.commands.copypasta.*;
import com.mazawrath.beanbot.utilities.*;
import com.mazawrath.beanbot.commands.admin.*;
import com.mazawrath.beanbot.utilities.jersey.RestServer;
import com.rethinkdb.net.Connection;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
//import org.apache.log4j.BasicConfigurator;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import static com.rethinkdb.RethinkDB.r;

public class Main {
    private static DiscordApi api;

    public static void main(String[] args) {
        //BasicConfigurator.configure();
        // Enable debugging, if no slf4j logger was found
        //FallbackLoggerConfiguration.setDebug(false);

        GoogleCloudVision cloudVision = new GoogleCloudVision();
        try {
            cloudVision.checkImange();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Connection conn = r.connection().hostname("localhost").port(28015).connect();

        Points points = new Points(conn);
        StockMarket stockMarket = new StockMarket(conn);
        Lottery lottery = new Lottery(conn);
        Thread restServer = new Thread(new RestServer());
        restServer.start();
        Twitch twitch = new Twitch(args[1], args[2], conn);


        new DiscordApiBuilder().setToken(args[0]).login().thenAccept(api -> {
            System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());

            Main.api = api;
            Twitch.setApi(api);

            // Instantiate command handler
            CommandHandler cmdHandler = new JavacordHandler(api);

            // Set the default prefix
            cmdHandler.setDefaultPrefix(".");

            // Register commands

            // Standard
            cmdHandler.registerCommand(new HelpCommand(cmdHandler));
            cmdHandler.registerCommand(new UserInfoCommand(points));
            cmdHandler.registerCommand(new ServerInfoCommand());
            cmdHandler.registerCommand(new ReactCommand(points));
            cmdHandler.registerCommand(new SourceCommand());
            // beanCoin
            cmdHandler.registerCommand(new BeanBalanceCommand(points));
            cmdHandler.registerCommand(new BeanFreeCommand(points));
            cmdHandler.registerCommand(new BeanBetCommand(points));
            cmdHandler.registerCommand(new BeanTransferCommand(points));
            cmdHandler.registerCommand(new BeanBoardCommand(points));
            // Bean Market
            cmdHandler.registerCommand(new BeanMarketCommand());
            cmdHandler.registerCommand(new BeanInvestCommand(points, stockMarket));
            // Bean Lottery Commands
            cmdHandler.registerCommand(new BeanLotteryCommand(points, lottery));
            // Admin commands
            cmdHandler.registerCommand(new AdminPostChangeLogCommand());
            cmdHandler.registerCommand(new AdminDeleteMessageCommand());
            cmdHandler.registerCommand(new AdminForceLotteryDrawingCommand(points, lottery));
            cmdHandler.registerCommand(new AdminAddBeanCoinCommand(points));
            cmdHandler.registerCommand(new AdminRemoveBeanCoinCommand(points));
            cmdHandler.registerCommand(new AdminPostMessageCommand());
            cmdHandler.registerCommand(new AdminPostHelpCommand(cmdHandler));
            cmdHandler.registerCommand(new AdminTwitch(twitch));
            // Copypasta
            cmdHandler.registerCommand(new Top500Command(points));
            cmdHandler.registerCommand(new GiveModCommand(points));
            cmdHandler.registerCommand(new ThirtyPercentWinrateCommand(points));
            cmdHandler.registerCommand(new CodeRedCommand(points));
            cmdHandler.registerCommand(new BlessedCommand(points));
            cmdHandler.registerCommand(new StfuCommand(points));
            cmdHandler.registerCommand(new LossCommand(points));
            cmdHandler.registerCommand(new ShameCommand(points));
            cmdHandler.registerCommand(new AsgCommand(points));
            cmdHandler.registerCommand(new GrindCommand(points));
            cmdHandler.registerCommand(new GnomedCommand(points));
            cmdHandler.registerCommand(new EightBallCommand(points));
        });
    }

}
