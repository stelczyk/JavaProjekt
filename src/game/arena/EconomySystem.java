package game.arena;

/**
 * EconomySystem - System zarabiania pieniędzy na Arenie.
 *
 * ZAROBKI:
 * - Wygrana: baseReward * audienceMultiplier
 * - Porażka: -penalty (drastyczna kara)
 */
public class EconomySystem {
    
    // ==================== REWARDS ====================
    
    private static final int BASE_SPARRING_REWARD = 100;
    private static final int BASE_TOURNAMENT_REWARD = 500;
    
    private static final int SPARRING_LOSS_PENALTY = 50;
    private static final int TOURNAMENT_LOSS_PENALTY = 200;
    
    /**
     * Oblicza nagrodę za zwycięstwo w sparingu.
     *
     * @param audienceMultiplier (0.0 - 1.0)
     * @return kwota pieniędzy
     */
    public static int calculateSparringReward(double audienceMultiplier) {
        return (int) (BASE_SPARRING_REWARD * audienceMultiplier);
    }
    
    /**
     * Oblicza nagrodę za zwycięstwo w turnieju.
     *
     * @param audienceMultiplier (0.0 - 1.0)
     * @return kwota pieniędzy
     */
    public static int calculateTournamentReward(double audienceMultiplier) {
        return (int) (BASE_TOURNAMENT_REWARD * audienceMultiplier);
    }
    
    /**
     * Oblicza karę za przegraną w sparingu.
     *
     * @return kwota straty
     */
    public static int calculateSparringLossPenalty() {
        return SPARRING_LOSS_PENALTY;
    }
    
    /**
     * Oblicza karę za przegraną w turnieju.
     *
     * @return kwota straty
     */
    public static int calculateTournamentLossPenalty() {
        return TOURNAMENT_LOSS_PENALTY;
    }
}

