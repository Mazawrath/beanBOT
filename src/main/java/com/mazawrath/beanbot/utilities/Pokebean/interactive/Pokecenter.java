package com.mazawrath.beanbot.utilities.Pokebean.interactive;

import com.mazawrath.beanbot.utilities.Pokebean.Monster.Pokebean;

public class Pokecenter {
    private Pokecenter() {}

    public static void healParty(final Player p) {
        for(Pokebean pokebean : p.getParty()) {
            if(pokebean != null)
                pokebean.revive();
        }
    }
}
