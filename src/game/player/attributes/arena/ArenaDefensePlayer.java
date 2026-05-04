package game.player.attributes.arena; // To musi być ZAWSZE pierwsza linijka!

import game.player.combat.FightMove;
import game.player.attributes.types.ArenaDefencePlayerAttributes;

public interface ArenaDefensePlayer extends FightMove {
    String getDefenseName(); // Nazwa obrony (np. "Zasłona gardą", "Szybki unik")

    int defenseAccuracyValue(ArenaDefencePlayerAttributes defender); // Szansa na całkowite uniknięcie ciosu jako liczba (pomysł moj jest taki zeby robić to na wartosciach a nie procentowo potem sumowac wszystko +atrybuty+rzeczy ze sklepu idp) i na koncu zwracać % szansy na powodzenie ciosu

    int calculateDamageReduction(ArenaDefencePlayerAttributes defender); // Ile obrażeń zadano
}