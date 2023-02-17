package edu.ntnu.idatt2001.paths.game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String name;
    int health;
    int score;
    int gold;
    List<String> inventory;

    public Player(String name, int health, int score, int gold) {
        this.name = name;
        this.health = health;
        this.score = score;
        this.gold = gold;
        inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    public int getGold() {
        return gold;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void addHealth(int health) {
        if (this.health + health < 0) {
            throw new IllegalArgumentException("Health cannot be less than 0");
        }
        this.health += health;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void addGold(int gold) {
        this.gold += gold;
    }

    public void addToInventory(String item) {
        inventory.add(item);
    }

    @Override
    public String toString() {
        return String.format("%-15s %3d HP  %4d PTS  %4d GOLD  INV: %s",
                name, health, score, gold, String.join(", ", inventory));
    }

}
