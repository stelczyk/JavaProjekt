package game.arena;

import game.player.Player;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.attributes.types.ArenaAudiencePlayerAttributes;
import game.player.attributes.types.ArenaDefencePlayerAttributes;
import game.player.attributes.types.ArenaMovementPlayerAttributes;

/**
 * Reprezentuje gracza lub przeciwnika W KONTEKŚCIE WALKI NA ARENIE.
 *
 * FILOZOFIA DESIGNU - WRAPPER PATTERN:
 * ArenaFighter jest "adapterem" między globalnym stanem gracza (Player)
 * a systemem walki (ArenaCombatEngine). Enkapsuluje:
 * - Referencję do globalnego gracza (Player)
 * - Tymczasowy stan walki (ArenaFighterState)
 * - Delegację atrybutów z Player do interfejsów Arena*
 *
 * SEPARACJA ODPOWIEDZIALNOŚCI:
 * - Player = globalny stan (money, xp, level, BAZOWE atrybuty)
 * - ArenaFighter = reprezentacja W WALCE (wrappuje Player + dodaje combat state)
 * - ArenaFighterState = czysty stan tej konkretnej walki (HP, stamina)
 *
 * DLACZEGO NIE DZIEDZICZYMY PO PLAYER:
 * - Dziedziczenie = "is-a", ale ArenaFighter NIE JEST Player
 * - ArenaFighter to KONTEKST walki, nie nowy typ gracza
 * - Kompozycja (has-a) jest bardziej elastyczna niż dziedziczenie
 *
 * UŻYCIE:
 * Player player = new Player(profile);
 * ArenaFighter fighter = new ArenaFighter(player);
 * // Walka...
 * // Po walce fighter jest niszczony, player zachowuje XP i money
 *
 * OPPONENT:
 * Ta sama klasa służy dla gracza i przeciwnika. Przeciwnik to po prostu:
 * Player opponent = createOpponent();
 * ArenaFighter opponentFighter = new ArenaFighter(opponent);
 */
public class ArenaFighter implements
        ArenaAttackPlayerAttributes,
        ArenaDefencePlayerAttributes,
        ArenaMovementPlayerAttributes,
        ArenaAudiencePlayerAttributes {

    // Referencja do globalnego gracza - NIE kopiujemy, tylko wskazujemy
    private final Player player;

    // Stan walki - tymczasowy, niszczony po walce
    private final ArenaFighterState state;

    /**
     * Tworzy reprezentację gracza w walce.
     *
     * @param player globalny obiekt gracza (zawiera atrybuty, inventory, money, xp)
     */
    public ArenaFighter(Player player) {
        this.player = player;

        // Inicjalizujemy stan walki na podstawie atrybutów gracza
        int maxHp = player.getMaxHp();
        int maxStamina = player.getAttributes().getStamina();
        int startingArmor = player.getInventory().getTotalDefenseBonus();

        this.state = new ArenaFighterState(maxHp, maxStamina, startingArmor);
    }

    // ==================== Delegacja do Player ====================

    /**
     * Zwraca referencję do globalnego gracza.
     * Używane przez system walki do przyznania XP/money po walce.
     */
    public Player getPlayer() {
        return player;
    }

    // ==================== Delegacja do ArenaFighterState ====================

    /**
     * Zwraca stan walki (HP, stamina, crowd, temporary effects).
     * System walki używa tego do modyfikowania stanu podczas walki.
     */
    public ArenaFighterState getState() {
        return state;
    }

    // ==================== Implementacja ArenaAttackPlayerAttributes ====================
    // Delegujemy do atrybutów gracza - to są BAZOWE wartości, nie zmieniane podczas walki

    @Override
    public int getStrength() {
        return player.getAttributes().getStrength();
    }

    @Override
    public int getAccuracy() {
        return player.getAttributes().getAccuracy();
    }

    @Override
    public int getCunning() {
        return player.getAttributes().getCunning();
    }

    @Override
    public int getValor() {
        return player.getAttributes().getValor();
    }

    @Override
    public int getBrave() {
        return player.getAttributes().getBrave();
    }

    @Override
    public int getConnections() {
        return player.getAttributes().getConnections();
    }

    // ==================== Implementacja ArenaDefencePlayerAttributes ====================

    @Override
    public int getDefense() {
        return player.getAttributes().getDefense();
    }

    @Override
    public int getSpeed() {
        return player.getAttributes().getSpeed();
    }

    // ==================== Implementacja ArenaMovementPlayerAttributes ====================
    // Speed już zaimplementowane wyżej

    // ==================== Implementacja ArenaAudiencePlayerAttributes ====================
    // Valor i Connections już zaimplementowane wyżej

    // ==================== Utility Methods ====================

    /**
     * Resetuje stan walki dla nowej walki z tym samym graczem.
     * Używane gdy gracz walczy kilka razy z rzędu (tournament mode).
     */
    public void resetForNewFight() {
        int startingArmor = player.getInventory().getTotalDefenseBonus();
        state.resetForNewFight(startingArmor);
    }

    /**
     * Sprawdza czy fighter jest jeszcze w stanie walczyć.
     */
    public boolean isAlive() {
        return state.isAlive();
    }

    /**
     * Sprawdza czy fighter został znokautowany.
     */
    public boolean isKnockedOut() {
        return state.isKnockedOut();
    }

    /**
     * Zwraca nazwę fightera (nick gracza).
     */
    public String getName() {
        return player.getProfile().getPlayerNickname();
    }

    /**
     * Wyświetla stan fightera (debug/UI).
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", getName(), state.toString());
    }

    // ==================== Metody pomocnicze dla systemu walki ====================

    /**
     * Otrzymuje obrażenia w walce.
     * System walki wywołuje to po obliczeniu finalnych obrażeń.
     */
    public void receiveDamage(int damage) {
        state.reduceHp(damage);
    }

    /**
     * Konsumuje staminę po ataku.
     */
    public void consumeStamina(int amount) {
        state.consumeStamina(amount);
    }

    /**
     * Regeneruje staminę między turami.
     */
    public void regenerateStamina(int amount) {
        state.regenerateStamina(amount);
    }

    /**
     * Dodaje reakcję tłumu po akcji.
     */
    public void addCrowdReaction(int crowdAppeal) {
        state.addCrowdSatisfaction(crowdAppeal);
    }

    /**
     * Ogłuszenie - fighter traci następną turę.
     */
    public void stun() {
        state.setStunned(true);
    }

    /**
     * Sprawdza czy fighter jest ogłuszony i automatycznie zdejmuje status.
     * System walki wywołuje to na początku tury.
     *
     * @return true jeśli fighter był ogłuszony (i traci turę)
     */
    public boolean checkAndClearStun() {
        if (state.isStunned()) {
            state.setStunned(false);
            return true; // Fighter był ogłuszony, traci turę
        }
        return false; // Fighter NIE był ogłuszony, może działać
    }

    // ==================== Post-Fight Rewards ====================

    /**
     * Przyznaje nagrody po wygranej walce.
     * Modyfikuje GLOBALNY stan gracza (Player), nie ArenaFighter.
     *
     * @param xpReward ilość XP do przyznania
     * @param moneyReward ilość pieniędzy (modyfikowana przez crowd satisfaction)
     */
    public void grantVictoryRewards(int xpReward, int moneyReward) {
        player.gainXp(xpReward);

        // Crowd satisfaction wpływa na zarobki (każde 10 punktów = +10% zarobków)
        double crowdMultiplier = 1.0 + (state.getCrowdSatisfaction() / 100.0);
        int finalMoney = (int) (moneyReward * crowdMultiplier);

        player.earnMoney(finalMoney);
    }
}
