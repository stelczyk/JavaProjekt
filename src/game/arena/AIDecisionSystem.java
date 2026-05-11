package game.arena;

import game.constants.GameConstants;
import game.player.Player;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.movement.Move;
import game.player.combat.movement.MovementDirection;
import java.util.List;
import java.util.Random;

/**
 * AIDecisionSystem - Inteligencja Rywala na Arenie.
 *
 * ALGORYTM DECYZJI:
 * 1. Bazowa szansa na OPTYMALNY ruch: 50%
 * 2. Modyfikator Cunning: Cunning * 2% (dodatkowo)
 * 3. Jeśli OPTYMALNY: wybierz najopłacalniejszy ruch
 * 4. Jeśli NIE: wybierz LOSOWY lub SUBOPTYMALNY
 *
 * FORMULA: optimalChance = 0.50 + (Cunning - 5) * 0.02
 * (Cunning=5 (default) → 50%, Cunning=15 → 70%, Cunning=25 → 90%)
 */
public class AIDecisionSystem {

    private final Random random = new Random();

    /**
     * Główna metoda decyzji AI.
     * Wybiera najlepszą akcję (atak lub ruch) dla Rywala.
     *
     * @param rival rywal (ze stankiem walki)
     * @param player gracz (do analizy)
     * @param availableAttacks dostępne ataki
     * @param availableMoves dostępne ruchy
     * @param arenaPosition pozycje na Arenie
     * @return wybrany ruch (atak lub move)
     */
    public Object makeDecision(
            Player rival,
            Player player,
            List<AbstractAttack> availableAttacks,
            List<Move> availableMoves,
            ArenaPosition arenaPosition) {

        // STEP 1: Oblicz szansę na optymalny ruch
        double optimalChance = calculateOptimalChance(rival);

        if (ArenaConstants.DEBUG_AI) {
            System.out.println("[AI] Cunning: " + rival.getAttributes().getCunning() +
                             " → Optimal Chance: " + (optimalChance * 100) + "%");
        }

        boolean chooseOptimal = random.nextDouble() < optimalChance;

        // STEP 2: Podejmij decyzję
        if (chooseOptimal) {
            return chooseOptimalAction(rival, player, availableAttacks, availableMoves, arenaPosition);
        } else {
            return chooseRandomAction(rival, availableAttacks, availableMoves, arenaPosition);
        }
    }

    /**
     * Oblicza szansę na wybranie OPTYMALNEGO ruchu.
     *
     * FORMULA:
     * optimalChance = BASE_OPTIMAL_CHANCE + (cunning - DEFAULT_START_ATTRIBUTE) * CUNNING_MULTIPLIER
     * optimalChance = 0.50 + (cunning - 5) * 0.02
     *
     * Ograniczenia: [0.0, 1.0]
     *
     * @param rival rywal (ze sznpem)
     * @return szansa [0.0, 1.0]
     */
    public double calculateOptimalChance(Player rival) {
        int cunning = rival.getAttributes().getCunning();
        int baseValue = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;

        double chance = ArenaConstants.BASE_OPTIMAL_CHANCE +
                       (cunning - baseValue) * ArenaConstants.CUNNING_MULTIPLIER;

        // Ogranicz do [0.0, 1.0]
        return Math.max(0.0, Math.min(1.0, chance));
    }

    /**
     * Wybiera OPTYMALNY ruch - matematycznie najbardziej opłacalny.
     *
     * Kryteria:
     * 1. Jeśli może atakować z wysoką szansą: atak (MAJą SPRAWDZIĆ ZASIĘG!)
     * 2. Jeśli może się zbliżyć bezpiecznie: zbliżenie
     * 3. Jeśli zagrożony: odsunięcie
     *
     * @return wybrany ruch lub atak
     */
    private Object chooseOptimalAction(
            Player rival,
            Player player,
            List<AbstractAttack> availableAttacks,
            List<Move> availableMoves,
            ArenaPosition arenaPosition) {

        int currentDistance = arenaPosition.getDistance();
        int rivalReach = rival.getAttributes().getSpeed() / 2;

        // OPCJA 1: Najlepszy atak - ALE TYLKO JEŚLI JEST W ZASIĘGU!
        if (!availableAttacks.isEmpty() && currentDistance <= rivalReach) {
            // Sortuj ataki po damage (descending)
            AbstractAttack bestAttack = availableAttacks.stream()
                .max((a, b) -> {
                    try {
                        int damageA = a.calculateDamage(rival.getAttributes(), null);
                        int damageB = b.calculateDamage(rival.getAttributes(), null);
                        return Integer.compare(damageA, damageB);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .orElse(null);

            if (bestAttack != null) {
                if (ArenaConstants.DEBUG_AI) {
                    System.out.println("[AI] Choosing OPTIMAL attack - highest damage (reach: " + rivalReach + ", distance: " + currentDistance + ")");
                }
                return bestAttack;
            }
        }

        // OPCJA 2: Zbliżenie (jeśli można i jest bezpieczne)
        if (!availableMoves.isEmpty() && arenaPosition.canApproach(rivalReach)) {
            Move approachMove = availableMoves.stream()
                .filter(m -> m.getDirection() == MovementDirection.LEFT)  // Dla Rywala: LEFT = zbliżenie (w stronę Gracza)
                .findFirst()
                .orElse(null);

            if (approachMove != null) {
                if (ArenaConstants.DEBUG_AI) {
                    System.out.println("[AI] Choosing OPTIMAL move - approaching enemy (distance: " + currentDistance + " > reach: " + rivalReach + ")");
                }
                return approachMove;
            }
        }

        // OPCJA 3: Default - losowy ruch z lista dostepnych
        if (!availableMoves.isEmpty()) {
            Move randomMove = availableMoves.get(random.nextInt(availableMoves.size()));
            if (ArenaConstants.DEBUG_AI) {
                System.out.println("[AI] Choosing RANDOM move (distance: " + currentDistance + ", reach: " + rivalReach + ")");
            }
            return randomMove;
        }

        // FALLBACK - pierwszy atak (nawet jeśli spoza zasięgu - "głupia" gra)
        if (!availableAttacks.isEmpty()) {
            if (ArenaConstants.DEBUG_AI) {
                System.out.println("[AI] FALLBACK - Choosing attack despite distance! (distance: " + currentDistance + " > reach: " + rivalReach + ")");
            }
            return availableAttacks.get(0);
        }

        return null;
    }

    /**
     * Wybiera LOSOWY lub SUBOPTYMALNY ruch.
     * Zwraca ruch, który może być nieoptym dalny strategicznie.
     *
     * @return wybrany ruch
     */
    private Object chooseRandomAction(
            Player rival,
            List<AbstractAttack> availableAttacks,
            List<Move> availableMoves,
            ArenaPosition arenaPosition) {

        // Łącz wszystkie dostępne akcje
        List<Object> allActions = new java.util.ArrayList<>();
        allActions.addAll(availableAttacks);
        allActions.addAll(availableMoves);

        if (allActions.isEmpty()) {
            return null; // Brak akcji
        }

        Object chosen = allActions.get(random.nextInt(allActions.size()));

        if (ArenaConstants.DEBUG_AI) {
            System.out.println("[AI] Choosing RANDOM action: " +
                (chosen instanceof AbstractAttack ? "Attack" : "Move"));
        }

        return chosen;
    }
}

