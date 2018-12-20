# beanBOT
[![Build Status](https://travis-ci.com/Mazawrath/beanBOT.svg?branch=master)](https://travis-ci.com/Mazawrath/beanBOT)

beanBOT is my own personal bot that I made for myself, for a discord server I'm in. Although it's not made for public use you can download this repository and use the bot yourself if you want to.

## How to use

### Preparatives
- Download or clone this repository.
- Install [RethinkDB.](https://www.rethinkdb.com/docs/install/)
- Set up an application on [discord.](https://discordapp.com/developers/applications/)
- Add a bot and obtain its token (TOKEN).
- Set up an app on [twitch.](https://glass.twitch.tv/console/apps)
- Obtain the client ID (CLIENT_ID)

### Build
`$ gradlew build`

### Run
 - Via gradle:
 `$ gradlew run -Ptoken="TOKEN"`
 - Standalone:
 `$ java -jar beanBOT-2.0.jar "TOKEN"`

## Things to note about this bot
This bot is made for my own personal use. It's currently being used by a streamer and there are a lot of commands made specifically for him such as the one under copypasta and the entire Bean Market. There are also `.admin` commands that allow you to change values and modify the bot. Although these are not in the `.help` command, you can find these commands in the source code under `admin`. All commands can only be used by the bot owner and some can be used by the server owner.

beanBOT is a Java bot that I have made for https://www.twitch.tv/shteeeb. Check him out for #EpicGamerMoments
