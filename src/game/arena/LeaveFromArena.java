package game.arena;

/**
 * Specjalna akcja dostępna TYLKO dla postaci idących ścieżką Confidenta.
 * Pozwala graczowi uciec z areny, kończąc walkę bez konsekwencji (ale też bez wygranej).
 *
 * KONTEKST NARRACYJNY:
 * Confident to sprytny przestępca który:
 * - Zna ukryte wyjścia i tajne przejścia w arenie
 * - Przekupił strażników lub ma z nimi układy
 * - Posiada fałszywe dokumenty lub inne sposoby na ucieczkę
 *
 * MECHANIKA:
 * - Dostępna TYLKO dla ścieżki Confidenta
 * - Wymaga minimalnego Cunning (spryt = znajomość ukrytych ścieżek)
 * - Wymaga > 0 HP (nieprzytomni nie uciekają)
 * - Kończy walkę natychmiast (FightResult.FLED)
 * - Brak konsekwencji (gracz NIE przegrywa)
 * - Brak nagrody (gracz NIE wygrywa)
 * - Drastyczny spadek crowd satisfaction (-50)
 *
 * RÓŻNICA OD ABSTRACTATTACK:
 * To NIE jest atak - to akcja specjalna kończąca walkę w nietypowy sposób.
 * Dlatego implementuje ArenaAction, nie AbstractAttack.
 *
 * LOKALIZACJA W game.arena:
 * LeaveFromArena należy do mechaniki areny, nie gracza. To część systemu walki,
 * dlatego jest w pakiecie game.arena, a nie player.combat.
 */

/// To rodzielenie ataków miedzy ucieczka z areny wynika z tego, że LeaveFromArena to jedyna sytuacja kiedy mozna ucieć z areny i walka się kończy, nie jest to "zywkły" ruch
public class LeaveFromArena implements ArenaAction {

    // Minimalna ilość Cunning wymagana do ucieczki
    // Confident musi być wystarczająco sprytny żeby znaleźć ukryte wyjście
    private static final int MIN_CUNNING_REQUIRED = 30;

    // Penalty do crowd satisfaction za tchórzliwe opuszczenie areny
    private static final int CROWD_PENALTY = 50;

    // Referencja do atrybutów gracza - sprawdzamy czy jest Confidentem i czy ma cunning
    private final int playerCunning;
    private final boolean isConfident;

    // Komunikat zwracany po wykonaniu akcji
    private String resultMessage;

    /**
     * Konstruktor dla LeaveFromArena.
     *
     * NOTATKA: W przyszłości można to uprościć, przekazując cały ArenaFighter
     * zamiast poszczególnych atrybutów. Obecnie jednak potrzebujemy informacji
     * o ścieżce (isConfident), której jeszcze nie ma w ArenaFighter.
     *
     * @param playerCunning atrybut Cunning gracza
     * @param isConfident czy gracz jest na ścieżce Confidenta
     */
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
                String.format("Dostępne tylko dla Confidentów z przebiegłością ≥ %d.", MIN_CUNNING_REQUIRED);
    }

    @Override
    public boolean canBePerformed(ArenaFighter fighter) {
        // Warunki użycia ucieczki:
        // 1. Gracz MUSI być na ścieżce Confidenta (tylko oni znają tajne wyjścia)
        if (!isConfident) {
            return false;
        }

        // 2. Gracz MUSI mieć wystarczająco wysokie Cunning (spryt = znajomość ukrytych ścieżek)
        if (playerCunning < MIN_CUNNING_REQUIRED) {
            return false;
        }

        // 3. Gracz MUSI mieć przynajmniej 1 HP (nieprzytomni nie mogą uciekać)
        if (fighter.getState().getCurrentHp() <= 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean performAction(ArenaFighter fighter) {
        if (!canBePerformed(fighter)) {
            // Akcja nie może być wykonana - ustal dlaczego i poinformuj gracza
            if (!isConfident) {
                resultMessage = "Tylko Confidenci znają tajne wyjścia z areny!";
            } else if (playerCunning < MIN_CUNNING_REQUIRED) {
                resultMessage = String.format("Potrzebujesz Cunning ≥ %d, aby znaleźć ukryte wyjście!",
                        MIN_CUNNING_REQUIRED);
            } else {
                resultMessage = "Nie możesz uciec będąc nieprzytomnym!";
            }
            return false; // Akcja nie powiodła się, walka trwa dalej
        }

        // Ucieczka udana - publiczność jest rozczarowana tchórzliwym wyjściem
        fighter.getState().reduceCrowdSatisfaction(CROWD_PENALTY);

        resultMessage = String.format(
                "Wymykasz się niepostrzeżenie przez boczne wyjście. %s gwiżdże z niezadowolenia, " +
                        "ale jesteś już bezpieczny poza areną. Walka zakończona bez rozstrzygnięcia.",
                "Publiczność"
        );

        return true; // Akcja kończy walkę (FightResult.FLED)
    }

    @Override
    public String getResultMessage() {
        return resultMessage;
    }

    // ==================== NOTATKI DLA SYSTEMU WALKI ====================
    //
    // LeaveFromArena jest unikalną akcją która kończy walkę w sposób niestandardowy.
    //
    // PRZYKŁADOWA IMPLEMENTACJA W SILNIKU WALKI:
    //
    // if (playerAction instanceof ArenaAction) {
    //     ArenaAction action = (ArenaAction) playerAction;
    //     boolean fightEnds = action.performAction(playerFighter);
    //
    //     if (fightEnds) {
    //         System.out.println(action.getResultMessage());
    //
    //         if (action instanceof LeaveFromArena) {
    //             // NIE przyznawaj nagrody (gracz nie wygrał)
    //             // NIE nakładaj kary (gracz nie przegrał)
    //             // AKTUALIZUJ statystyki: player.incrementFightsFled()
    //             return FightResult.FLED;
    //         }
    //     }
    // }
    //
    // ZALECANA IMPLEMENTACJA FightResult ENUM:
    //
    // public enum FightResult {
    //     VICTORY,    // Gracz wygrał (przeciwnik HP = 0)
    //     DEFEAT,     // Gracz przegrał (gracz HP = 0)
    //     FLED,       // Gracz uciekł (LeaveFromArena)
    //     SURRENDER,  // Gracz się poddał (przyszła akcja)
    //     DRAW        // Remis (oba HP = 0, lub time limit)
    // }
    //
    // STATYSTYKI GRACZA (rozszerzenie Player):
    //
    // class Player {
    //     private int fightsWon;
    //     private int fightsLost;
    //     private int fightsFled;     // ← NOWE dla LeaveFromArena
    //     private int fightsSurrendered; // ← NOWE dla SurrenderFight (przyszłość)
    // }
}
