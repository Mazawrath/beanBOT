package com.mazawrath.beanbot.commands.copypasta;

import com.mazawrath.beanbot.utilities.Points;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Random;

public class EightBallCommand implements CommandExecutor {
    private Points points;

    public EightBallCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"beanBall"},
            usage = "beanBall",
            description = "Rolls a 10,000 sided die to randomly give mod.",
            privateMessages = false
    )

    public void onCommand(DiscordApi api, String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            if (args.length == 0) {
                serverTextChannel.sendMessage("You didn't even ask me a question...");
                return;
            }
            Random r = new Random();

            int choice = 1 + r.nextInt(16);
            String response;

            if ( choice == 1 )
                response = "The Bean Gods command it so.";
            else if ( choice == 2 )
                response = "It is decidedly yes.";
            else if ( choice == 3 )
                response = "I'm pretty sure yeah.";
            else if ( choice == 4 )
                response = "Guaranteed the answer is yes.";
            else if ( choice == 5 )
                response = "You may rely on it.";
            else if ( choice == 6 )
                response = "No, why would you ever think that?";
            else if ( choice == 7 )
                response = "beanBOT thinks that is an absolute no";
            else if ( choice == 8 )
                response = "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO\n" +
                        "NO";
            else if ( choice == 9 )
                response = "I'm thinking of a number between \"No\" and \"Absolutely no\"";
            else if ( choice == 10 )
                response = "No.";
            else if ( choice == 11 )
                response = "Sorry, Maza is a terrible programmer and didn't expect this output to ever happen so I dunno figure the answer out yourself.";
            else if ( choice == 12 )
                response = "java.lang.reflect.InvocationTargetException\n" +
                        "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
                        "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "\tat java.base/java.lang.reflect.Method.invoke(Method.java:564)\n" +
                        "\tat de.btobastian.sdcf4j.handler.JavacordHandler.invokeMethod(JavacordHandler.java:150)\n" +
                        "\tat de.btobastian.sdcf4j.handler.JavacordHandler.handleMessageCreate(JavacordHandler.java:134)\n" +
                        "\tat de.btobastian.sdcf4j.handler.JavacordHandler.lambda$new$0(JavacordHandler.java:59)\n" +
                        "\tat org.javacord.core.util.event.EventDispatcher.lambda$dispatchMessageCreateEvent$93(EventDispatcher.java:2499)\n" +
                        "\tat org.javacord.core.util.event.EventDispatcherBase.lambda$dispatchEvent$10(EventDispatcherBase.java:191)\n" +
                        "\tat org.javacord.core.util.event.EventDispatcherBase.lambda$checkRunningListenersAndStartIfPossible$21(EventDispatcherBase.java:270)\n" +
                        "\tat java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:514)\n" +
                        "\tat java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)\n" +
                        "\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)\n" +
                        "\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)\n" +
                        "\tat java.base/java.lang.Thread.run(Thread.java:844)\n" +
                        "Caused by: java.lang.NullPointerException\n" +
                        "\tat com.rethinkdb.ast.ReqlAst.run(ReqlAst.java:65)\n" +
                        "\tat com.mazawrath.beanbot.utilities.Points.checkServer(Points.java:41)\n" +
                        "\tat com.mazawrath.beanbot.utilities.Points.checkUser(Points.java:48)\n" +
                        "\tat com.mazawrath.beanbot.utilities.Points.removePoints(Points.java:94)\n" +
                        "\tat com.mazawrath.beanbot.commands.copypasta.GnomedCommand.onCommand(GnomedCommand.java:27)\n" +
                        "\t... 15 more";
            else if ( choice == 13 )
                response = "I dunno go ask shteeeb.";
            else if ( choice == 14 )
                response = "If I say the answer is \"I don't know\" that doesn't mean you get to try the command again to get a different response.";
            else if ( choice == 15 )
                response = "Stop being so lazy and just run this code yourself ```java\n" +
                        "package com.mazawrath.beanbot.commands.copypasta;\n" +
                        "\n" +
                        "import com.mazawrath.beanbot.utilities.Points;\n" +
                        "import de.btobastian.sdcf4j.Command;\n" +
                        "import de.btobastian.sdcf4j.CommandExecutor;\n" +
                        "import org.javacord.api.DiscordApi;\n" +
                        "import java.util.Random;\n" +
                        "\n" +
                        "public class EightBallCommand implements CommandExecutor {\n" +
                        " @Command(\n" +
                        " aliases={\"beanBall\"},\n" +
                        " usage=\"beanBall\",\n" +
                        " )\n" +
                        "\n" +
                        " public void onCommand(DiscordApi api, ServerTextChannel serverTextChannel) {\n" +
                        " if (points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {\n" +
                        " Random r=new Random();\n" +
                        "\n" +
                        " int choice=1 + r.nextInt(15);\n" +
                        " String response;\n" +
                        "\n" +
                        " if (choice=1)\n" +
                        " response=\"The Bean Gods command it so.\";\n" +
                        " else if (choice=2)\n" +
                        " response=\"It is decidedly yes.\";\n" +
                        " else if (choice=3)\n" +
                        " response=\"I'm pretty sure yeah.\";\n" +
                        " else if (choice=4)\n" +
                        " response=\"Guaranteed the answer is yes.\";\n" +
                        " else if (choice=5)\n" +
                        " response=\"You may rely on it.\";\n" +
                        " else if (choice=6)\n" +
                        " response=\"No, why would you ever think that?\";\n" +
                        " else if (choice=7)\n" +
                        " response=\"beanBOT thinks that is an absolute no\";\n" +
                        " else if (choice=8)\n" +
                        " response=\"NO\\n\" +\n" +
                        " \"NO\\n\" +\n" +
                        " \"NO\";\n" +
                        " else if (choice=9)\n" +
                        " response=\"I'm thinking of a number between \\\"No\\\" and \\\"Absolutely no\\\"\";\n" +
                        " else if (choice=10)\n" +
                        " response=\"Yes\";\n" +
                        " else if (choice=11)\n" +
                        " response=\"Sorry, Maza is a terrible programmer and didn't expect this output to ever happen so I dunno the answer out yourself.\";\n" +
                        " else if ( choice=12 )\n" +
                        " response=\"there isn’t enough space for this response\";\n" +
                        " else if ( choice=13 )\n" +
                        " response=\"Screw that, I'm not even going to answer that.\";\n" +
                        " else if ( choice=14 )\n" +
                        " response=\"If I say the answer is \\\"I don't know\\\" that doesn't mean you magically get to try the command again to get a different response.\";\n" +
                        " else if ( choice=15 )\n" +
                        " response=\"Stop being so lazy and just run this code yourself\"\n" +
                        " else\n" +
                        " response=\"8-BALL ERROR!\";\n" +
                        "\n" +
                        " }\n" +
                        " }\n" +
                        "}```";
            else if ( choice == 16 )
                response = "```                __\n" +
                        "             .-'  |\n" +
                        "            /   <\\|     'Ello me ol' chum\n" +
                        "           /     \\'\n" +
                        "           |_.- o-o      I'm g'not a g'nelf\n" +
                        "           / C  -._)\\\n" +
                        "          /',        |   I'm g'not a g'noblin\n" +
                        "         |   `-,_,__,'\n" +
                        "         (,,)====[_]=|   I'm a g'nome and you've been GNOMED\n" +
                        "           '.   ____/\n" +
                        "            | -|-|_\n" +
                        "            |____)_)```";
            else
                response = "8-BALL ERROR!";

            serverTextChannel.sendMessage( "MAGIC BEAN-BALL SAYS: " + response );
        }
    }
}
