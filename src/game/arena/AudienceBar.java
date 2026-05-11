package game.arena;

import game.constants.GameConstants;

/**
 * AudienceBar - Pasek widowni determinujący zarobki na Arenie.
 *
 * MECHANIAKA:
 * - Przechowuje pomiędzy walkami
 * - Rośnie od spektakularnych ataków
 * - Spada drastycznie po przegranych
 * - Mnożnik zarobków: money *= (audienceBar / MAX_AUDIENCE)
 *
 * CONTRIBUTORS:
 * - Connection attribute (more fans)
 * - Valor attribute (epic moments)
 * - Attack Types (big attacks = more appeal)
 */
public class AudienceBar {

    private static final int MIN_AUDIENCE = 0;
    private static final int MAX_AUDIENCE = 100; // 100% satysfakcji
    private static final int DEFAULT_AUDIENCE = 50; // Startowe 50% satysfakcji

    private int currentAudience;

    /**
     * Konstruktor - inicjalizuje pasek na poziomie domyślnym.
     */
    public AudienceBar() {
        this.currentAudience = DEFAULT_AUDIENCE;
    }

    /**
     * Konstruktor z custom wartością (dla testów).
     */
    public AudienceBar(int initialValue) {
        this.currentAudience = Math.max(MIN_AUDIENCE, Math.min(MAX_AUDIENCE, initialValue));
    }

    // ==================== MECHANICS ====================

    /**
     * Zwiększa pasek po spektakularnym ataku.
     *
     * FORMULA:
     * appeal = baseAppeal (z AttackStyle)
     * appeal *= (1.0 + connection * 0.01) [Connection boost]
     * appeal *= (1.0 + valor * 0.02) [Valor multiplier]
     *
     * @param baseAppeal
     boost z ataku (Quick=5, Normal=10, Strong=25)
     * @param connectionAttribute Connections gracza
     * @param valorAttribute Valor gracza
     */
    public void addAudienceFromAttack(int baseAppeal, int connectionAttribute, int valorAttribute) {
        double boost = baseAppeal;

        // Connection: każdy punkt to 1% więcej
        boost *= (1.0 + (connectionAttribute - GameConstants.DEFAULT_START_ATTRIBUTE_VALUE) * 0.01);

        // Valor: każdy punkt to 2% więcej
        boost *= (1.0 + (valorAttribute - GameConstants.DEFAULT_START_ATTRIBUTE_VALUE) * 0.02);

        addAudience((int) boost);
    }

    /**
     * Dodaje publiczność (zwycięstwo).
     *
     * @param amount ilość do dodania
     */
    public void addAudience(int amount) {
        currentAudience = Math.min(MAX_AUDIENCE, currentAudience + amount);
    }

    /**
     * Drastycznie zmniejsza publiczność (porażka).
     *
     * KARA:
     * - Porażka w sparingu: audience -= 20 (drastyczna kara)
     * - Porażka w turnieju: audience -= 35 (bardzo drastyczna)
     *
     * @param amount ilość do odjęcia
     */
    public void removeAudienceOnLoss(int amount) {
        currentAudience = Math.max(MIN_AUDIENCE, currentAudience - amount);
    }

    /**
     * Mnożnik zarobków na podstawie widowni.
     *
     * FORMULA:
     * multiplier = currentAudience / MAX_AUDIENCE
     * monetaryReward *= multiplier
     *
     * @return mnożnik [0.0, 1.0]
     */
    public double getMoneyMultiplier() {
        return (double) currentAudience / MAX_AUDIENCE;
    }

    // ==================== GETTERS ====================

    public int getCurrentAudience() {
        return currentAudience;
    }

    public int getMaxAudience() {
        return MAX_AUDIENCE;
    }

    public double getAudiencePercentage() {
        return (double) currentAudience / MAX_AUDIENCE * 100.0;
    }

    // ==================== DISPLAY ====================

    /**
     * Wizualizuje pasek widowni.
     *
     * [████████████----] 60%
     */
    public String getBarVisualization() {
        int filledBlocks = (int) ((double) currentAudience / MAX_AUDIENCE * 20);
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < 20; i++) {
            sb.append(i < filledBlocks ? "█" : "░");
        }

        sb.append("] ").append(String.format("%.0f", getAudiencePercentage())).append("%");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Audience: %d/%d %s",
            currentAudience, MAX_AUDIENCE, getBarVisualization());
    }
}

