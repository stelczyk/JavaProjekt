package game.player.combat.attack;

import game.arena.ArenaFighterState;
import game.player.attributes.arena.ArenaAttackPlayer;
import game.player.attributes.types.ArenaAttackPlayerAttributes;

public abstract class AbstractAttack implements ArenaAttackPlayer {

    protected abstract AttackStyle getAttackStyle();
    protected abstract int getBaseStaminaCost();

    protected abstract double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker);
    protected abstract double calculateStaminaContribution(ArenaFighterState state);

    protected abstract double calculateEquipmentContribution(); // to jest na bron miejsce

    @Override
    public final int calculateDamage(ArenaAttackPlayerAttributes attacker, ArenaFighterState state) {
        double attributeValue = calculatePrimaryAttributeContribution(attacker);
        double staminaValue = calculateStaminaContribution(state);
        double equipmentValue = calculateEquipmentContribution();

        double rawPower = attributeValue + staminaValue + equipmentValue;

        double finalDamage = rawPower * getAttackStyle().getDamageMultiplier();

        return (int) finalDamage;
    }

    @Override
    public final int attackAccuracyValue(ArenaAttackPlayerAttributes attacker, ArenaFighterState state) {
        double baseAccuracy = attacker.getAccuracy();
        // W przyszłości: + calculateEquipmentAccuracyContribution()

        return (int) (baseAccuracy * getAttackStyle().getAccuracyMultiplier());
    }

    @Override
    public void applySpecialEffects(ArenaFighterState state) {
        // Po ciosie zawsze ubywa staminy z paska
        state.consumeStamina(getBaseStaminaCost());
    }

    @Override
    public int getCrowdAppeal() {
        // Po ciosie zmienia sie nastawienie publicznosci
        return getAttackStyle().getBaseCrowdAppeal();
    }
}
