package game.player.combat.attack.special;

import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.state.FighterState;

/**
 * Abstrakcyjna klasa bazowa dla specjalnych ataków.
 * Special attacks różnią się od standardowych ataków tym, że:
 * - Często mają dodatkowe efekty poza zwykłymi obrażeniami
 * - Mogą być uzależnione od specyficznych warunków (np. pasek postępu)
 * - Wykorzystują nietypowe kombinacje atrybutów
 * - Mogą pomijać standardowe mechaniki defensywne
 */
public abstract class AbstractSpecialAttack extends AbstractAttack {

    @Override
    protected double calculateStaminaContribution(FighterState state) {
        // Specjalne ataki zazwyczaj nie są ograniczone przez wyczerpanie fizyczne
        // Są bardziej mentalne/psychologiczne niż fizyczne
        return 0.0;
    }

    @Override
    protected double calculateEquipmentContribution() {
        // Specjalne ataki nie korzystają z broni - są oparte na umiejętnościach społecznych
        return 0.0;
    }

    /**
     * Metoda sprawdzająca czy specjalny atak może być użyty w danym momencie.
     * Każdy special attack może mieć własne wymagania (np. pełny pasek, minimalne atrybuty)
     *
     * @param attacker atrybuty atakującego
     * @param state stan walki
     * @return true jeśli atak może być wykonany
     */
    public abstract boolean canBeUsed(ArenaAttackPlayerAttributes attacker, FighterState state);

    /**
     * Dodatkowe efekty specjalne które występują po użyciu ataku.
     * Nadpisuje bazową implementację z AbstractAttack aby umożliwić bardziej złożone efekty.
     */
    @Override
    public abstract void applySpecialEffects(FighterState state);
}
