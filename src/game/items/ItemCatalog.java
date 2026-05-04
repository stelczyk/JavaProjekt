package game.items;

import java.util.List;

public class ItemCatalog {

    public static final List<Weapon> WEAPONS = List.of(
            new Weapon("Butelka",              20,  1, 1, 2),
            new Weapon("Kastet",               80,  1, 2, 3),
            new Weapon("Kij bejsbolowy",      100,  1, 3, 1),
            new Weapon("Rekawice bokserskie", 120,  2, 2, 2),
            new Weapon("Raca",                150,  3, 1, 4),
            new Weapon("Maczeta",             300,  5, 5, 2),
            new Weapon("Molotow",             400,  7, 2, 5)
    );

    public static final List<Clothing> CLOTHING = List.of(
            new Clothing("Zonobijka",    30,  1, 1, 2, ClothingSlot.TULOWIE),
            new Clothing("Szalik",       50,  1, 2, 1, ClothingSlot.AKCESORIA),
            new Clothing("Buty Ferrari", 200, 3, 1, 4, ClothingSlot.BUTY)
    );

    private ItemCatalog() {}
}