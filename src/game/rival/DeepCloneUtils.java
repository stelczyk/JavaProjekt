package game.rival;

import game.player.Player;
import game.player.PlayerAttribute;
import game.player.PlayerProfile;

/**
 * Narzędzie do bezpiecznego klonowania (Deep Copy) obiektu Player.
 *
 * Philosophy:
 * - Oryginalny Player pozostaje ABSOLUTE READ-ONLY
 * - Klon jest CAŁKOWICIE niezależny od oryginału
 * - Nie ma wspólnych referencji pomiędzy graczem a rywalem
 *
 * BEZPIECZEŃSTWO:
 * - Prototyp przechodzi przez głęboką kopię
 * - Każdy atybut jest indywidualnie klonowany
 * - Ekwipunek jest czyszczony (Rival zaczyna z niczym)
 */
public class DeepCloneUtils {

    /**
     * Tworzy głęboką kopię obiektu Player.
     *
     * @param original oryginalny gracz (NIE BĘDZIE MODYFIKOWANY)
     * @return Całkowicie niezależna kopia
     */
    public static Player deepClonePlayer(Player original) {
        // STEP 1: Klon profilu (imię, wiek)
        // Profil jest immutable, ale tworzymy nowy element dla logiki
        String clonedNickname = original.getProfile().getPlayerNickname() + "_Rival";
        int clonedAge = original.getProfile().getPlayerAge();

        PlayerProfile clonedProfile = new PlayerProfile(clonedNickname, clonedAge);

        // STEP 2: Utwórz nową instancję Player z sklonowanym profilem
        Player clonedPlayer = new Player(clonedProfile);

        // STEP 3: Klonuj atrybuty (deep copy)
        cloneAttributes(original.getAttributes(), clonedPlayer.getAttributes());

        // STEP 4: Klonuj ekwipunek jeśli było co klonować
        // W NASZYM PRZYPADKU: Rywal zaczyna z CZYSTYM ekwipunkiem
        // (zostanie napełniony przez RivalEquipmentManager)
        clonedPlayer.getInventory().unequipWeapon();
        // ownedItems są tworzone nowe na start

        // STEP 5: Klonuj globalny stan (money, xp, level)
        // Będą przeliczane przez RivalGenerator
        clonedPlayer.earnMoney(original.getMoney()); // Zaczynamy z pieniędzmiOriginalnego

        return clonedPlayer;
    }

    /**
     * Kopiuje atrybuty bezpośrednio (O(1) zamiast poprzedniego O(N) przez pętlę upgradeAttribute).
     */
    private static void cloneAttributes(PlayerAttribute source, PlayerAttribute target) {
        target.copyFrom(source);
    }
}

