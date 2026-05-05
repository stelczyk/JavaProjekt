package game.player;

import game.constants.GameConstants;
import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Weapon;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.attributes.types.ArenaAudiencePlayerAttributes;
import game.player.attributes.types.ArenaDefencePlayerAttributes;
import game.player.attributes.types.ArenaMovementPlayerAttributes;
import game.player.combat.state.FighterState;


// Player implementuje wszystkie interfejsy zeby go przekazac do walki
public class Player implements FighterState,
        ArenaAttackPlayerAttributes,
        ArenaDefencePlayerAttributes,
        ArenaAudiencePlayerAttributes,
        ArenaMovementPlayerAttributes {

    private final PlayerProfile profile;
    private final PlayerAttribute attributes;
    private final PlayerInventory inventory;
    private int level;
    private int xp;
    private int money;
    private int currentHp;
    private int currentStamina;
    private int crowdSatisfaction;

    public Player(PlayerProfile profile) {
        this.profile = profile;
        this.attributes = new PlayerAttribute();
        this.inventory = new PlayerInventory();
        this.level = GameConstants.DEFAULT_STARTING_LEVEL;
        this.xp = GameConstants.DEFAULT_XP_LEVEL;
        this.money = GameConstants.DEFAULT_STARTING_MONEY;
        this.currentHp = getMaxHp();
        this.currentStamina = attributes.getStamina();
        this.crowdSatisfaction = 0;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public PlayerAttribute getAttributes() {
        return attributes;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getMoney() {
        return money;
    }

    public int getMaxHp() {
        return attributes.getStamina() * 10;
    }

    public boolean spendMoney(int amount) {
        if (amount > money) return false;
        money -= amount;
        return true;
    }

    public void earnMoney(int amount) {
        money += amount;
    }

    public void gainXp(int amount) {
        xp = Math.min(xp + amount, GameConstants.MAX_XP_LIMIT);
    }

    @Override
    public int getStrength() {
        return attributes.getStrength();
    }

    @Override
    public int getAccuracy() {
        return attributes.getAccuracy();
    }

    @Override
    public int getDefense() {
        return attributes.getDefense();
    }

    @Override
    public int getSpeed() {
        return attributes.getSpeed();
    }

    @Override
    public int getConnections() {
        return attributes.getConnections();
    }

    @Override
    public int getValor() {
        return attributes.getValor();
    }

    @Override
    public int getCurrentHp() {
        return currentHp;
    }

    @Override
    public void reduceHp(int amount) {
        currentHp = Math.max(currentHp - amount, 0);
    }

    @Override
    public void increaseHp(int amount) {
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    @Override
    public int getCurrentStamina() {
        return currentStamina;
    }

    @Override
    public void consumeStamina(int amount) {
        currentStamina = Math.max(currentStamina - amount, 0);
    }

    @Override
    public void increaseStamina(int amount) {
        currentStamina = Math.min(attributes.getStamina(), currentStamina + amount);
    }

    @Override
    public int getCurrentArmor() {
        return inventory.getTotalDefenseBonus();
    }

    @Override
    public void reduceArmor(int amount) {
    }

    @Override
    public int getCrowdSatisfaction() {
        return crowdSatisfaction;
    }

    @Override
    public void addCrowdSatisfaction(int amount) {
        crowdSatisfaction += amount;
    }

    @Override
    public void reduceCrowdSatisfaction(int amount) {
        crowdSatisfaction = Math.max(0, crowdSatisfaction - amount);
    }
}
