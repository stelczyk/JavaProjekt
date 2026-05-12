package game.items;

public class Weapon extends Item{
    private final int strengthBonus;
    private final int accuracyBonus;

    public Weapon(String name, int price, int requiredLevel, int strengthBonus, int accuracyBonus) {
        super(name, price, requiredLevel, false);
        this.strengthBonus = strengthBonus;
        this.accuracyBonus = accuracyBonus;
    }

    // Dla jednorazowych broni (Raca, Mołotow)
    public Weapon(String name, int price, int requiredLevel, int strengthBonus, int accuracyBonus, boolean consumable) {
        super(name, price, requiredLevel, consumable);
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