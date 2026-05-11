package game.rival;

import game.constants.GameConstants;
import game.player.Player;
import game.player.PlayerProfile;
import locations.Shop;

/**
 * DEMONSTRATION: Rival Generation System
 *
 * Pokazuje pełny flow generowania Rywala:
 * 1. Gracza ma atrybuty, pieniądze, ekwipunek
 * 2. Generyator tworzy Rywala (niezmieniony gracz)
 * 3. Rival ma losowe, przeliczone statystyki
 * 4. Rival ma losowy ekwipunek
 */
public class RivalGenerationDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         RIVAL GENERATION SYSTEM - DEMONSTRATION            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // KROK 1: Utwórz gracza
        System.out.println("[01] CREATING PLAYER...");
        PlayerProfile playerProfile = new PlayerProfile("Janusz", 30);
        Player player = new Player(playerProfile);

        // Daj graczowi trochę pieniędzy i ekwipunku
        player.earnMoney(1000);
        player.gainXp(100);

        // Upgrade atrybutów
        for (int i = 0; i < 10; i++) {
            player.getAttributes().upgradeAttribute(
                game.player.attributes.CharacterAttributeType.STRENGTH);
        }
        for (int i = 0; i < 5; i++) {
            player.getAttributes().upgradeAttribute(
                game.player.attributes.CharacterAttributeType.DEFENSE);
        }

        System.out.println("✓ Player created:");
        printPlayerStats(player);

        // KROK 2: Wykonaj snapshot oryginalnego gracza
        System.out.println("\n[02] TAKING SNAPSHOT OF ORIGINAL PLAYER...");
        String originalName = player.getProfile().getPlayerNickname();
        int originalLevel = player.getLevel();
        int originalMoney = player.getMoney();
        int originalAttributes = calculateTotalAttributeSum(player);

        System.out.println("✓ Snapshot taken (będziemy weryfikować read-only)");

        // KROK 3: Generuj Rywala
        System.out.println("\n[03] GENERATING RIVAL...");
        RivalGenerator generator = new RivalGenerator(new Shop());
        Player rival = generator.generateRival(player);

        System.out.println("\n✓ Rival generated:");
        printPlayerStats(rival);

        // KROK 4: Weryfikacja bezpieczeństwa (Player jest READ-ONLY)
        System.out.println("\n[04] VERIFICATION - PLAYER REMAINED UNCHANGED (READ-ONLY)...");
        boolean playerUnchanged =
            player.getProfile().getPlayerNickname().equals(originalName) &&
            player.getLevel() == originalLevel &&
            player.getMoney() == originalMoney &&
            calculateTotalAttributeSum(player) == originalAttributes;

        System.out.println("Player Name: " + player.getProfile().getPlayerNickname() +
                          " (expected: " + originalName + ") → " + (player.getProfile().getPlayerNickname().equals(originalName) ? "✓" : "✗"));
        System.out.println("Player Level: " + player.getLevel() +
                          " (expected: " + originalLevel + ") → " + (player.getLevel() == originalLevel ? "✓" : "✗"));
        System.out.println("Player Money: " + player.getMoney() +
                          " (expected: " + originalMoney + ") → " + (player.getMoney() == originalMoney ? "✓" : "✗"));
        System.out.println("Player Attributes: " + calculateTotalAttributeSum(player) +
                          " (expected: " + originalAttributes + ") → " + (calculateTotalAttributeSum(player) == originalAttributes ? "✓" : "✗"));

        if (playerUnchanged) {
            System.out.println("\n✓✓✓ SUCCESS! Player remained completely unchanged (READ-ONLY) ✓✓✓");
        } else {
            System.out.println("\n✗✗✗ FAILURE! Player was modified! ✗✗✗");
        }

        // KROK 5: Porównanie
        System.out.println("\n[05] COMPARISON - PLAYER vs RIVAL...");
        System.out.println("\n┌─────────────────┬─────────────┬─────────────┐");
        System.out.println("│ Attribute       │ Player      │ Rival       │");
        System.out.println("├─────────────────┼─────────────┼─────────────┤");

        System.out.printf("│ Name            │ %-11s │ %-11s │\n",
            player.getProfile().getPlayerNickname(), rival.getProfile().getPlayerNickname());
        System.out.printf("│ Level           │ %-11d │ %-11d │\n",
            player.getLevel(), rival.getLevel());
        System.out.printf("│ HP (max)        │ %-11d │ %-11d │\n",
            player.getMaxHp(), rival.getMaxHp());
        System.out.printf("│ Money           │ %-11d │ %-11d │\n",
            player.getMoney(), rival.getMoney());
        System.out.printf("│ Attr Sum        │ %-11d │ %-11d │\n",
            calculateTotalAttributeSum(player), calculateTotalAttributeSum(rival));
        System.out.printf("│ Armor Bonus     │ %-11d │ %-11d │\n",
            player.getInventory().getTotalDefenseBonus(), rival.getInventory().getTotalDefenseBonus());
        System.out.printf("│ Items Owned     │ %-11d │ %-11d │\n",
            player.getInventory().getOwnedItems().size(), rival.getInventory().getOwnedItems().size());

        System.out.println("└─────────────────┴─────────────┴─────────────┘");

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              DEMONSTRATION COMPLETED SUCCESSFULLY!           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    private static void printPlayerStats(Player player) {
        System.out.println("  Name: " + player.getProfile().getPlayerNickname());
        System.out.println("  Level: " + player.getLevel());
        System.out.println("  HP (max): " + player.getMaxHp());
        System.out.println("  Money: " + player.getMoney());
        System.out.println("  Attributes Sum: " + calculateTotalAttributeSum(player));
        System.out.println("  Armor Bonus: " + player.getInventory().getTotalDefenseBonus());
        System.out.println("  Items Owned: " + player.getInventory().getOwnedItems().size());
    }

    private static int calculateTotalAttributeSum(Player player) {
        return player.getAttributes().getStrength()
                + player.getAttributes().getDefense()
                + player.getAttributes().getAccuracy()
                + player.getAttributes().getStamina()
                + player.getAttributes().getBrave()
                + player.getAttributes().getCunning()
                + player.getAttributes().getSpeed()
                + player.getAttributes().getPathology()
                + player.getAttributes().getValor()
                + player.getAttributes().getConnections();
    }
}

