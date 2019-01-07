package com.mazawrath.beanbot.utilities.Pokebean.Monster;

import java.awt.*;
import java.util.Arrays;

public class Pokebean
{
    private String nickname;
    private Species species;
    //private final String OT;
    private final Nature NATURE;
    private final Gender GENDER;
    private final byte[] IVS;

    //Poison, Paralyze, Burn, Frozen, Asleep, Seeded
    private boolean[] status;

    //Byte -128 -> 127
    private byte level;

    private int totalExpForNextLevel, totalExp;

    //HP, Attack, Defense, Sp. Attack, Sp. Defense, Speed
    private short[] currentStats, inBattleStats;
    private byte[] evs;
    private Move[] moveSet;

    private Image pokedexImage,
                  fromBackImage,
                  fromFrontImage;

    /**
     * Creates a new Pokebean with a nickname.
     * @param nickname The nickname of the Pokebean.
     * @param s The Pokebean Species that this Pokebean will be based off of.
     */
    public Pokebean(String nickname, Species s)
    {
        this(s, 5);
        this.nickname = nickname;
    }

    public Pokebean(Species s)
    {
        this(s, 5);
    }
    /**
     * Creates a brand new Pokebean based on a given species. This Pokebean will start at level 5. If you want to evolve a Pokebean, use the copy constructor.
     * @param species The Pokebean Species that this Pokebean will be based off of.
     */
    public Pokebean(Species species, int level)
    {
        //Set the Species of the Pokebean (ie. Charizard, Squirtle, etc...)
        this.species = species;
        GENDER = species.getGender();
        //All Pokebean start out with no status conditions
        status = new boolean[] {false, false, false, false, false, false};

        nickname = species.getName();
        //Needs to be a short because it can get bigger than 128
        inBattleStats = new short[6];
        moveSet = new Move[4];
        
        //IV's only go 0-31;
        IVS = new byte[6];

        for(int i = 0; i < IVS.length; i++)
        {
            IVS[i] = (byte)(Math.random() * 32);
        }

        //EVS all start at 0
        evs = new byte[] {0, 0, 0, 0, 0, 0};

        this.level = (byte)level;

        currentStats = new short[]{
                calculateStat(Stat.HP),
                calculateStat(Stat.ATTACK),
                calculateStat(Stat.DEFENSE),
                calculateStat(Stat.SP_ATTACK),
                calculateStat(Stat.SP_DEFENSE),
                calculateStat(Stat.SPEED)};

        //Copy the contents of currentStats -> inBattleStats
        System.arraycopy(currentStats, 0, inBattleStats, 0, currentStats.length);

        if(level < 100)
        {
            totalExpForNextLevel = species.calculateExp((byte)(level + 1));
        }
        else
        {
            totalExpForNextLevel = species.calculateExp((byte)(100));
        }

        totalExp = species.calculateExp(level);

        NATURE = Nature.values()[(int)Math.random() * Nature.values().length];

        initializeMoves();
    }

    /**
     * Copy constructor that creates a new Pokebean with an evolution.
     * @param evolveTo The species the Pokebean evolves to.
     * @param p The Pokebean that will be copied and evolved.
     */
    public Pokebean(Species evolveTo, Pokebean p)
    {
        species = evolveTo;

        level = p.level;
        NATURE = p.NATURE;
        GENDER = p.GENDER;

        status = new boolean[] {p.status[0], p.status[1], p.status[2], p.status[3], p.status[4], p.status[5]};

        IVS = new byte[] {p.IVS[0], p.IVS[1], p.IVS[2], p.IVS[3], p.IVS[4], p.IVS[5]};
        evs = new byte[] {p.evs[0], p.evs[1], p.evs[2], p.evs[3], p.evs[4], p.evs[5]};

        currentStats = new short[]{
                calculateStat(Stat.HP),
                calculateStat(Stat.ATTACK),
                calculateStat(Stat.DEFENSE),
                calculateStat(Stat.SP_ATTACK),
                calculateStat(Stat.SP_DEFENSE),
                calculateStat(Stat.SPEED)};

        System.arraycopy(currentStats, 0, inBattleStats, 0, currentStats.length);

        totalExpForNextLevel = species.calculateExp((byte)(level + 1));
        totalExp = p.totalExp;
    }

    public boolean[] getStatus() {
        return status;
    }

    public void takeDamage(int damage)
    {
        inBattleStats[(byte)Stat.HP.ordinal()] -= (short)damage;

        if(inBattleStats[(byte)Stat.HP.ordinal()] < 0)
        {
            inBattleStats[(byte)Stat.HP.ordinal()] = 0;
        }
    }

    public short getExpYield()
    {
        return species.getExpYield();
    }

    public boolean isFainted()
    {
        return inBattleStats[(byte)Stat.HP.ordinal()] <= 0;
    }

    public boolean canLearnNewMove()
    {
        return species.getLearnset().containsKey(level);
    }

    private void initializeMoves()
    {
        for(Move m : species.getLearnset().keySet())
        {
            if(species.getLearnset().get(m) <= level)
            {
                for(int j = 0; j < moveSet.length; j++)
                {
                    if(moveSet[j] == null)
                    {
                        moveSet[j] = m;
                        break;
                    }
                }
            }
        }
    }

    public Move[] getMoveSet()
    {
        byte count = (byte)moveSet.length;
        for(Move m : moveSet)
        {
            if(m == null)
            {
                count--;
            }
        }
        return Arrays.copyOfRange(moveSet, 0, count);
    }
    /**
     * Gets the Pokebean's Gender.
     * @return GENDER The Pokebean's Gender.
     */
    public Gender getGender()
    {
        return GENDER;
    }

    /**
     * Gets the Pokebean's name.
     * @return The Pokebean's name.
     */
    public String getName()
    {
        return species.getName();
    }

    public String getNickname()
    {
        return nickname;
    }
    /**
     * Gets the Pokebean's Type.
     * @return The Pokebean's Type.
     */
    public Type[] getType()
    {
        return species.getType();
    }

    public void setLevel(int level)
    {
        this.level = (byte)level;
        recalculateStats();
        totalExpForNextLevel = level < 100 ? species.calculateExp(level + 1) : species.calculateExp(level + 1);
    }
    /**
     * Gets the level of the Pokebean.
     * @return level The level of the Pokebean.
     */
    public int getLevel()
    {
        return level;
    }

    private void recalculateStats()
    {
        for(int i = 0; i < currentStats.length; i++)
        {
            currentStats[i] = calculateStat(Stat.values()[i]);
            inBattleStats[i] = currentStats[i];
        }
    }
    /**
     * Calculates any stat.
     * @param  stat The Constant for the stat that you want to calculate
     * @return the calculated stat
     */
    private short calculateStat(final Stat stat)
    {
        return (short)(stat == Stat.HP ? (((IVS[(byte)Stat.HP.ordinal()] + (2 * species.getBaseStat((byte)Stat.HP.ordinal())) + (evs[(byte)Stat.HP.ordinal()] / 4) + 100) * level) / 100) + 10 :
                (((IVS[(byte)stat.ordinal()] + (2 * species.getBaseStat((byte)stat.ordinal())) + (evs[(byte)stat.ordinal()] / 4)) * level) / 100) + 5);
    }

    /**
     * Gets any battle stat of the Pokebean.
     * @param stat The in battle stat to get.
     * @return The in battle stat of the Pokebean.
     * @throws ArrayIndexOutOfBoundsException
     */
    public short getInBattleStat(final Stat stat) throws ArrayIndexOutOfBoundsException
    {
        return inBattleStats[(byte)stat.ordinal()];
    }

    /**
     * Gets any current stat of the Pokebean.
     * @param stat The current stat to get.
     * @return The current stat of the Pokebean.
     * @throws ArrayIndexOutOfBoundsException
     */
    public short getCurrentStat(final Stat stat) throws ArrayIndexOutOfBoundsException
    {
        return currentStats[(byte)stat.ordinal()];
    }

    /**
     * All this does is make sure that at the beginning of each battle, all stats except HP have been reset to their original values
     */
    public void resetStats()
    {
        //Ignore the HP
        for(int i = Stat.ATTACK.ordinal(); i <= Stat.SPEED.ordinal(); i++)
        {
            inBattleStats[i] = currentStats[i];
        }
    }

    /**
     * Gets the current HP of the Pokebean.
     * @return Current HP of the Pokebean.
     */
    public int getInBattleHp()
    {
        return inBattleStats[(byte)Stat.HP.ordinal()];
    }

    /**
     * This is how the Pokebean gains totalExperience.
     * @param newExp the totalExp to be added.
     */
    public void addExp(int newExp)
    {
        totalExp += newExp;
    }

    public String levelUp()
    {
        String str = "";
        //If we have enough exp
        if(totalExp >= totalExpForNextLevel)
        {
            //level up and set remaining exp
            level++;
            totalExpForNextLevel = species.calculateExp(level + 1);

            System.arraycopy(currentStats, 0, inBattleStats, 0, currentStats.length);

            //
            currentStats = new short[]{
                    calculateStat(Stat.HP),
                    calculateStat(Stat.ATTACK),
                    calculateStat(Stat.DEFENSE),
                    calculateStat(Stat.SP_ATTACK),
                    calculateStat(Stat.SP_DEFENSE),
                    calculateStat(Stat.SPEED)};
            str += species.getName() + " grew to level " + level + "!";

            for(Stat s : Stat.values())
            {
                str += "\n" + s + ": +" + (currentStats[s.ordinal()] - inBattleStats[s.ordinal()]);
            }

            revive();

            return "\n" + str;
        }

        return "Too bad, " + species.getName() + " didn't level up. They need " + (totalExpForNextLevel - totalExp) + " more exp to reach level " + (level + 1) + ".";
    }

    public void revive()
    {
        for(Move m : getMoveSet())
        {
            m.resetPP();
            m.resetAccuracy();
        }

        resetStats();
        //Un-neglect the HP
        inBattleStats[(byte)Stat.HP.ordinal()] = currentStats[(byte)Stat.HP.ordinal()];

        for(byte i = (byte)Status.POISON.ordinal(); i <= (byte)Status.SEED.ordinal(); i++)
        {
            status[i] = false;
        }
    }

    @Override
    public String toString()
    {
        return "\"" + nickname + "\"/" + getName() + ": Level "+ level + ", HP(" + getInBattleHp() + "/" + getCurrentStat(Stat.HP) + ")";
    }
}