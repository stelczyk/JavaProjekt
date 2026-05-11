package game.player.combat.attack.melee;

import game.player.Player;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.state.FighterState;
import game.items.Weapon;

public abstract class AbstractMeleeAttack extends AbstractAttack {

    private static final double STRENGTH_WEIGHT = 1.0; // Siła oddaje 100% swojej wartości
    private static final double STAMINA_WEIGHT = 0.4;  // Stamina oddaje 40% wartości do siły ciosu
    private static final double WEAPON_WEIGHT = 1.0;   // Broń oddaje 100% swojej siły

    @Override
    protected double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker) {
        // Dla Melee głównym atrybutem jest SIŁA
        return attacker.getStrength() * STRENGTH_WEIGHT;
    }

    @Override
    protected double calculateStaminaContribution(FighterState state) {
        return state.getCurrentStamina() * STAMINA_WEIGHT;
    }

    @Override
    protected double calculateEquipmentContribution() {
        return 0.0; // Dla old API - nie mamy dostępu do broni tutaj
    }

    /**
     * NOWA METODA: Oblicza obrażenia z uwzględnieniem ekwipunku.
     *
     * FORMULA:
     * damage = (strength * 1.0 + stamina * 0.4 + weaponBonus * 1.0) * styleMultiplier
     *
     * @param player gracz (ma dostęp do inventory)
     * @param state stan walki
     * @return finalne obrażenia z ekwipunkiem
     */
    public int calculateDamageWithEquipment(Player player, FighterState state) {
        double attributeValue = calculatePrimaryAttributeContribution(player.getAttributes());
        double staminaValue = calculateStaminaContribution(state);

        // INTEGRACJA EKWIPUNKU
        double weaponValue = 0.0;
        Weapon equippedWeapon = player.getInventory().getEquippedWeapon();

        if (equippedWeapon != null) {
            weaponValue = equippedWeapon.getStrengthBonus() * WEAPON_WEIGHT;
        }

        double rawPower = attributeValue + staminaValue + weaponValue;
        double finalDamage = rawPower * getAttackStyle().getDamageMultiplier();

        return (int) finalDamage;
    }
}