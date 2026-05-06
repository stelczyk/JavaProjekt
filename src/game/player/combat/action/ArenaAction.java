package game.player.combat.action;

import game.player.combat.state.FighterState;

/**
 * Interfejs reprezentujący specjalne akcje areny, które nie są atakami ani obroną.
 * Są to nietypowe działania które mogą zakończyć walkę lub zmienić jej przebieg w unikalny sposób.
 *
 * Przykłady ArenaAction:
 * - Ucieczka z areny (LeaveFromArena)
 * - Poddanie się
 * - Negocjacje z przeciwnikiem
 * - Apel do publiczności o zatrzymanie walki
 *
 * ArenaAction różni się od AbstractAttack/Defense tym że:
 * - Nie zadaje obrażeń ani nie blokuje ataków
 * - Może zakończyć walkę bez wygranej/przegranej
 * - Często ma unikalne warunki użycia (specjalna ścieżka, atrybut, etc.)
 */
public interface ArenaAction {

    /**
     * Nazwa akcji wyświetlana graczowi
     * @return nazwa akcji
     */
    String getActionName();

    /**
     * Opis akcji i jej potencjalnych konsekwencji
     * @return opis akcji
     */
    String getActionDescription();

    /**
     * Sprawdza czy akcja może być wykonana w danym momencie.
     * Każda akcja może mieć własne wymagania (ścieżka postaci, atrybuty, stan walki)
     *
     * @param state aktualny stan walki
     * @return true jeśli akcja może być wykonana
     */
    boolean canBePerformed(FighterState state);

    /**
     * Wykonuje akcję i zwraca informację czy walka powinna się zakończyć
     *
     * @param state stan walki
     * @return true jeśli akcja kończy walkę (np. ucieczka), false jeśli walka trwa dalej
     */
    boolean performAction(FighterState state);

    /**
     * Zwraca komunikat wyświetlany po wykonaniu akcji
     * @return komunikat dla gracza
     */
    String getResultMessage();
}
