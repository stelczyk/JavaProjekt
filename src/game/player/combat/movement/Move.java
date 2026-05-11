package game.player.combat.movement;

import game.arena.ArenaConstants;
import game.player.attributes.arena.ArenaMovementPlayer;
import game.player.attributes.types.ArenaMovementPlayerAttributes;
import game.player.combat.FightMove;

/**
 * MOVE - Abstrakcyjna klasa ruchu na Arenie.
 *
 * SYSTEM RUCHU:
 * Y (Zasięg Ruchu) = Speed (X) * MOVEMENT_SPEED_MULTIPLIER
 * Y = X * 0.5
 *
 * Czerpie bezpośrednio z atrybutu Speed gracza/rywala.
 * Each move type (Step, Jump) ma inny mnożnik staminowy.
 */
public abstract class Move implements ArenaMovementPlayer, FightMove {
    protected String movementName;
    protected double staminaCost;          // Koszt staminy dla tego ruchu
    protected MovementDirection direction;  // Kierunek ruchu

    public Move(String movementName, double staminaCost, MovementDirection direction) {
        this.movementName = movementName;
        this.staminaCost = staminaCost;
        this.direction = direction;
    }

    /**
     * Oblicza rzeczywisty dystans ruchu na podstawie atrybutu Speed.
     *
     * FORMULA: Y = Speed * MOVEMENT_SPEED_MULTIPLIER
     * Y = Speed * 0.5
     *
     * Przykład: Speed = 10 → Y = 5 jednostek na Arenie
     *
     * @param playerAttributes atrybuty gracza (zawiera Speed)
     * @return dystans (Y) - maksymalny zasięg ruchu w jednostkach Areny
     */
    @Override
    public double sizeOfStep(ArenaMovementPlayerAttributes playerAttributes) {
        int speed = playerAttributes.getSpeed();
        return speed * ArenaConstants.MOVEMENT_SPEED_MULTIPLIER;
    }

    @Override
    public String getMovementName() {
        return movementName;
    }

    @Override
    public MovementDirection moveDirection() {
        return direction;
    }

    /**
     * Sprawdza czy ruch jest możliwy.
     *
     * WARUNKI:
     * 1. Gracz ma wystarczającą staminę
     * 2. Ruch nie wybiega poza granice Areny
     * 3. Jeśli zbliżanie: D > Y (dystans > zasięg)
     *
     * @param attacker atrybuty atakującego (dla Speed)
     * @param defender atrybuty broniącego (potrzebne dla dystansu?)
     * @return czy ruch jest możliwy
     */
    @Override
    public boolean isPossibleMove(ArenaMovementPlayerAttributes attacker, ArenaMovementPlayerAttributes defender) {
        // Tutaj mogą być dodatkowe warunki
        // Na razie zwracamy true - weryfikacja zrobi się w ArenaEngine
        return true;
    }

    // ==================== GETTERS ====================

    public double getStaminaCost() {
        return staminaCost;
    }

    public MovementDirection getDirection() {
        return direction;
    }

    /**
     * Zwraca opis ruchu dla UI.
     */
    @Override
    public String getActionDescription() {
        return getMovementName();
    }
}
