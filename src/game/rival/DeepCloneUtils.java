package game.rival;

import game.items.ItemCatalog;
import game.player.CharacterBodyType;
import game.player.Player;
import game.player.PlayerAttribute;
import game.player.PlayerInventory;
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
     * Klonuje atrybuty z jednego obiektu do drugiego.
     * Zapewnia, że każdy atrybut jest niezależny.
     */
    private static void cloneAttributes(PlayerAttribute source, PlayerAttribute target) {
        // Każdy atrybut jest zmienną prymitywną (int)
        // Musimy je "przeskalować" ręcznie

        // Pobierz wartości ze źródła
        int sourceStrength = source.getStrength();
        int sourceDefense = source.getDefense();
        int sourceAccuracy = source.getAccuracy();
        int sourceStamina = source.getStamina();
        int sourceBrave = source.getBrave();
        int sourceCunning = source.getCunning();
        int sourceSpeed = source.getSpeed();
        int sourcePathology = source.getPathology();
        int sourceValor = source.getValor();
        int sourceConnections = source.getConnections();

        // Cel: Zmień atrybuty na TARGET (najpierw usuń domyślne)
        // Ponieważ upgradeAttribute() dodaje DEFAULT_BOOST_ATTRIBUTE = 1
        // musimy obliczyć różnicę

        game.player.attributes.CharacterAttributeType[] attributeTypes =
            game.player.attributes.CharacterAttributeType.values();

        int[] sourceValues = {
            sourceStrength, sourceDefense, sourceAccuracy, sourceStamina,
            sourceBrave, sourceCunning, sourceSpeed, sourcePathology,
            sourceValor, sourceConnections
        };

        int[] targetValues = {
            target.getStrength(), target.getDefense(), target.getAccuracy(), target.getStamina(),
            target.getBrave(), target.getCunning(), target.getSpeed(), target.getPathology(),
            target.getValor(), target.getConnections()
        };

        // Dla każdego atrybutu: upgrade'uj tyle razy, aby osiągnąć wartość źródła
        for (int i = 0; i < attributeTypes.length; i++) {
            int difference = sourceValues[i] - targetValues[i];
            for (int j = 0; j < difference; j++) {
                target.upgradeAttribute(attributeTypes[i]);
            }
        }
    }

    /**
     * Tworzy PUSTE inventory dla rywala (czysty start).
     * Ekwipunek będzie napełniony przez RivalEquipmentManager.
     */
    public static PlayerInventory createEmptyInventory() {
        return new PlayerInventory();
        // Lista ownedItems jest pusta, nie ma ekwipunku
        // Wszystko jest NULL
    }
}

