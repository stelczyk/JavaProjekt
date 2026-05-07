package game.items;

import java.util.Objects;

public abstract class Item {
    private final String name;
    private final int price;
    private final int requiredLevel;

    public Item(String name, int price, int requiredLevel) {
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
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