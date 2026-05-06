package game.player.attributes.arena;

import game.player.combat.FightMove;
import game.player.attributes.types.ArenaDefencePlayerAttributes;

/**
 * Interfejs definiujący CO GRACZ MOŻE ZROBIĆ podczas obrony na arenie.
 *
 * FILOZOFIA DESIGNU:
 * Podobnie jak ArenaAttackPlayer, ten interfejs rozdziela AKCJĘ (defensywa)
 * od IMPLEMENTACJI (jak konkretna defensywa działa).
 *
 * DWA RODZAJE OBRONY:
 * 1. UNIK (defenseAccuracyValue) - całkowite uniknięcie ciosu (0 damage)
 * 2. REDUKCJA (calculateDamageReduction) - zmniejszenie otrzymanych obrażeń
 *
 * System walki:
 * - Najpierw sprawdza czy atak trafił (attackAccuracy vs defenseAccuracy)
 * - Jeśli trafił, odejmuje damageReduction od surowych obrażeń
 * - Finalne damage = max(rawDamage - damageReduction, 0)
 *
 * SEPARACJA OD ATAKU:
 * Defensywa NIE WIE o atakach - otrzymuje tylko surowe obrażenia od systemu walki.
 */
public interface ArenaDefensePlayer extends FightMove {

    /**
     * Nazwa defensywy wyświetlana graczowi (np. "Zasłona Gardą", "Szybki Unik")
     */
    String getDefenseName();

    /**
     * Oblicza SUROWĄ WARTOŚĆ defense accuracy (nie procent!).
     * System walki porówna attack accuracy vs defense accuracy i obliczy % uniknięcia.
     *
     * DLACZEGO WARTOŚĆ A NIE PROCENT:
     * - Umożliwia sumowanie defense z różnych źródeł (atrybuty + ekwipunek + stance)
     * - Procent oblicza system walki: dodgeChance = defenseAcc / (attackAcc + defenseAcc)
     * - Unika problemów z mnożeniem procentów (np. 50% * 50% = 25%, a nie 100%)
     *
     * @param defender atrybuty broniacego (Defense, Speed)
     * @return surowa wartość defense accuracy (np. 40)
     */
    int defenseAccuracyValue(ArenaDefencePlayerAttributes defender);

    /**
     * Oblicza ile obrażeń zostanie ZREDUKOWANE (nie ile pozostanie!).
     * System walki odejmie tę wartość od surowych obrażeń ataku.
     *
     * PRZYKŁAD:
     * Atak zadaje 50 raw damage, defensywa redukuje 20 → gracz otrzymuje 30 damage
     *
     * ARMOR vs DEFENSE:
     * - Defense (atrybut) - umiejętność blokowania
     * - Armor (ekwipunek) - fizyczna ochrona
     * Oba są sumowane w tej metodzie.
     *
     * @param defender atrybuty broniacego
     * @return wartość redukcji obrażeń (np. 20)
     */
    int calculateDamageReduction(ArenaDefencePlayerAttributes defender);
}