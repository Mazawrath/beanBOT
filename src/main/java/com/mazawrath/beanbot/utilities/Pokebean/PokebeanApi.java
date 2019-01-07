package com.mazawrath.beanbot.utilities.Pokebean;

import com.mazawrath.beanbot.utilities.Twitch;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class PokebeanApi {
    private static Connection conn;

    private static final RethinkDB r = RethinkDB.r;
    private static final String DB_NAME = "Pokebean";
    private static final String TWITCH_CHANNEL_LIST_TABLE = "PokemonOwned";
    private static final String PLAYER_PARTY_TABLE = "PlayerParty";

    public PokebeanApi(String clientId, String ipAddress, Connection conn) {
        PokebeanApi.conn = conn;
    }

}
