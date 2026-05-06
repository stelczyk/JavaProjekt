package game.player.combat.action;

import game.player.combat.state.FighterState;

/**
 * Specjalna akcja dostępna TYLKO dla postaci idących ścieżką Confidenta.
 * Pozwala graczowi uciec z areny, kończąc walkę bez konsekwencji (ale też bez wygranej).
 *
 * Kontekst narracyjny:
 * Confident to sprytny przestępca który zna wyjścia awaryjne, przekupił strażników,
 * lub ma inne sposoby na wydostanie się z niebezpiecznej sytuacji.
 *
 * Mechanika:
 * - Dostępna TYLKO dla ścieżki Confidenta
 * - Kończy walkę natychmiast
 * - Brak konsekwencji (gracz nie przegrywa)
 * - Brak nagrody (gracz nie wygrywa)
 * - Niska reakcja publiczności (tchórzliwe wyjście)
 *
 * NOTATKA: Ta klasa NIE dziedziczy po AbstractAttack, ponieważ to nie jest atak.
 * Jest to specjalna akcja która kończy walkę w unikalny sposób.
 */
public class LeaveFromArena implements ArenaAction {

    // Minimalna ilość Cunning wymagana do ucieczki
    // Confident musi być wystarczająco sprytny żeby znaleźć wyjście
    private static final int MIN_CUNNING_REQUIRED = 30;

    // Referencja do atrybutów gracza - potrzebne do sprawdzenia czy jest Confidentem
    private final int playerCunning;
    private final boolean isConfident;

    // Komunikat zwracany po wykonaniu akcji
    private String resultMessage;

    public LeaveFromArena(int playerCunning, boolean isConfident) {
        this.playerCunning = playerCunning;
        this.isConfident = isConfident;
    }

    @Override
    public String getActionName() {
        return "Opuść Arenę";
    }

    @Override
    public String getActionDescription() {
        return "Wykorzystujesz swoją wiedzę o ukrytych przejściach i przekupionych strażnikach, " +
                "by niepostrzeżenie opuścić arenę. Walka zakończy się bez wygranej, ale też bez przegranej. " +
                "Dostępne tylko dla Confidentów z wysoką przebiegłością.";
    }

    @Override
    public boolean canBePerformed(FighterState state) {
        // Warunki użycia ucieczki:
        // 1. Gracz MUSI być na ścieżce Confidenta (sprawdzane w konstruktorze)
        // 2. Gracz MUSI mieć wystarczająco wysokie Cunning (spryt = znajomość wyjść)
        // 3. Gracz MUSI mieć przynajmniej 1 HP (nie można uciec będąc nieprzytomnym)

        if (!isConfident) {
            return false; // Tylko Confident może uciec
        }

        if (playerCunning < MIN_CUNNING_REQUIRED) {
            return false; // Za mało sprytu żeby znaleźć wyjście
        }

        if (state.getCurrentHp() <= 0) {
            return false; // Nieprzytomni nie uciekają
        }

        return true;
    }

    @Override
    public boolean performAction(FighterState state) {
        if (!canBePerformed(state)) {
            resultMessage = "Nie możesz opuścić areny w tym momencie!";
            return false; // Akcja nie powiodła się, walka trwa
        }

        // Ucieczka udana - publiczność jest rozczarowana tchórzliwym wyjściem
        // Drastycznie obniżamy crowd satisfaction
        state.reduceCrowdSatisfaction(50);

        resultMessage = "Wymykasz się niepostrzeżenie przez boczne wyjście. " +
                "Publiczność gwiżdże z niezadowolenia, ale jesteś już bezpieczny. " +
                "Walka zakończona bez rozstrzygnięcia.";

        return true; // Akcja kończy walkę
    }

    @Override
    public String getResultMessage() {
        return resultMessage;
    }

    // NOTATKA DLA SYSTEMU WALKI:
    //
    // LeaveFromArena jest unikalną akcją która kończy walkę w sposób niestandardowy:
    //
    // if (action.performAction(playerState)) {
    //     // Walka się kończy
    //     System.out.println(action.getResultMessage());
    //
    //     // NIE przyznawaj nagrody (gracz nie wygrał)
    //     // NIE nakładaj kary (gracz nie przegrał)
    //     // AKTUALIZUJ statystyki: fightsFled++ zamiast fightsWon/fightsLost
    //
    //     return FightResult.FLED; // Nowy typ zakończenia walki
    // }
    //
    // Zalecana implementacja enuma FightResult:
    // enum FightResult {
    //     VICTORY,    // Gracz wygrał
    //     DEFEAT,     // Gracz przegrał
    //     FLED,       // Gracz uciekł (tylko Confident)
    //     DRAW        // Remis (na przyszłość)
    // }
}
