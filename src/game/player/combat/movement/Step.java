package game.player.combat.movement;

/**
 * STEP - Krótki, efektywny energetycznie ruch.
 *
 * CHARAKTERYSTYKA:
 * - Koszt staminy: NISKI (10 staminy)
 * - Dystans: Równy Speed * 0.5 (jak każdy ruch)
 * - Zastosowanie: Ostrożne zbliżanie, pozycjonowanie, kiting
 * - Best for: Długie walki, conserving stamina, tactical movement
 */
public class Step extends Move {

    private static final double STAMINA_COST = 10.0;

    /**
     * Constructor dla Step w kierunku LEFT.
     */
    public Step() {
        this(MovementDirection.LEFT);
    }

    /**
     * Constructor z custom kierunkiem.
     *
     * @param direction kierunek (LEFT = cofa się, RIGHT = zbliża się)
     */
    public Step(MovementDirection direction) {
        super("Krok", STAMINA_COST, direction);
    }

    @Override
    public String getMovementName() {
        return "Krok [" + direction.name() + "]";
    }
}

