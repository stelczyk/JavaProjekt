package game.player.attributes.arena;

import game.player.combat.FightMove;
import game.player.combat.state.FighterState;
import game.player.attributes.types.ArenaAttackPlayerAttributes;

/**
 * Interfejs definiujący CO GRACZ MOŻE ZROBIĆ podczas ataku na arenie.
 *
 * FILOZOFIA DESIGNU:
 * Ten interfejs NIE określa JAK atak jest wykonany (to rola konkretnych implementacji),
 * ale definiuje JAKIE INFORMACJE system walki potrzebuje od każdego ataku.
 *
 * ODPOWIEDZIALNOŚĆ:
 * - Rozłożenie ataku na "czynniki pierwsze": damage, accuracy, crowd appeal, efekty specjalne
 * - Nie wie o konkretnych atakach (melee/ranged/special) - to zadanie klas implementujących
 *
 * SEPARACJA OD DEFENSYWY:
 * System walki używa tego interfejsu do obliczenia RAW DAMAGE (surowych obrażeń).
 * Dopiero później system walki porówna to z defensywą przeciwnika.
 * Atak NIE ZNA defensywy - to zachowuje Single Responsibility Principle.
 */
public interface ArenaAttackPlayer extends FightMove {

    /**
     * Nazwa ataku wyświetlana graczowi (np. "Szybki Lewy Prosty")
     */
    String getAttackName();

    /**
     * Oblicza SUROWĄ WARTOŚĆ accuracy (nie procent!).
     * System walki później porówna to z defensywą i dopiero wtedy obliczy % trafienia.
     *
     * DLACZEGO WARTOŚĆ A NIE PROCENT:
     * Pozwala to elastycznie sumować accuracy z różnych źródeł (atrybuty + ekwipunek + buffy)
     * bez problemów z mnożeniem procentów. Procent jest obliczany na końcu przez system walki.
     *
     * @param attacker atrybuty atakującego (Strength, Accuracy, etc.)
     * @param state aktualny stan walki (stamina wpływa na celność)
     * @return surowa wartość accuracy (np. 50)
     */
     int attackAccuracyValue(ArenaAttackPlayerAttributes attacker, FighterState state);

    /**
     * Oblicza SUROWE OBRAŻENIA przed odjęciem defensywy.
     * System walki później odejmie armor/defense przeciwnika.
     *
     * HERMETYZACJA:
     * Metoda zwraca FINALNĄ wartość damage - już zsumowaną ze wszystkich źródeł
     * (atrybuty + stamina + ekwipunek + modyfikatory). System walki nie musi
     * wiedzieć JAK te obrażenia zostały obliczone.
     *
     * @param attacker atrybuty atakującego
     * @param state aktualny stan walki
     * @return surowe obrażenia (np. 45)
     */
    int calculateDamage(ArenaAttackPlayerAttributes attacker, FighterState state);

    /**
     * Wpływ ataku na publiczność (crowd appeal).
     * Różne ataki wzbudzają różne emocje: quick attack = 5, strong attack = 25.
     *
     * @return punkty crowd appeal
     */
    int getCrowdAppeal();

    /**
     * Efekty specjalne które występują PO ataku.
     * Najczęściej: konsumpcja staminy, ale mogą być też:
     * - Ogłuszenie przeciwnika (LeaderScream)
     * - Reset paska postępu (CallToFriends)
     * - Dodatkowy damage over time
     *
     * NOTATKA: Efekty które zmieniają PRZECIWNIKA (np. ogłuszenie) są obsługiwane
     * przez system walki, a nie bezpośrednio przez tę metodę. Tutaj tylko zmiany
     * stanu ATAKUJĄCEGO.
     *
     * @param state stan walki atakującego
     */
     void applySpecialEffects(FighterState state);
}