package com.mazawrath.beanbot.utilities.Pokebean;

public class PokebeanMonster {
    private String name;
    private String nickName;
    private int id;
    private int pokebeanNumber;

    private Type pokebeanType;
    private int level;
    private int xp;
    private int xpMax;

    private PokebeanMove move1;
    private PokebeanMove move2;
    private PokebeanMove move3;
    private PokebeanMove move4;

    private int currentHp;
    private int maxHp;
    private int attack;
    private int defense;
    private int spAttack;
    private int spDefense;
    private int speed;
    private int power;

    public PokebeanMonster(Type pokebeanType, int level, int currentHp, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed, int power) {
        this.pokebeanType = pokebeanType;
        this.level = level;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
        this.power = currentHp + maxHp + attack + defense+ spAttack+ spDefense + speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPokebeanNumber() {
        return pokebeanNumber;
    }

    public void setPokebeanNumber(int pokebeanNumber) {
        this.pokebeanNumber = pokebeanNumber;
    }

    public Type getPokebeanType() {
        return pokebeanType;
    }

    public void setPokebeanType(Type pokebeanType) {
        this.pokebeanType = pokebeanType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXpMax() {
        return xpMax;
    }

    public void setXpMax(int xpMax) {
        this.xpMax = xpMax;
    }

    public PokebeanMove getMove1() {
        return move1;
    }

    public void setMove1(PokebeanMove move1) {
        this.move1 = move1;
    }

    public PokebeanMove getMove2() {
        return move2;
    }

    public void setMove2(PokebeanMove move2) {
        this.move2 = move2;
    }

    public PokebeanMove getMove3() {
        return move3;
    }

    public void setMove3(PokebeanMove move3) {
        this.move3 = move3;
    }

    public PokebeanMove getMove4() {
        return move4;
    }

    public void setMove4(PokebeanMove move4) {
        this.move4 = move4;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpAttack() {
        return spAttack;
    }

    public void setSpAttack(int spAttack) {
        this.spAttack = spAttack;
    }

    public int getSpDefense() {
        return spDefense;
    }

    public void setSpDefense(int spDefense) {
        this.spDefense = spDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
