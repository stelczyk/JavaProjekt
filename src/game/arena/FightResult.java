package game.arena;

import game.player.Player;

/**
 * Wynik pojedynczej walki na arenie.
 * Zwracany przez ArenaCombatEngine.startMatch() — używany do:
 * - wyświetlenia ekranu podsumowania (frontend / Main2)
 * - aktualizacji statystyk gracza
 * - testów (sprawdzenie kto wygrał, ile zadał obrażeń)
 */
public record FightResult(
        Outcome outcome,
        Player winner,
        int turns,
        int moneyEarned,
        int playerDamageDealt,
        int rivalDamageDealt,
        boolean playerKnockedOut,
        boolean rivalKnockedOut
) {

    public enum Outcome {
        PLAYER_WIN,
        PLAYER_LOSS,
        DRAW
    }

    public boolean isPlayerWin() {
        return outcome == Outcome.PLAYER_WIN;
    }

    public boolean isPlayerLoss() {
        return outcome == Outcome.PLAYER_LOSS;
    }
}
