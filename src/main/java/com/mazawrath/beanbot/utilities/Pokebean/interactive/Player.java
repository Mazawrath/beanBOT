package com.mazawrath.beanbot.utilities.Pokebean.interactive;


import com.mazawrath.beanbot.utilities.Pokebean.Monster.Pokebean;

import java.util.Arrays;

public class Player {
    private boolean inBattle;
    private int money;
    private final String NAME;
    private Bag bag;
    private Pokebean[] party;

    public Player(final String name) {
        this.inBattle = false;
        this.money = 0;
        this.NAME = name;
        this.bag = new Bag();
        this.party = new Pokebean[6];
    }

    public boolean isInBattle() {
        return inBattle;
    }

    public int getMoney() {
        return this.money;
    }

    public String getName() {
        return NAME;
    }

    public Bag getBag() {
        return this.bag;
    }

    public Pokebean[] getParty(){
        byte count = (byte)party.length;
        for(Pokebean p : party) {
            if(p == null){
                count--;
            }
        }
        return Arrays.copyOfRange(party, 0, count);
    }

    public void setBattleState(final boolean inBattle) {
        this.inBattle = inBattle;
    }

    public boolean isPartyEmpty() {
        return getParty().length == 0;
    }

    /**
     * Attempts to add a <code>Pokemon</code> to your party.
     * @param pokemon <code>Pokemon</code> to add to the party.
     * @return <code>true</code> if the pokemon was added, <code>false</code> otherwise.
     */
    public boolean addToParty(final Pokebean pokemon) {
        for(int i = 0; i < party.length; i++) {
            if(party[i] == null) {
                party[i] = pokemon;
                return true;
            }
        }
        return false;
    }

    public boolean swapSlots(final int one, final int two) {
        if((one < 0 || one > 5) || (two < 0 || two > 5)) {
            return false;
        }
        final Pokebean temp = getParty()[one];
        this.party[one] = this.party[two];
        this.party[two] = temp;
        return true;
    }

    @Override
    public String toString() {
        return "Name: " + getName() +
               "\nMoney: " + getMoney() +
               "\nBag contents " + getBag().getMap() +
               "\nParty contents " + Arrays.toString(getParty());
    }
}
