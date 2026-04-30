package game.player.combat.attack.melee;

import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.state.FighterState;

public abstract class AbstractMeleeAttack extends AbstractAttack {

    private static final double STRENGTH_WEIGHT = 1.0; // Siła oddaje 100% swojej wartości
    private static final double STAMINA_WEIGHT = 0.4;  // Stamina oddaje tylko 40% wartości do siły ciosu

    @Override
    protected double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker) {
        // Dla Melee głównym atrybutem jest SIŁA
        return attacker.getStrength() * STRENGTH_WEIGHT;
    }

    @Override
    protected double calculateStaminaContribution(FighterState state) {
        // Jeśli ma 100 staminy, doda +40 do mocy. Jeśli ma 0 staminy, zmienie to kiedyś zeby nie było można oddac tego ciosu, albo jakis ogolny warunek pod spodem ze nie mozna ciosu oddawac jak stamina=0
        return state.getCurrentStamina() * STAMINA_WEIGHT;
    }

    @Override
    protected double calculateEquipmentContribution() {
        return 0.0; // tutaj bedzie dodawac logike sklepu
    }
}