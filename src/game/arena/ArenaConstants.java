package game.arena;

/**
 * ARENA CONSTANTS - Stałe globalne dla Arena System.
 *
 * Definiuje wymiary Areny, pozycje startowe i inne parametry.
 */
public final class ArenaConstants {

    // ==================== ARENA DIMENSIONS ====================
    public static final int ARENA_MIN_COORDINATE = -100;
    public static final int ARENA_MAX_COORDINATE = 100;
    public static final int ARENA_SIZE = ARENA_MAX_COORDINATE - ARENA_MIN_COORDINATE;

    // ==================== STARTING POSITIONS ====================
    public static final int PLAYER_STARTING_POSITION = -20;
    public static final int RIVAL_STARTING_POSITION = 20;
    public static final int DEFAULT_STARTING_DISTANCE = RIVAL_STARTING_POSITION - PLAYER_STARTING_POSITION;

    // ==================== MOVEMENT SCALING ====================
    // Zasięg Ruchu (Y) = Speed (X) * MOVEMENT_SPEED_MULTIPLIER
    public static final double MOVEMENT_SPEED_MULTIPLIER = 0.5;

    // ==================== AI DECISION ====================
    public static final double BASE_OPTIMAL_CHANCE = 0.50; // 50% szans na optymalny ruch
    public static final double CUNNING_MULTIPLIER = 0.02;   // Każdy punkt Cunning += 2% szansy

    // ==================== CRITICAL HIT ====================
    // Valor-based critical hit chance
    // Formula: baseChance + (attackerValor - defenderValor) * valorMultiplier
    public static final double BASE_CRITICAL_CHANCE = 0.10; // 10% base
    public static final double VALOR_MULTIPLIER = 0.01;     // Każdy punkt Valor += 1% szansy

    // ==================== DEBUG ====================
    public static final boolean DEBUG_POSITIONING = false;
    public static final boolean DEBUG_MOVEMENT = false;
    public static final boolean DEBUG_AI = false;

    private ArenaConstants() {}
}

