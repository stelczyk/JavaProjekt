package game.player.attributes.arena;

import game.player.combat.FightMove;
import game.player.combat.movement.MovementDirection;
import game.player.attributes.types.ArenaMovementPlayerAttributes;

public interface ArenaMovementPlayer extends FightMove {
    String getMovementName(); // Nazwa ruchu (np. "Skok", "Podejście", "Odwrót")

    double sizeOfStep(ArenaMovementPlayerAttributes playerAttributes); // dlugosc kroku niezaleznie od nazwy (zalezne od speed)

    MovementDirection moveDirection(); // kierunek ruchu (domyslenie user w prawo, ale pewnie rywal bedzie dziedziczył to on w lewo) plaszczyna 2D

    boolean isPossibleMove(ArenaMovementPlayerAttributes attackerMovement, ArenaMovementPlayerAttributes defenderMovement); // czy jest ruch mozliwy czy koniec mapy?
}