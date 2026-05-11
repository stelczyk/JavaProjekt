package game.arena;

/**
 * CombatMode - Enum dla trybu walki na Arenie.
 *
 * Każdy tryb ma inne zasady zwycięstwa i mechaniki.
 */
public enum CombatMode {

    /**
     * SPARRING - Tryb treningowy.
     *
     * Zasady:
     * - Walka do "pierwszej krwi" (pierwszych obrażeń HP)
     * - Pancerz ignorowany (jeśli trafia, od razu idzie do HP)
     * - Instant Critical: cios, który całkowicie ignoruje pancerz
     * - Szansa na Critical = Valor difference
     * - Zwycięstwo na HP damage, nie na śmierci
     */
    SPARRING("Sparring - Tryb treningowy", true),

    /**
     * TOURNAMENT - Tryb rywalizacyjny.
     *
     * Zasady:
     * - Walka na śmierć (HP musi spaść poniżej 0)
     * - Mgła Wojny: Gracz nie widzi HP/Armor Rywala
     * - Pancerz CHRONI: obrażenia najpierw trafią pancerz
     * - Dopiero po zniszczeniu pancerza (Armor=0): HP damage
     * - One-Shot Critical: instant kończy walkę JEŚLI damage > HP+Armor
     * - Zwycięstwo: jeden z walczących ma HP <= 0
     */
    TOURNAMENT("Tournament - Tryb rywalizacyjny", false);

    private final String description;
    private final boolean isTraining; // true = sparring, false = tournament

    CombatMode(String description, boolean isTraining) {
        this.description = description;
        this.isTraining = isTraining;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTraining() {
        return isTraining;
    }

    public boolean isTournament() {
        return !isTraining;
    }
}

