package game.player.combat.attack;

import game.player.attributes.arena.ArenaAttackPlayer;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.state.FighterState;

public abstract class AbstractAttack implements ArenaAttackPlayer {

    protected abstract AttackStyle getAttackStyle();
    protected abstract int getBaseStaminaCost();

    protected abstract double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker);
    protected abstract double calculateStaminaContribution(FighterState state);

    protected abstract double calculateEquipmentContribution(); // to jest na bron miejsce

    @Override
    public final int calculateDamage(ArenaAttackPlayerAttributes attacker, FighterState state) {
        double attributeValue = calculatePrimaryAttributeContribution(attacker);
        double staminaValue = calculateStaminaContribution(state);
        double equipmentValue = calculateEquipmentContribution();

        double rawPower = attributeValue + staminaValue + equipmentValue;

        double finalDamage = rawPower * getAttackStyle().getDamageMultiplier();

        return (int) finalDamage;
    }

    @Override
    public final int attackAccuracyValue(ArenaAttackPlayerAttributes attacker, FighterState state) {
        double baseAccuracy = attacker.getAccuracy();
        // W przyszłości: + calculateEquipmentAccuracyContribution()

        return (int) (baseAccuracy * getAttackStyle().getAccuracyMultiplier());
    }

    @Override
    public void applySpecialEffects(FighterState state) {
        // Po ciosie zawsze ubywa staminy z paska
        state.consumeStamina(getBaseStaminaCost());
    }

    @Override
    public int getCrowdAppeal() {
        // Po ciosie zmienia sie nastawienie publicznosci
        return getAttackStyle().getBaseCrowdAppeal();
    }

    /**
     * Publiczny getter dla kosztu staminiy - używany w UI i AI.
     */
    public int getStaminaCost() {
        return getBaseStaminaCost();
    }
}
