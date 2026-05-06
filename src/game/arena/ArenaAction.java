package game.arena;

/**
 * Interfejs reprezentujący specjalne akcje areny, które nie są atakami ani obroną.
 *
 * FILOZOFIA DESIGNU:
 * ArenaAction to akcje które NIE PASUJĄ do standardowego systemu walki (attack/defense).
 * Są to nietypowe działania które mogą:
 * - Zakończyć walkę w niestandardowy sposób (ucieczka, poddanie się)
 * - Zmienić zasady walki (apel do tłumu, negocjacje)
 * - Wywołać specjalne eventy (wezwanie arbitra, prośba o przerwę)
 *
 * RÓŻNICA OD ABSTRACTATTACK/ABSTRACTDEFENSE:
 * - Attack/Defense są CZĘŚCIĄ systemu walki (damage, accuracy, reduction)
 * - ArenaAction są POZA systemem walki (end fight, change rules, special events)
 *
 * PRZYKŁADY ARENAACTION:
 * - LeaveFromArena - Ucieczka z areny (tylko Confident)
 * - SurrenderFight - Poddanie się (kończy walkę, przegrana bez dalszych obrażeń)
 * - BribeOpponent - Przekupienie przeciwnika (tylko dla wysokiego Cunning)
 * - CallForBreak - Prośba o przerwę (regeneracja HP, ale -crowd satisfaction)
 *
 * KIEDY UŻYWAĆ ARENAACTION ZAMIAST ABSTRACTATTACK:
 * - Jeśli akcja NIE zadaje obrażeń → ArenaAction
 * - Jeśli akcja KOŃCZY walkę w nietypowy sposób → ArenaAction
 * - Jeśli akcja ma unikalne wymagania (ścieżka, specjalny item) → ArenaAction
 *
 * LOKALIZACJA:
 * ArenaAction jest w pakiecie game.arena, nie player.combat, ponieważ:
 * - To część mechaniki ARENY, nie gracza
 * - Może być używane zarówno przez gracza jak i NPC
 * - Należy do kontekstu walki (arena), nie kontekstu gracza (player)
 */
public interface ArenaAction {

    /**
     * Nazwa akcji wyświetlana graczowi (np. "Opuść Arenę", "Poddaj Się")
     */
    String getActionName();

    /**
     * Szczegółowy opis akcji i jej potencjalnych konsekwencji.
     * Powinien informować gracza o wymaganiach i efektach.
     */
    String getActionDescription();

    /**
     * Sprawdza czy akcja może być wykonana w danym momencie walki.
     *
     * WARUNKI MOGĄ OBEJMOWAĆ:
     * - Ścieżkę postaci (Confident, Leader, Warrior)
     * - Atrybuty (minimalne Cunning, Valor, etc.)
     * - Stan walki (HP > 0, nie można uciec będąc nieprzytomnym)
     * - Posiadane przedmioty (item do przekupstwa)
     *
     * @param fighter fighter który próbuje wykonać akcję
     * @return true jeśli akcja może być wykonana
     */
    boolean canBePerformed(ArenaFighter fighter);

    /**
     * Wykonuje akcję i zwraca informację czy walka powinna się zakończyć.
     *
     * SEMANTYKA ZWRACANEJ WARTOŚCI:
     * - true = akcja kończy walkę (ucieczka, poddanie się)
     * - false = akcja została wykonana, ale walka trwa (crowd appeal, buff)
     *
     * EFEKTY UBOCZNE:
     * Metoda może modyfikować stan fightera (crowd satisfaction, HP, etc.)
     * poprzez fighter.getState()
     *
     * @param fighter fighter wykonujący akcję
     * @return true jeśli akcja kończy walkę
     */
    boolean performAction(ArenaFighter fighter);

    /**
     * Zwraca komunikat wyświetlany graczowi PO wykonaniu akcji.
     * Komunikat powinien opisywać co się stało i jakie były konsekwencje.
     *
     * PRZYKŁADY:
     * - "Wymykasz się przez boczne wyjście. Publiczność gwiżdże z niezadowolenia."
     * - "Poddajesz się. Przeciwnik zostaje ogłoszony zwycięzcą."
     *
     * @return komunikat do wyświetlenia
     */
    String getResultMessage();
}
