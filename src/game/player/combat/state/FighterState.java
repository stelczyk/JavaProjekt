package game.player.combat.state;

public interface FighterState {
    int getCurrentHp();
    void reduceHp(int amount);
    void increaseHp(int amount);

    int getCurrentStamina();
    void consumeStamina(int amount);
    void increaseStamina(int amount);

    int getCurrentArmor();
    void reduceArmor(int amount);

    int getCrowdSatisfaction();
    void addCrowdSatisfaction(int amount);
    void reduceCrowdSatisfaction(int amount);

}