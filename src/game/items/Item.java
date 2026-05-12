package game.items;

import java.util.Objects;

public abstract class Item {
    private final String name;
    private final int price;
    private final int requiredLevel;
    // czy przedmiot znika po użyciu (np. Raca, Mołotow)
    private final boolean consumable;

    public Item(String name, int price, int requiredLevel) {
        this(name, price, requiredLevel, false);
    }

    public Item(String name, int price, int requiredLevel, boolean consumable) {
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.consumable = consumable;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getSellPrice() {
        return price / 2;
    }

    /**
     * [KACPER] Czy przedmiot znika po użyciu (Raca, Mołotow = true).
     * Użyj w ArenaCombatEngine po wykonaniu ataku:
     *
     *   Weapon used = attacker.getInventory().getEquippedWeapon();
     *   if (used != null && used.isConsumable()) {
     *       attacker.getInventory().unequipWeapon();
     *       attacker.getInventory().removeItem(used);
     *       System.out.println(used.getName() + " zostało zużyte!");
     *   }
     */
    public boolean isConsumable() {
        return consumable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return price == item.price
                && requiredLevel == item.requiredLevel
                && name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, requiredLevel);
    }
}