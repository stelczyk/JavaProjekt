package game.player.attributes.arena;

import game.player.attributes.types.ArenaAudiencePlayerAttributes;
import game.player.combat.attack.AttackStyle;

public interface ArenaAudiencePlayer {

    // Oblicza ile Hypu wygenerował gracz w tej turze, zalezne od atrybutów i rodzaju ciosu
    int calculateCrowdHype(ArenaAudiencePlayerAttributes player, AttackStyle styleUsed);

    // Oblicza końcowy mnożnik zarobków na podstawie paska hypu
    double calculateMoneyMultiplier(ArenaAudiencePlayerAttributes player);
}
