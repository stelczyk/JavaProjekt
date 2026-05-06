package game.player.combat.special;

/**
 * Interfejs reprezentujący system paska postępu dla specjalnych zdolności.
 * Używany głównie przez CallToFriends, ale zaprojektowany aby mógł być dziedziczony
 * przez inne typy postaci które mogą mieć własne specjalne umiejętności.
 *
 * Mechanika:
 * - Pasek wypełnia się w trakcie walki
 * - Szybkość ładowania zależy od atrybutów gracza (np. Valor)
 * - Level przeciwnika spowalnia ładowanie (wyższy level = więcej wymagań)
 * - Gdy pasek jest pełen, gracz może użyć specjalnej zdolności
 */
public interface SpecialAbilityProgress {

    /**
     * Zwraca aktualny postęp paska (0-100)
     * @return wartość postępu w procentach
     */
    int getCurrentProgress();

    /**
     * Zwiększa pasek postępu o określoną wartość
     * Wartość wzrostu powinna być obliczana na podstawie:
     * - atrybutu Valor gracza (im wyższy, tym szybszy wzrost)
     * - levelu przeciwnika (im wyższy, tym wolniejszy wzrost)
     *
     * @param amount wartość o jaką zwiększyć postęp
     */
    void increaseProgress(int amount);

    /**
     * Resetuje pasek postępu do zera
     * Używane po użyciu specjalnej zdolności
     */
    void resetProgress();

    /**
     * Sprawdza czy pasek jest pełny i zdolność może być użyta
     * @return true jeśli postęp osiągnął 100%
     */
    boolean isAbilityReady();

    /**
     * Oblicza tempo ładowania paska w danej turze
     * Uwzględnia Valor gracza i level przeciwnika
     *
     * @param playerValor atrybut Valor gracza
     * @param opponentLevel level przeciwnika
     * @return wartość o jaką pasek wzrośnie w tej turze
     */
    int calculateProgressGain(int playerValor, int opponentLevel);
}
