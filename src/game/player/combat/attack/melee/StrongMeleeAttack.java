package game.player.combat.attack.melee;

import game.player.combat.attack.AttackStyle;

public class StrongMeleeAttack extends AbstractMeleeAttack {

    @Override
    public String getAttackName() {
        return "Potężny Prawy Hak";
    }

    @Override
    public String getActionDescription() {
        return "Miażdżący, niezwykle ryzykowny atak. Pożera mnóstwo kondycji, ale jeśli trafi, wywołuje euforię na trybunach.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        return AttackStyle.STRONG;
    }

    @Override
    protected int getBaseStaminaCost() {
        return 40;
    }
}