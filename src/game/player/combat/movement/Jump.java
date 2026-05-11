package game.player.combat.movement;

/**
 * JUMP - Agresywny, dalekosiężny ruch.
 *
 * CHARAKTERYSTYKA:
 * - Koszt staminy: WYSOKI (30 staminy)
 * - Dystans: Równy Speed * 0.5 (jak każdy ruch, ale więcej staminy zużywa)
 * - Zastosowanie: Szybkie zbliżanie, dynamiczne pozycjonowanie
 * - Best for: Krótkie walki, aggressive gameplay, melee combat
 *
 * NOTA: Jump używa WIĘCEJ staminy niż Step, ale dystans jest ten sam!
 * Stąd: Jump lepszy do agresji, Step lepszy do efektywności.
 */
public class Jump extends Move {

    private static final double STAMINA_COST = 30.0;

    /**
     * Constructor dla Jump w kierunku RIGHT (zbliżenie).
     */
    public Jump() {
        this(MovementDirection.RIGHT);
    }

    /**
     * Constructor z custom kierunkiem.
     *
     * @param direction kierunek (LEFT = cofa się, RIGHT = zbliża się)
     */
    public Jump(MovementDirection direction) {
        super("Skok", STAMINA_COST, direction);
    }

    @Override
    public String getMovementName() {
        return "Skok [" + direction.name() + "]";
    }
}

