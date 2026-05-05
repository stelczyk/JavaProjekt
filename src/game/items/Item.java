package game.items;

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
        return price / 2; //wstępnie dalem, ze mozna sprzedac za polowe cene
    }
}