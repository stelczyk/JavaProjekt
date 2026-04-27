package game.items;

public class Weapon extends Item{
    private final int strengthBonus;
    private final int accuracyBonus;

    public Weapon(String name, int price, int requiredLevel, int strengthBonus, int accuracyBonus) {
        super(name, price, requiredLevel);
        this.strengthBonus = strengthBonus;
        this.accuracyBonus = accuracyBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getAccuracyBonus() {
        return accuracyBonus;
    }

}