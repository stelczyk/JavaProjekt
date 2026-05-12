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
    private CharacterPath path;

    public Player(PlayerProfile profile) {
        this.profile = profile;
        this.attributes = new PlayerAttribute();
        this.inventory = new PlayerInventory();
        this.level = GameConstants.DEFAULT_STARTING_LEVEL;
        this.xp = GameConstants.DEFAULT_XP_LEVEL;
        this.money = GameConstants.DEFAULT_STARTING_MONEY;
        this.statPointsAvailable = GameConstants.DEFAULT_STARTING_STAT_POINTS;
        this.path = CharacterPath.WOJOWNIK; // domyślna ścieżka, zmieniana w CharacterCreationMenu
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

    /**
     * [KACPER] Ścieżka postaci wybrana przez gracza w CharacterCreationMenu.
     * Używaj w ArenaCombatEngine do bonusów i specjalnych ataków:
     *
     *   if (attacker.getPath() == CharacterPath.WOJOWNIK)  → +15% obrażenia wręcz
     *   if (attacker.getPath() == CharacterPath.LIDER)     → odblokuj LeaderScream
     *   if (attacker.getPath() == CharacterPath.CWANIAK)   → +10% szansa na unik
     */
    public CharacterPath getPath() {
        return path;
    }

    public void setPath(CharacterPath path) {
        this.path = path;
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

    /**
     * Ustawia level bezpośrednio — używane przy generowaniu Rywala.
     * Zastępuje refleksję w RivalGenerator.reflectiveSetLevel().
     *
     * [KACPER] W RivalGenerator.java zamień całą metodę reflectiveSetLevel()
     * i jej wywołanie na jedną linię:
     *
     *   rival.initLevel(rivalLevel);
     */
    public void initLevel(int level) {
        if (level >= 1 && level <= GameConstants.MAX_PLAYER_LEVEL) {
            this.level = level;
        }
    }
}