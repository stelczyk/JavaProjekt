package game.rival;

import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Item;
import game.items.ItemCatalog;
import game.items.Weapon;
import game.player.Player;
import locations.Shop;
import locations.ShopResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Zarządzanie ekwipunkiem Rywala.
 *
 * Philosophy:
 * 1. Sprzedaż: Sprzedaj CAŁY ekwipunek gracza za 100% wartości bazowej
 * 2. Budżet: Nowa pula × modyfikator [0.8 - 1.2] = Budżet Rywala
 * 3. Zakup: Kup losowe przedmioty w ramach budżetu
 * 4. Integracja: Użyj istniejących metod Shop
 *
 * WAŻNE:
 * - Rywal zaczyna z CZYSTYM ekwipunkiem (DeepCloneUtils.createEmptyInventory())
 * - Najpierw "sprzedajemy" całość gracza (hipotetycznie)
 * - Potem kupujemy nowe przedmioty
 */
public class RivalEquipmentManager {

    private static final double BUDGET_MODIFIER_MIN = 0.8;
    private static final double BUDGET_MODIFIER_MAX = 1.2;

    /**
     * Główna metoda: Przygotuj ekwipunek dla Rywala.
     *
     * KROKI:
     * 1. Sprzedaj hipotycznie cały ekwipunek gracza (sprzedaż za 100%)
     * 2. Powiększ budżet modyfikatorem [0.8 - 1.2]
     * 3. Kup losowe przedmioty dla Rywala
     * 4. Załóż wybrane przedmioty
     *
     * @param player gracz (źródło do obliczenia budżetu)
     * @param rival rival (gdzie będą dodane przedmioty)
     * @param rivalLevel level rywala (wpływa na dostęp do przedmiotów)
     * @param shop shop (do kupna przedmiotów)
     */
    public static void equipRival(Player player, Player rival, int rivalLevel, Shop shop) {
        // STEP 1: Oblicz budżet Rywala
        int rivalBudget = calculateRivalBudget(player);

        System.out.println("[RIVAL] Budżet na ekwipunek: " + rivalBudget + " złotych");

        // STEP 2: Daj Rivalowi te pieniądze
        rival.earnMoney(rivalBudget);

        // STEP 3: Kup losowe przedmioty w ramach budżetu
        purchaseEquipmentForRival(rival, rivalLevel, shop);

        System.out.println("[RIVAL] Ekwipunek przygotowany!");
    }

    /**
     * Oblicza budżet na ekwipunek Rywala.
     *
     * LOGIKA:
     * 1. Pobierz CAŁKOWITĄ WARTOŚĆ ekwipunku gracza (po 100% cenie sprzedaży)
     * 2. Pomnóż przez modyfikator [0.8 - 1.2]
     * 3. To jest budżet Rywala
     *
     * @param player gracz (źródło wartości ekwipunku)
     * @return budżet w złotych
     */
    private static int calculateRivalBudget(Player player) {
        int totalEquipmentValue = 0;

        // WARIANT 1: Sprzedaż za PEŁNĄ CENĘ BAZOWĄ (brak rabatu)
        // Sprzedajemy każdy przedmiot za jego getSellPrice() (50% ceny)
        // ALE: chcemy sprzedać za 100% wartości bazowej
        // Wyjście: obliczamy ceny Item.getPrice() * 0.5 * 2 = getPrice()

        // Lepiej: iterujemy po ekwipunku i sumujemy PEŁNE ceny
        // Item.getPrice() = cena kupna
        // Item.getSellPrice() = 50% ceny
        // Chcemy sprzedać za 100%

        // Iteruj po całym ekwipunku gracza
        for (Item item : player.getInventory().getOwnedItems()) {
            // Cena sprzedaży to zwykle getSellPrice() = getPrice() / 2
            // Ale chcemy sprzedać za 100%, więc: getPrice()
            totalEquipmentValue += item.getPrice();
        }

        // STEP 2: Pomnóż przez losowy modyfikator [0.8 - 1.2]
        double budgetModifier = generateBudgetModifier();
        int modifiedBudget = (int) (totalEquipmentValue * budgetModifier);

        return Math.max(0, modifiedBudget);
    }

    /**
     * Kupuje losowe przedmioty dla Rywala w ramach budżetu.
     *
     * LOGIKA:
     * 1. Filtruj dostępne przedmioty (level <= rivalLevel)
     * 2. Losuj przedmioty
     * 3. Jeśli starcza pieniędzy, kup
     * 4. Załóż przedmioty (weapon + clothing z każdego slotu)
     *
     * @param rival rival (gdzie dodawać przedmioty)
     * @param rivalLevel level rywala (dla filtrowania)
     * @param shop shop (do kupna)
     */
    private static void purchaseEquipmentForRival(Player rival, int rivalLevel, Shop shop) {
        Random random = new Random();

        // STEP 1: Filtruj bronie dostępne dla Rywala
        List<Weapon> availableWeapons = new ArrayList<>();
        for (Weapon weapon : ItemCatalog.WEAPONS) {
            if (weapon.getRequiredLevel() <= rivalLevel) {
                availableWeapons.add(weapon);
            }
        }

        // STEP 2: Filtruj odzież dostępną dla Rywala
        List<Clothing> availableClothing = new ArrayList<>();
        for (Clothing clothing : ItemCatalog.CLOTHING) {
            if (clothing.getRequiredLevel() <= rivalLevel) {
                availableClothing.add(clothing);
            }
        }

        // STEP 3: Kup BROŃ (opcjonalnie)
        if (!availableWeapons.isEmpty()) {
            Weapon randomWeapon = availableWeapons.get(random.nextInt(availableWeapons.size()));
            ShopResult weaponResult = shop.buyItem(rival, randomWeapon);

            if (weaponResult == ShopResult.SUCCESS) {
                shop.equipWeapon(rival, randomWeapon);
                System.out.println("[RIVAL] Kupił broń: " + randomWeapon.getName());
            }
        }

        // STEP 4: Kup ODZIEŻ (losowo dla każdego slotu)
        // Części ciała: GŁOWA, TUŁÓW, NOGI, BUTY, AKCESORIA
        for (ClothingSlot slot : ClothingSlot.values()) {
            // Filtruj odzież dla tego slotu
            List<Clothing> clothingForSlot = new ArrayList<>();
            for (Clothing clothing : availableClothing) {
                if (clothing.getSlot() == slot && rival.getMoney() >= shop.calculateDiscountedPrice(rival, clothing)) {
                    clothingForSlot.add(clothing);
                }
            }

            // Jeśli są opcje, losuj jedną
            if (!clothingForSlot.isEmpty()) {
                Clothing randomClothing = clothingForSlot.get(random.nextInt(clothingForSlot.size()));
                ShopResult clothingResult = shop.buyItem(rival, randomClothing);

                if (clothingResult == ShopResult.SUCCESS) {
                    shop.equipClothing(rival, randomClothing);
                    System.out.println("[RIVAL] Kupił odzież (" + slot + "): " + randomClothing.getName());
                }
            }
        }
    }

    /**
     * Generuje losowy modyfikator budżetu [0.8 - 1.2].
     *
     * @return modyfikator
     */
    private static double generateBudgetModifier() {
        Random random = new Random();
        return random.nextDouble() * (BUDGET_MODIFIER_MAX - BUDGET_MODIFIER_MIN) + BUDGET_MODIFIER_MIN;
    }

    /**
     * Publiczna metoda do generowania modyfikatora (dla testów).
     */
    public static double getBudgetModifier() {
        return generateBudgetModifier();
    }
}

