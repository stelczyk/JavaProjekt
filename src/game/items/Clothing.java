package game.items

public class Clothing extends Item{
    private final int defenseBonus;
    private final int speedBonus;

    public Clothing(String name, int price, int requiredLevel, int defenseBonus, int speedBonus) {
        super(name, price, requiredLevel);
        this.defenseBonus = defenseBonus;
        this.speedBonus = speedBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

}