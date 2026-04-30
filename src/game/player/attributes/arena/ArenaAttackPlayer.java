package game.player.attributes.arena;

import game.player.combat.FightMove;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.state.FighterState;

public interface ArenaAttackPlayer extends FightMove {
    String getAttackName();

    // zwraca liczbe (nie %) jaka jest na to ze trafi ciosem, zalezne od siły + wytrzymałosci (nie atrybutu tylko tej kondycji w walce)
    int attackAccuracyValue(ArenaAttackPlayerAttributes attacker, FighterState state);

    // Zwraca czyste, hermetycznie zsumowane obrażenia
    int calculateDamage(ArenaAttackPlayerAttributes attacker, FighterState state);

    // Wpływ na publikę
    int getCrowdAppeal();
    // efekty specialne (rywal traci ruch coś ala palenie się na spelu w MIS)
    void applySpecialEffects(FighterState state);
}