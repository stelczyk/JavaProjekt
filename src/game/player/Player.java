package game.player;

import game.constants.GameConstants;


//globalny stan gracza
//nie implementuje interfejsow areny
public class Player {

    private final PlayerProfile profile;
    private final PlayerAttribute attributes;
    private final PlayerInventory inventory;
    private int level;
    private int xp;
    private int money;
    private int statPointsAvailable;

    public Player(PlayerProfile profile) {
        this.profile = profile;
        this.attributes = new PlayerAttribute();
        this.inventory = new PlayerInventory();
        this.level = GameConstants.DEFAULT_STARTING_LEVEL;
        this.xp = GameConstants.DEFAULT_XP_LEVEL;
        this.money = GameConstants.DEFAULT_STARTING_MONEY;
        this.statPointsAvailable = GameConstants.DEFAULT_STARTING_STAT_POINTS;
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

    // Używane przez ArenaFighter do inicjalizacji HP w walce
    public int getMaxHp() {
        return attributes.getStamina() * 10;
    }

    public int getStatPointsAvailable() {
        return statPointsAvailable;
    }

    public boolean spendMoney(int amount) {
        if (amount > money) return false;
        money -= amount;
        return true;
    }

    public void earnMoney(int amount) {
        money += amount;
    }

    // Wywoływane przez ShopMenu po każdym rozdzieleniu punktu
    public void spendStatPoint() {
        if (statPointsAvailable > 0) statPointsAvailable--;
    }

    public void gainXp(int amount) {
        xp = Math.min(xp + amount, GameConstants.MAX_XP_LIMIT);
        checkLevelUp();
    }

    //sprawdza czy gracz awansowal
    //prog level * 150 xp, za kazdy level moze rozdac 4 pkt statystyk
    private void checkLevelUp() {
        while (level < GameConstants.MAX_PLAYER_LEVEL && xp >= xpRequiredForNextLevel()) {
            level++;
            statPointsAvailable += GameConstants.DEFAULT_STAT_POINTS_TO_DISTRIBUTE;
            System.out.println("AWANS! Poziom: " + level + " | Punkty do rozdania: " + statPointsAvailable);
        }
    }

    private int xpRequiredForNextLevel() {
        return level * 150;
    }
}