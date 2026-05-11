package game.rival;

import game.constants.GameConstants;
import game.player.Player;
import game.player.PlayerAttribute;

/**
 * Odpowiada za przeliczanie atrybutów i poziomu Rywala.
 *
 * Philosophy:
 * 1. Pobierz CAŁKOWITĄ SUMĘ atrybutów gracza
 * 2. Pomnóż przez losowy modyfikator [0.8 - 1.2]
 * 3. Nowa suma → wylicz Level
 * 4. Rozdziel nową pulę punktów na atrybuty Rywala
 */
public class RivalAttributesRecalculator {

    private static final double MIN_ATTRIBUTE_MODIFIER = 0.8;
    private static final double MAX_ATTRIBUTE_MODIFIER = 1.2;

    // Korelacja: 10 punktów atrybutów = 1 level
    // To jest balans mechaniki - można rozdzielić na etapie gameplay
    private static final int ATTRIBUTES_PER_LEVEL = 10;

    /**
     * Przelicza atrybuty Rywala na podstawie gracza.
     *
     * Algorytm:
     * 1. Suma wszystkich atrybutów GRACZA
     * 2. Modyfikator losowy x [0.8 - 1.2]
     * 3. Nowa suma → przeliczenie na Level
     * 4. Rozprowadzenie nowej puli atrybutów
     *
     * @param player oryginalny gracz (do odczytania sum)
     * @param rival klon gracza (będzie zmodyfikowany)
     * @return wyliczony level dla Rywala
     */
    public static int recalculateRivalAttributes(Player player, Player rival) {
        // STEP 1: Pobierz całkowitą sumę atrybutów GRACZA
        int originalAttributeSum = calculateTotalAttributeSum(player.getAttributes());

        // STEP 2: Wylicz losowy modyfikator [0.8 - 1.2]
        double attributeModifier = generateRandomModifier();

        // STEP 3: Oblicz NOWĄ SUMĘ atrybutów dla Rywala
        int newAttributeSum = (int) (originalAttributeSum * attributeModifier);

        // STEP 4: Wylicz Level na podstawie nowej sumy
        // Level = (newAttributeSum / ATTRIBUTES_PER_LEVEL) + 1
        int calculatedLevel = Math.max(1, (newAttributeSum / ATTRIBUTES_PER_LEVEL) + 1);

        // Ogranicz do maksymalnego poziomu
        calculatedLevel = Math.min(calculatedLevel, GameConstants.MAX_PLAYER_LEVEL);

        // STEP 5: Rozprowadź atrybuty Riwala losowo
        distributeAttributesRandomly(rival.getAttributes(), newAttributeSum);

        // STEP 6: Ustaw level i XP
        // XP = 0 (gracz ma nowy bilet do rozgrywek)
        // Level zostanie ustawiony przez RivalGenerator

        return calculatedLevel;
    }

    /**
     * Recalculates armor (equipment defense bonus) with modifier.
     *
     * Logic:
     * 1. Get current armor from PLAYER (all equipped clothing defense bonuses)
     * 2. Apply random modifier [0.8 - 1.2]
     * 3. Store result for equipment purchase logic
     *
     * @param player gracz źródłowy (dla pancerza)
     * @return zmieniona wartość pancerza
     */
    public static int recalculateArmorBonus(Player player) {
        // Pancerz pochodzi z wyposażenia
        int currentArmor = player.getInventory().getTotalDefenseBonus();

        // Jeśli gracz nic nie ma założonego, pancerz = 0
        if (currentArmor == 0) {
            // Minimum armor dla Rywala
            return 0;
        }

        // Modyfikator pancerza [0.8 - 1.2]
        double armorModifier = generateRandomModifier();
        int modifiedArmor = (int) (currentArmor * armorModifier);

        return Math.max(0, modifiedArmor);
    }

    /**
     * Wylicza całkowitą sumę wszystkich atrybutów.
     *
     * Sumujemy:
     * - Strength, Defense, Accuracy, Stamina
     * - Brave, Cunning, Speed, Pathology
     * - Valor, Connections
     *
     * = 10 atrybutów razem
     */
    private static int calculateTotalAttributeSum(PlayerAttribute attributes) {
        return attributes.getStrength()
                + attributes.getDefense()
                + attributes.getAccuracy()
                + attributes.getStamina()
                + attributes.getBrave()
                + attributes.getCunning()
                + attributes.getSpeed()
                + attributes.getPathology()
                + attributes.getValor()
                + attributes.getConnections();
    }

    /**
     * Rozprowadza nową pulę atrybutów LOSOWO między wszystkie atrybuty Riwala.
     *
     * Celowość:
     * - Każdy Rival jest UNIKATOWY
     * - Może być silny w jednym atrybucie, słaby w innym
     * - Tworzy zróżnicowaną AI
     *
     * Algorytm:
     * 1. Usuń domyślne atrybuty (każde zaczyna na 5)
     * 2. Podziel nową pulę losowo
     * 3. Upgrade każde atrybuty
     */
    private static void distributeAttributesRandomly(PlayerAttribute rival, int totalPoints) {
        // Najpierw: reset do bazowych wartości (done by DeepCloneUtils)

        // Pozostało do rozdania: totalPoints - (10 * DEFAULT_START_ATTRIBUTE_VALUE)
        // Każdy atrybut ma 5, razem jest 50
        int baseTotalPoints = 10 * GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
        int pointsToDistribute = totalPoints - baseTotalPoints;

        // Jeśli wynik jest ujemny (modyfikator < 0.5), ustaw minimum
        if (pointsToDistribute < 0) {
            pointsToDistribute = 0;
        }

        // Tablica atrybutów do upgradu
        game.player.attributes.CharacterAttributeType[] attributeTypes =
            game.player.attributes.CharacterAttributeType.values();

        // Losowe rozprowadzenie
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < pointsToDistribute; i++) {
            // Wybierz LOSOWY atrybut
            int randomIndex = random.nextInt(attributeTypes.length);
            // Upgrade'uj go
            rival.upgradeAttribute(attributeTypes[randomIndex]);
        }
    }

    /**
     * Generuje losowy modyfikator z zakresu [0.8 - 1.2].
     *
     * Celowość:
     * - Rival może być lepszy lub gorszy od gracza
     * - Losowość vs Predictability
     *
     * @return wartość z zakresu [0.8, 1.2]
     */
    private static double generateRandomModifier() {
        java.util.Random random = new java.util.Random();
        // [0.0 - 1.0) * (1.2 - 0.8) + 0.8 = [0.8 - 1.2]
        return random.nextDouble() * (MAX_ATTRIBUTE_MODIFIER - MIN_ATTRIBUTE_MODIFIER) + MIN_ATTRIBUTE_MODIFIER;
    }

    /**
     * Publiczna metoda do wygenerowania losowego modyfikatora (dla testów i zewnętrznych użytków).
     */
    public static double getRandomModifier() {
        return generateRandomModifier();
    }
}

