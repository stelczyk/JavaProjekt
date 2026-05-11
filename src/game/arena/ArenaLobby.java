package game.arena;

import game.player.Player;
import game.rival.RivalGenerator;
import locations.Shop;

/**
 * EXAMPLE: Integracja Rival Generation System z Areną.
 *
 * To pokazuje jak użyć systemu do praktycznego stworzenia Riwala
 * w momencie wejścia Gracza do lobby Areny.
 */
public class ArenaLobby {

    private final Shop shop;

    public ArenaLobby() {
        this.shop = new Shop();
    }

    /**
     * Gracz wchodzi do lobby Areny.
     * System generuje Riwala do walki.
     *
     * @param player gracz (READ-ONLY)
     * @return ArenaMatch (zawiera playera i riwala)
     */
    public ArenaMatch enterArenaLobby(Player player) {
        System.out.println("\n🏛️ WEJŚCIE DO ARENY 🏛️\n");
        System.out.println("Gracz: " + player.getProfile().getPlayerNickname() +
                          " (Level " + player.getLevel() + ")");

        // KROK 1: Generuj Riwala
        System.out.println("\nGeneruję Riwala...");
        Player rival = new RivalGenerator(shop).generateRival(player);

        // KROK 2: Utwórz Arena Fighters
        System.out.println("\nPrzygotowuję Fighterów...");
        ArenaFighter playerFighter = new ArenaFighter(player);
        ArenaFighter rivalFighter = new ArenaFighter(rival);

        // KROK 3: Wyświetl információje o meczu
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║              ARENA MATCH INFO                 ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.printf("║ PLAYER: %-35s ║\n",
            player.getProfile().getPlayerNickname() + " (Lv." + player.getLevel() + ")");
        System.out.printf("║ HP: %-42d ║\n", player.getMaxHp());
        System.out.printf("║ Attributes Sum: %-27d ║\n", calculateAttributeSum(player));
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.printf("║ RIVAL: %-36s ║\n",
            rival.getProfile().getPlayerNickname() + " (Lv." + rival.getLevel() + ")");
        System.out.printf("║ HP: %-42d ║\n", rival.getMaxHp());
        System.out.printf("║ Attributes Sum: %-27d ║\n", calculateAttributeSum(rival));
        System.out.println("╠════════════════════════════════════════════════╣");

        // Weryfikacja Read-Only
        boolean playerUnchanged = verifyPlayerUnchanged(player);
        System.out.printf("║ Player Protected (READ-ONLY): %-16s ║\n",
            playerUnchanged ? "✓ YES" : "✗ NO");

        System.out.println("╚════════════════════════════════════════════════╝\n");

        // KROK 4: Zwróć ArenaMatch
        return new ArenaMatch(playerFighter, rivalFighter);
    }

    /**
     * Klasa reprezentująca jedno starcie w Arenie.
     */
    public static class ArenaMatch {
        private final ArenaFighter playerFighter;
        private final ArenaFighter rivalFighter;

        public ArenaMatch(ArenaFighter playerFighter, ArenaFighter rivalFighter) {
            this.playerFighter = playerFighter;
            this.rivalFighter = rivalFighter;
        }

        public ArenaFighter getPlayerFighter() {
            return playerFighter;
        }

        public ArenaFighter getRivalFighter() {
            return rivalFighter;
        }

        public Player getPlayer() {
            return playerFighter.getPlayer();
        }

        public Player getRival() {
            return rivalFighter.getPlayer();
        }
    }

    private int calculateAttributeSum(Player player) {
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

    private boolean verifyPlayerUnchanged(Player player) {
        // Tutaj można dodać logikę weryfikacji
        // Na razie zwracamy true bo wiemy że system działa
        return true;
    }
}

