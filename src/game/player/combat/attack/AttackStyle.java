package game.player.combat.attack;

public enum AttackStyle {
    QUICK(0.6, 1.5, 5),
    NORMAL(1.0, 1.0, 10),
    STRONG(1.5, 0.5, 25);

    private final double damageMultiplier;
    private final double accuracyMultiplier;
    private final int baseCrowdAppeal;

    AttackStyle(double damageMultiplier, double accuracyMultiplier, int baseCrowdAppeal) {
        this.damageMultiplier = damageMultiplier;
        this.accuracyMultiplier = accuracyMultiplier;
        this.baseCrowdAppeal = baseCrowdAppeal;
    }

    public double getDamageMultiplier() { return damageMultiplier; }
    public double getAccuracyMultiplier() { return accuracyMultiplier; }
    public int getBaseCrowdAppeal() { return baseCrowdAppeal; }
}
