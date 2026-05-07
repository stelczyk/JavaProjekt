package game.arena;

import game.player.combat.state.FighterState;

/**
 * Reprezentuje TYMCZASOWY stan gracza/przeciwnika PODCZAS KONKRETNEJ WALKI.
 *
 * FILOZOFIA DESIGNU:
 * To jest KLASA (nie interfejs), ponieważ reprezentuje konkretny stan walki.
 * Nie ma potrzeby polimorfizmu - każda walka ma dokładnie ten sam stan.
 *
 * SEPARACJA OD PLAYER:
 * - Player = permanentny stan gracza (level, money, BAZOWE atrybuty)
 * - ArenaFighterState = tymczasowy stan WALKI (HP, stamina w tej konkretnej walce)
 *
 * Po zakończeniu walki, ten obiekt jest niszczony. Player pozostaje z zaktualizowanym
 * XP i pieniędzmi, ale nie przechowuje stanu walki.
 *
 * DLACZEGO NIE INTERFEJS:
 * - Interfejsy są dla polimorfizmu (wiele implementacji)
 * - Tutaj jest tylko JEDNA implementacja stanu walki
 * - Klasa jest prostsza i bardziej czytelna
 *
 * ODPOWIEDZIALNOŚĆ:
 * - Przechowywanie zmiennych wartości podczas walki (HP, stamina, crowd)
 * - Zarządzanie temporary effects (ogłuszenie, zatrucie, itp.)
 * - Reset stanu między rundami
 */
public class ArenaFighterState implements FighterState {

    // Stan zdrowia w walce (nie permanentny!)
    private int currentHp;
    private final int maxHp;

    // Stamina w walce (regeneruje się między rundami)
    private int currentStamina;
    private final int maxStamina;

    // Armor może być degradowany podczas walki (tarcza się niszczy)
    private int currentArmor;

    // Reakcja tłumu na tę konkretną walkę
    private int crowdSatisfaction;

    // Temporary effects - efekty tymczasowe w walce
    private boolean isStunned;       // Ogłuszenie (LeaderScream) - traci turę
    private boolean isPoisoned;      // Zatrucie - damage over time
    private int temporaryDefenseBonus; // Buff defensywny z special move

    /**
     * Konstruktor tworzy stan walki na podstawie maksymalnych wartości z atrybutów gracza.
     *
     * @param maxHp maksymalne HP (z atrybutu Stamina gracza)
     * @param maxStamina maksymalna stamina (z atrybutu Stamina gracza)
     * @param startingArmor początkowy armor (z ekwipunku gracza)
     */
    public ArenaFighterState(int maxHp, int maxStamina, int startingArmor) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.maxStamina = maxStamina;
        this.currentStamina = maxStamina;
        this.currentArmor = startingArmor;
        this.crowdSatisfaction = 0;
        this.isStunned = false;
        this.isPoisoned = false;
        this.temporaryDefenseBonus = 0;
    }

    // ==================== HP Management ====================

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void reduceHp(int amount) {
        currentHp = Math.max(currentHp - amount, 0);
    }

    public void increaseHp(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public boolean isKnockedOut() {
        return currentHp <= 0;
    }

    // ==================== Stamina Management ====================

    public int getCurrentStamina() {
        return currentStamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void consumeStamina(int amount) {
        currentStamina = Math.max(currentStamina - amount, 0);
    }

    public void increaseStamina(int amount) {
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    /**
     * Regeneracja staminy między rundami.
     * Używane przez system walki na początku każdej tury.
     *
     * @param amount ilość staminy do zregenerowania
     */
    public void regenerateStamina(int amount) {
        increaseStamina(amount);
    }

    // ==================== Armor Management ====================

    public int getCurrentArmor() {
        return currentArmor + temporaryDefenseBonus;
    }

    /**
     * Redukuje armor (tarcza/zbroja się niszczy podczas walki).
     * W przeciwieństwie do HP, armor może spaść do 0 i nie regeneruje się automatycznie.
     */
    public void reduceArmor(int amount) {
        currentArmor = Math.max(currentArmor - amount, 0);
    }

    // ==================== Crowd Satisfaction ====================

    public int getCrowdSatisfaction() {
        return crowdSatisfaction;
    }

    public void addCrowdSatisfaction(int amount) {
        crowdSatisfaction += amount;
    }

    public void reduceCrowdSatisfaction(int amount) {
        crowdSatisfaction = Math.max(0, crowdSatisfaction - amount);
    }

    // ==================== Temporary Effects ====================

    /**
     * Status ogłuszenia (stun) - gracz traci następną turę.
     * Używane przez LeaderScream.
     */
    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
    }

    /**
     * Status zatrucia - gracz otrzymuje damage over time.
     * Przygotowane na przyszłe special attacks.
     */
    public boolean isPoisoned() {
        return isPoisoned;
    }

    public void setPoisoned(boolean poisoned) {
        isPoisoned = poisoned;
    }

    /**
     * Tymczasowy bonus do defensywy z special moves.
     * Przygotowane na przyszłe buffs.
     */
    public int getTemporaryDefenseBonus() {
        return temporaryDefenseBonus;
    }

    public void addTemporaryDefenseBonus(int bonus) {
        temporaryDefenseBonus += bonus;
    }

    public void clearTemporaryDefenseBonus() {
        temporaryDefenseBonus = 0;
    }

    // ==================== Reset & Utility ====================

    /**
     * Resetuje stan walki do wartości początkowych.
     * Używane przy rozpoczęciu nowej walki z tym samym graczem.
     */
    public void resetForNewFight(int newStartingArmor) {
        this.currentHp = maxHp;
        this.currentStamina = maxStamina;
        this.currentArmor = newStartingArmor;
        this.crowdSatisfaction = 0;
        this.isStunned = false;
        this.isPoisoned = false;
        this.temporaryDefenseBonus = 0;
    }

    /**
     * Zwraca procent pozostałego HP (do wyświetlania graczowi).
     */
    public double getHpPercentage() {
        return (double) currentHp / maxHp * 100.0;
    }

    /**
     * Zwraca procent pozostałej staminy (do wyświetlania graczowi).
     */
    public double getStaminaPercentage() {
        return (double) currentStamina / maxStamina * 100.0;
    }

    @Override
    public String toString() {
        return String.format("HP: %d/%d (%.1f%%), Stamina: %d/%d (%.1f%%), Armor: %d, Crowd: %d%s%s",
                currentHp, maxHp, getHpPercentage(),
                currentStamina, maxStamina, getStaminaPercentage(),
                getCurrentArmor(),
                crowdSatisfaction,
                isStunned ? " [STUNNED]" : "",
                isPoisoned ? " [POISONED]" : "");
    }
}
