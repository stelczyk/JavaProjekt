package game.player.combat.attack.melee;

import game.player.combat.attack.AttackStyle;

public class MediumMeleeAttack extends AbstractMeleeAttack {

    @Override
    public String getAttackName() {
        return "Klasyczny Prawy Sierpowy";
    }

    @Override
    public String getActionDescription() {
        return "Standardowy cios, optymalnie balansujący zadaną siłę i zmęczenie własnego organizmu.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        return AttackStyle.NORMAL;
    }

    @Override
    protected int getBaseStaminaCost() {
        return 20; // Klasyczny, bazowy koszt kondycji
    }
}