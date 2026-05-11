package game.player.combat.regeneration;

import game.player.combat.state.FighterState;
import game.player.attributes.types.ArenaRegeneratePlayerAttributes;

/**
 * Abstrakcyjna klasa bazowa dla różnych metod regeneracji postaci na Arenie.
 *
 * Każda konkretna metoda regeneracji dziedziczy z tej klasy i implementuje
 * specifyczną logikę przywracania staminyi oraz HP.
 *
 * WZORZEC PROJEKTOWY: Strategy Pattern
 * - Każda implementacja to inna strategia regeneracji
 * - Można je zamieniać dynamicznie w trakcie walki
 * - Każda ma inny koszt, efekt i opis
 */
public abstract class AbstractRegeneratePlayer {

    protected String regenerationName;
    protected int staminaRestored;
    protected int healthRestored;
    protected String description;

    public AbstractRegeneratePlayer(String regenerationName, int staminaRestored, int healthRestored, String description) {
        this.regenerationName = regenerationName;
        this.staminaRestored = staminaRestored;
        this.healthRestored = healthRestored;
        this.description = description;
    }

    /**
     * Główna metoda wykonania regeneracji.
     * Przywraca staminę i HP postaci.
     */
    public void executeRegeneration(FighterState fighterState) {
        fighterState.increaseStamina(staminaRestored);
        if (healthRestored > 0) {
            fighterState.increaseHp(healthRestored);
        }
    }

    // ==================== GETTERS ====================

    public String getRegenerationName() {
        return regenerationName;
    }

    public int getStaminaRestored() {
        return staminaRestored;
    }

    public int getHealthRestored() {
        return healthRestored;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s (Stamina: +%d, HP: +%d) - %s",
            regenerationName, staminaRestored, healthRestored, description);
    }
}

