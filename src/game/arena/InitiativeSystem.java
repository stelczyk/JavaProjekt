package game.arena;

import game.player.Player;
import java.util.Random;

/**
 * InitiativeSystem - Ustalanie kolejności ruchów w walce.
 *
 * ZASADA PATOLOGII:
 * - Postać z WYŻSZYM Pathology ZAWSZE zaczyna pierwszą turę
 * - W razie remisu (Pathology równe): losowy rzut monetą
 * - Initiatywę ustala się raz na początek walki
 *
 * PSYCHOLOGIA:
 * Pathology = "chorobliwość" -> wyższe wartości = bardziej agresywny, rażący charakter
 * Osoba bardziej nieobliczalna/szalona atakuje pierwsza
 */
public class InitiativeSystem {

    private final Random random = new Random();
    private boolean playerFirst; // true = gracz zaczyna, false = rywal

    /**
     * Ustala INICJATYWĘ dla walki.
     *
     * FORMULA:
     * 1. Porównaj Pathology obu stron
     * 2. Wyższy Pathology = pierwsi ruch
     * 3. Remis = moneta (50/50)
     *
     * @param player gracz
     * @param rival rywal
     * @return true = gracz first, false = rival first
     */
    public boolean rollInitiative(Player player, Player rival) {
        int playerPathology = player.getAttributes().getPathology();
        int rivalPathology = rival.getAttributes().getPathology();

        if (playerPathology > rivalPathology) {
            playerFirst = true;
        } else if (rivalPathology > playerPathology) {
            playerFirst = false;
        } else {
            // Remis - 50/50
            playerFirst = random.nextBoolean();
        }

        return playerFirst;
    }

    /**
     * Czy gracz ma inicjatywę (zaczyna)?
     *
     * @return true = gracz first
     */
    public boolean isPlayerFirst() {
        return playerFirst;
    }

    /**
     * Czy rywal ma inicjatywę (zaczyna)?
     *
     * @return true = rival first
     */
    public boolean isRivalFirst() {
        return !playerFirst;
    }

    /**
     * Główna metoda do pobrania kolejności turys.
     * Zwraca tablicę: [0] = pierwszy, [1] = drugi
     *
     * @return tablica Fighters w kolejności (Player/Rival obiekty)
     */
    public String[] getTurnOrder() {
        if (playerFirst) {
            return new String[]{"PLAYER", "RIVAL"};
        } else {
            return new String[]{"RIVAL", "PLAYER"};
        }
    }

    @Override
    public String toString() {
        return "Initiative: " + (playerFirst ? "Player FIRST" : "Rival FIRST");
    }
}

