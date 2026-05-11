package game.arena;

import game.arena.ArenaFighterState;
import game.player.Player;
import java.util.Random;

/**
 * DamageCalculator - System obliczania obrażeń na Arenie.
 *
 * MECHANIKA OBRAŻEŃ:
 *
 * SPARRING MODE (Treningowy):
 * - Pancerz IGNOROWANY
 * - Obrażenia ZAWSZE trafiają HP
 * - Critical Hit: całkowicie ignoruje pancerz + instant kill
 *
 * TOURNAMENT MODE (Rywalizacyjny):
 * - Pancerz CHRONI (zmniejsza obrażenia)
 * - Najpierw pancerz: dmg -= armor (min 0)
 * - Potem HP: hpDamage = dmg (jeśli armor = 0)
 * - Critical Hit: jeden shot jeśli dmg > hp+armor
 */
public class DamageCalculator {

    private final Random random = new Random();

    /**
     * Oblicza i aplicuje obrażenia defenderowi.
     *
     * @param attacker atak ellactor
     * @param defender broniący się
     * @param baseDamage bazowe obrażenia z ataku
     * @param defenderState stan walki broniącego
     * @param mode tryb walki
     * @return rezultat zadanego obrażenia (info dla UI)
     */
    public DamageResult applyDamage(
            Player attacker,
            Player defender,
            int baseDamage,
            ArenaFighterState defenderState,
            CombatMode mode) {

        // STEP 1: Sprawdź Critical Hit
        boolean isCritical = rollForCritical(attacker, defender);

        if (isCritical) {
            return applyCriticalDamage(attacker, defender, baseDamage, defenderState, mode);
        }

        // STEP 2: Zwykłe obrażenia
        if (mode.isTraining()) {
            return applySparringDamage(baseDamage, defenderState);
        } else {
            return applyTournamentDamage(baseDamage, defenderState);
        }
    }

    /**
     * Czy zadany będzie cios krytyczny?
     *
     * FORMULA:
     * criticalChance = BASE (0.10) + (attackerValor - defenderValor) * VALOR_MULTIPLIER (0.01)
     * criticalChance = 0.10 + (attacker.valor - defender.valor) * 0.01
     *
     * Ograniczenia: [0.0, 1.0]
     *
     * @return czy kritycz
     */
    public boolean rollForCritical(Player attacker, Player defender) {
        int attackerValor = attacker.getAttributes().getValor();
        int defenderValor = defender.getAttributes().getValor();

        double baseCriticalChance = ArenaConstants.BASE_CRITICAL_CHANCE;
        double valorDifference = (attackerValor - defenderValor) * ArenaConstants.VALOR_MULTIPLIER;
        double finalChance = baseCriticalChance + valorDifference;

        // Ograniczenia
        finalChance = Math.max(0.0, Math.min(1.0, finalChance));

        return random.nextDouble() < finalChance;
    }

    /**
     * Aplikuje CRITICAL obrażenia.
     *
     * Sparring: instant kill (ignores armor completely)
     * Tournament: one-shot jeśli dmg > hp+armor
     *
     * @return rezultat critical
     */
    private DamageResult applyCriticalDamage(
            Player attacker,
            Player defender,
            int baseDamage,
            ArenaFighterState defenderState,
            CombatMode mode) {

        int multipliedDamage = (int) (baseDamage * 1.5); // 150% damage multiplier

        if (mode.isTraining()) {
            // Sparring: instant kill
            defenderState.reduceHp(defenderState.getCurrentHp()); // Kill instantly
            return new DamageResult(multipliedDamage, 0, multipliedDamage, true, true);
        } else {
            // Tournament: one-shot check
            int totalHp = defenderState.getCurrentHp() + defenderState.getCurrentArmor();
            boolean isOneShotKill = multipliedDamage > totalHp;

            if (isOneShotKill) {
                defenderState.reduceHp(defenderState.getCurrentHp());
                defenderState.reduceArmor(defenderState.getCurrentArmor());
                return new DamageResult(multipliedDamage, multipliedDamage, 0, true, true);
            } else {
                return applyTournamentDamage(multipliedDamage, defenderState);
            }
        }
    }

    /**
     * Aplikuje obrażenia w trybie SPARRING.
     * Pancerz IGNOROWANY, obrażenia trafiają HP.
     *
     * @return rezultat
     */
    private DamageResult applySparringDamage(int baseDamage, ArenaFighterState defenderState) {
        defenderState.reduceHp(baseDamage);
        return new DamageResult(baseDamage, 0, baseDamage, false, false);
    }

    /**
     * Aplikuje obrażenia w trybie TOURNAMENT.
     *
     * Logika:
     * 1. Pancerz ZMNIEJSZA obrażenia: actualDamage = baseDamage - armor
     * 2. Pancerz idzie do 0 najpierw: armorDamage = min(baseDamage, armor)
     * 3. Pozostałe damage idzie do HP: hpDamage = max(0, baseDamage - armor)
     *
     * @return rezultat
     */
    private DamageResult applyTournamentDamage(int baseDamage, ArenaFighterState defenderState) {
        int currentArmor = defenderState.getCurrentArmor();

        // Pancerz idzie do 0 najpierw
        int armorDamage = Math.min(baseDamage, currentArmor);
        int remainingDamage = baseDamage - armorDamage;

        defenderState.reduceArmor(armorDamage);
        defenderState.reduceHp(remainingDamage);

        return new DamageResult(baseDamage, armorDamage, remainingDamage, false, false);
    }

    /**
     * Rezultat zadanego obrażenia.
     */
    public static class DamageResult {
        public final int totalDamage;      // Całkowite obrażenia (do display)
        public final int armorDamage;      // Ile pancerza zostało zniszczone
        public final int hpDamage;         // Ile HP zostało uszkodzne
        public final boolean wasCritical;  // Czy był cios krytyczny
        public final boolean wasInstantKill; // Czy zabił w jednym ciosie

        public DamageResult(int total, int armor, int hp, boolean crit, boolean kill) {
            this.totalDamage = total;
            this.armorDamage = armor;
            this.hpDamage = hp;
            this.wasCritical = crit;
            this.wasInstantKill = kill;
        }

        @Override
        public String toString() {
            return String.format("Dmg=%d (armor=%d, hp=%d)%s%s",
                totalDamage, armorDamage, hpDamage,
                wasCritical ? " [CRITICAL]" : "",
                wasInstantKill ? " [ONE-SHOT]" : "");
        }
    }
}

