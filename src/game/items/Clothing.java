package game.items;

public class Clothing extends Item{
    private final int defenseBonus;
    private final int speedBonus;
    private final ClothingSlot slot;

    public Clothing(String name, int price, int requiredLevel, int defenseBonus, int speedBonus, ClothingSlot slot) {
        super(name, price, requiredLevel);
        this.defenseBonus = defenseBonus;
        this.speedBonus = speedBonus;
        this.slot = slot;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

    public ClothingSlot getSlot() { return slot; }

}