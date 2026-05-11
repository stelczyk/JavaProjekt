package game.arena;

import game.player.combat.movement.MovementDirection;

/**
 * ArenaPosition - Zarządzanie pozycjami postaci na 1D osi Areny.
 *
 * ZASADY:
 * P - współrzędna Gracza
 * R - współrzędna Rywala
 * D - dystans = R - P
 *
 * Gold RULE: P < R (Gracz zawsze po lewej, Rywal po prawej)
 * RANGE: 0 <= P, R <= 50
 */
public class ArenaPosition {

    private int playerPosition;
    private int rivalPosition;

    /**
     * Konstruktor z pozycjami startowymi.
     */
    public ArenaPosition() {
        this.playerPosition = ArenaConstants.PLAYER_STARTING_POSITION;
        this.rivalPosition = ArenaConstants.RIVAL_STARTING_POSITION;
        validatePositions();
    }

    /**
     * Konstruktor z custom pozycjami (do testów).
     */
    public ArenaPosition(int playerPos, int rivalPos) {
        this.playerPosition = playerPos;
        this.rivalPosition = rivalPos;
        validatePositions();
    }

    // ==================== VALIDATION ====================

    /**
     * Weryfikuje czy pozycje spełniają GOLD RULE: P < R
     */
    private void validatePositions() {
        if (playerPosition >= rivalPosition) {
            throw new IllegalStateException(
                String.format("GOLD RULE VIOLATION: Player(%d) must be < Rival(%d)",
                    playerPosition, rivalPosition));
        }
        if (playerPosition < ArenaConstants.ARENA_MIN_COORDINATE ||
            playerPosition > ArenaConstants.ARENA_MAX_COORDINATE) {
            throw new IllegalStateException(
                String.format("Player position %d out of bounds [%d, %d]",
                    playerPosition, ArenaConstants.ARENA_MIN_COORDINATE, ArenaConstants.ARENA_MAX_COORDINATE));
        }
        if (rivalPosition < ArenaConstants.ARENA_MIN_COORDINATE ||
            rivalPosition > ArenaConstants.ARENA_MAX_COORDINATE) {
            throw new IllegalStateException(
                String.format("Rival position %d out of bounds [%d, %d]",
                    rivalPosition, ArenaConstants.ARENA_MIN_COORDINATE, ArenaConstants.ARENA_MAX_COORDINATE));
        }
    }

    // ==================== MOVEMENT ====================

    /**
     * Przesuwa pozycję Gracza.
     * Gracz może poruszać się:
     * - LEFT: w kierunku 0 (cofa się, kiting)
     * - RIGHT: w kierunku Rywala (zbliża się)
     *
     * @param direction kierunek ruchu
     * @param distance dystans do przesunięcia
     * @return czy ruch był możliwy
     */
    public boolean movePlayer(MovementDirection direction, double distance) {
        int newPosition = calculateNewPosition(playerPosition, direction, distance, true);

        if (newPosition < ArenaConstants.ARENA_MIN_COORDINATE ||
            newPosition > ArenaConstants.ARENA_MAX_COORDINATE) {
            return false; // Ruch poza granice
        }

        if (newPosition >= rivalPosition) {
            return false; // Narusza GOLD RULE
        }

        playerPosition = newPosition;
        return true;
    }

    /**
     * Przesuwa pozycję Rywala.
     * Rywal może poruszać się:
     * - RIGHT: w kierunku 50 (cofa się, kiting)
     * - LEFT: w kierunku Gracza (zbliża się)
     *
     * @param direction kierunek ruchu
     * @param distance dystans do przesunięcia
     * @return czy ruch był możliwy
     */
    public boolean moveRival(MovementDirection direction, double distance) {
        int newPosition = calculateNewPosition(rivalPosition, direction, distance, false);

        if (newPosition < ArenaConstants.ARENA_MIN_COORDINATE ||
            newPosition > ArenaConstants.ARENA_MAX_COORDINATE) {
            return false; // Ruch poza granice
        }

        if (newPosition <= playerPosition) {
            return false; // Narusza GOLD RULE
        }

        rivalPosition = newPosition;
        return true;
    }

    /**
     * Oblicza nową pozycję po ruchu.
     *
     * @param currentPosition obecna pozycja
     * @param direction kierunek
     * @param distance maksymalny dystans
     * @param isPlayer czy to gracz (inny system kierunków)
     * @return nowa pozycja
     */
    private int calculateNewPosition(int currentPosition, MovementDirection direction, double distance, boolean isPlayer) {
        int maxDistance = (int) Math.ceil(distance);

        if (isPlayer) {
            // Gracz: LEFT = w stronę 0, RIGHT = w stronę Rywala
            switch (direction) {
                case LEFT:
                    return currentPosition - maxDistance; // Cofa się
                case RIGHT:
                    return currentPosition + maxDistance; // Zbliża się
                case NONE:
                default:
                    return currentPosition;
            }
        } else {
            // Rywal: RIGHT = w stronę 50, LEFT = w stronę Gracza
            switch (direction) {
                case RIGHT:
                    return currentPosition + maxDistance; // Cofa się
                case LEFT:
                    return currentPosition - maxDistance; // Zbliża się
                case NONE:
                default:
                    return currentPosition;
            }
        }
    }

    // ==================== DISTANCE CALCULATION ====================

    /**
     * Oblicza aktualny dystans między postaciami.
     * D = R - P
     *
     * @return dystans (zawsze >= 0)
     */
    public int getDistance() {
        return rivalPosition - playerPosition;
    }

    /**
     * Sprawdza czy postaci są w zwarciu (melee).
     * Zwarcie, jeśli: D <= 0 (teoretycznie niemożliwe dzięki GOLD RULE)
     * Praktycznie: jeśli dystans jest bardzo mały
     *
     * @return czy w zwarciu
     */
    public boolean isInMelee() {
        return getDistance() <= 0; // Teoretically impossible, but safe check
    }

    /**
     * Sprawdza czy ruch w kierunku przeciwnika jest możliwy.
     * Możliwy, jeśli: D > Y (dystans > zasięg ruchu)
     *
     * @param movementRange maksymalny zasięg ruchu (Y)
     * @return czy można się zbliżyć
     */
    public boolean canApproach(double movementRange) {
        return getDistance() > movementRange;
    }

    /**
     * Sprawdza czy Gracz może cofa się (kiting).
     *
     * @return czy można się wycofać
     */
    public boolean canPlayerKite() {
        return playerPosition > ArenaConstants.ARENA_MIN_COORDINATE;
    }

    /**
     * Sprawdza czy Rywal może się cofać (kiting).
     *
     * @return czy można się wycofać
     */
    public boolean canRivalKite() {
        return rivalPosition < ArenaConstants.ARENA_MAX_COORDINATE;
    }

    // ==================== GETTERS ====================

    public int getPlayerPosition() {
        return playerPosition;
    }

    public int getRivalPosition() {
        return rivalPosition;
    }

    // ==================== DEBUG ====================

    @Override
    public String toString() {
        return String.format("Arena: P=%d, R=%d, Distance=%d | %s %s",
            playerPosition, rivalPosition, getDistance(),
            canPlayerKite() ? "P-kite:ON" : "P-kite:OFF",
            canRivalKite() ? "R-kite:ON" : "R-kite:OFF");
    }

    public String visualize() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = ArenaConstants.ARENA_MIN_COORDINATE; i <= ArenaConstants.ARENA_MAX_COORDINATE; i++) {
            if (i == playerPosition) {
                sb.append("P");
            } else if (i == rivalPosition) {
                sb.append("R");
            } else if (i % 20 == 0) {
                sb.append("|");
            } else {
                sb.append("-");
            }
        }

        sb.append("]\n");
        sb.append("Distance: ").append(getDistance());

        return sb.toString();
    }
}

