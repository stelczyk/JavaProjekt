package game.items;

import java.util.List;

public class ItemCatalog {

    public static final List<Weapon> WEAPONS = List.of(
            new Weapon("Butelka",              20,  1, 1, 2),
            new Weapon("Kastet",               80,  1, 2, 3),
            new Weapon("Kij bejsbolowy",      100,  1, 3, 1),
            new Weapon("Rękawice bokserskie", 120,  2, 2, 2),
            new Weapon("Raca",                150,  3, 1, 4),
            new Weapon("Maczeta",             300,  5, 5, 2),
            new Weapon("Mołotow",             400,  7, 2, 5)
    );

    public static final List<Clothing> CLOTHING = List.of(
            // GLOWA
            new Clothing("Czapka z daszkiem", 40,  1, 1, 1, ClothingSlot.GLOWA),
            new Clothing("Kominiarka",         90,  2, 2, 0, ClothingSlot.GLOWA),
            new Clothing("Kask motocyklowy",  220, 5, 5, 0, ClothingSlot.GLOWA),

            // TULOWIE
            new Clothing("Żonobijka",         30,  1, 1, 2, ClothingSlot.TULOWIE),
            new Clothing("Bluza dresowa",      80,  2, 2, 1, ClothingSlot.TULOWIE),
            new Clothing("Kamizelka taktyczna",200, 4, 4, 0, ClothingSlot.TULOWIE),

            // NOGI
            new Clothing("Dresy",             50,  1, 1, 2, ClothingSlot.NOGI),
            new Clothing("Jeansy bojówki",   110,  2, 2, 1, ClothingSlot.NOGI),
            new Clothing("Spodnie taktyczne",230,  5, 3, 2, ClothingSlot.NOGI),

            // BUTY
            new Clothing("Buty Ferrari",     200,  3, 1, 4, ClothingSlot.BUTY),
            new Clothing("Glany",            160,  2, 3, 2, ClothingSlot.BUTY),

            // AKCESORIA
            new Clothing("Szalik",            50,  1, 2, 1, ClothingSlot.AKCESORIA),
            new Clothing("Łańcuch na szyi",  130,  3, 1, 2, ClothingSlot.AKCESORIA)
    );


    private ItemCatalog() {}
}