package game.player.combat.attack.melee;

import game.player.combat.attack.AttackStyle;

public class QuickMeleeAttack extends AbstractMeleeAttack {

    @Override
    public String getAttackName() {
        return "Szybki Lewy Prosty";
    }

    @Override
    public String getActionDescription() {
        return "Błyskawiczny, badający cios. Świetny do nękania przeciwnika przy minimalnym zużyciu energii.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        return AttackStyle.QUICK;
    }

    @Override
    protected int getBaseStaminaCost() {
        return 10;
    }
}
