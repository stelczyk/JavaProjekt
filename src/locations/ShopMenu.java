package locations;

import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Item;
import game.items.Weapon;
import game.player.Player;
import game.player.attributes.CharacterAttributeType;

import java.util.List;
import java.util.Scanner;



// interejs do konsoli sklepu, oddzielony od shop.java
public class ShopMenu {

    private final Shop shop;
    private final Scanner scanner;

    public ShopMenu(Shop shop, Scanner scanner) {
        this.shop = shop;
        this.scanner = scanner;
    }

    // Główne wejście do sklepu
    public void open(Player player) {
        boolean inShop = true;
        while (inShop) {
            printHeader(player);
            System.out.println("  [1] Broń");
            System.out.println("  [2] Odzież");
            System.out.println("  [3] Sprzedaj przedmiot");
            System.out.println("  [4] Ekwipunek");
            if (player.getStatPointsAvailable() > 0) {
                System.out.println("  [5] *** Rozdaj punkty statystyk (" + player.getStatPointsAvailable() + " dostępnych) ***");
            }
            System.out.println("  [6] Statystyki");
            System.out.println("  [0] Wyjdź ze sklepu");
            System.out.print("  Wybór: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> showWeapons(player);
                case 2 -> showClothing(player);
                case 3 -> showSellMenu(player);
                case 4 -> showEquipment(player);
                case 5 -> {
                    if (player.getStatPointsAvailable() > 0) distributeStats(player);
                    else System.out.println("  Nieprawidłowy wybór.");
                }
                case 6 -> showPlayerStats(player);
                case 0 -> inShop = false;
                default -> System.out.println("  Nieprawidłowy wybór.");
            }
        }
        System.out.println("  Do zobaczenia!");
    }

    // -------------------------------------------------------
    // BROŃ
    // -------------------------------------------------------

    private void showWeapons(Player player) {
        List<Weapon> weapons = shop.getWeaponsForSale();
        boolean inSection = true;
        while (inSection) {
            printHeader(player);
            System.out.println("=== BROŃ ===");
            for (int i = 0; i < weapons.size(); i++) {
                Weapon w = weapons.get(i);
                int price = shop.calculateDiscountedPrice(player, w);
                String locked = player.getLevel() < w.getRequiredLevel()
                        ? "  [poziom " + w.getRequiredLevel() + " wymagany]" : "";
                String owned = player.getInventory().hasItem(w) ? " [MASZ]" : "";
                Weapon equipped = player.getInventory().getEquippedWeapon();
                String strDiff = equipped != null ? diffStr(w.getStrengthBonus(), equipped.getStrengthBonus()) : "";
                String accDiff = equipped != null ? diffStr(w.getAccuracyBonus(), equipped.getAccuracyBonus()) : "";
                System.out.printf("  [%d] %-24s | Siła: +%d%-5s | Celność: +%d%-5s | %d zł%s%s%n",
                        i + 1, w.getName(), w.getStrengthBonus(), strDiff, w.getAccuracyBonus(), accDiff, price, locked, owned);
            }
            System.out.println("  [0] Wróć");
            System.out.print("  Wybór: ");

            int choice = readInt();
            if (choice == 0) {
                inSection = false;
            } else if (choice >= 1 && choice <= weapons.size()) {
                buyItem(player, weapons.get(choice - 1));
            } else {
                System.out.println("  Nieprawidłowy wybór.");
            }
        }
    }

    // -------------------------------------------------------
    // ODZIEŻ
    // -------------------------------------------------------

    private void showClothing(Player player) {
        List<Clothing> clothes = shop.getClothingForSale();
        boolean inSection = true;
        while (inSection) {
            printHeader(player);
            System.out.println("=== ODZIEŻ ===");
            for (int i = 0; i < clothes.size(); i++) {
                Clothing c = clothes.get(i);
                int price = shop.calculateDiscountedPrice(player, c);
                String locked = player.getLevel() < c.getRequiredLevel()
                        ? "  [poziom " + c.getRequiredLevel() + " wymagany]" : "";
                String owned = player.getInventory().hasItem(c) ? " [MASZ]" : "";
                Clothing equippedInSlot = player.getInventory().getEquippedClothing(c.getSlot());
                String defDiff = equippedInSlot != null ? diffStr(c.getDefenseBonus(), equippedInSlot.getDefenseBonus()) : "";
                String spdDiff = equippedInSlot != null ? diffStr(c.getSpeedBonus(), equippedInSlot.getSpeedBonus()) : "";
                System.out.printf("  [%2d] %-24s | %-12s | Obr: +%d%-5s | Szybk: +%d%-5s | %d zł%s%s%n",
                        i + 1, c.getName(), c.getSlot().name(), c.getDefenseBonus(), defDiff, c.getSpeedBonus(), spdDiff, price, locked, owned);
            }
            System.out.println("  [0] Wróć");
            System.out.print("  Wybór: ");

            int choice = readInt();
            if (choice == 0) {
                inSection = false;
            } else if (choice >= 1 && choice <= clothes.size()) {
                buyItem(player, clothes.get(choice - 1));
            } else {
                System.out.println("  Nieprawidłowy wybór.");
            }
        }
    }

    private void buyItem(Player player, Item item) {
        int price = shop.calculateDiscountedPrice(player, item);
        System.out.printf("  Kupić '%s' za %d zł? [t/n]: ", item.getName(), price);
        if (readYesNo()) {
            ShopResult result = shop.buyItem(player, item);
            printResult(result, item.getName());
        }
    }

    // -------------------------------------------------------
    // SPRZEDAŻ
    // -------------------------------------------------------

    private void showSellMenu(Player player) {
        boolean inSection = true;
        while (inSection) {
            List<Item> items = player.getInventory().getOwnedItems();
            if (items.isEmpty()) {
                System.out.println("  Nie masz nic do sprzedania.");
                return;
            }
            printHeader(player);
            System.out.println("=== SPRZEDAJ PRZEDMIOT ===");
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.printf("  [%d] %-24s | Odzyskasz: %d zł%n",
                        i + 1, item.getName(), item.getSellPrice());
            }
            System.out.println("  [0] Wróć");
            System.out.print("  Wybór: ");

            int choice = readInt();
            if (choice == 0) {
                inSection = false;
            } else if (choice >= 1 && choice <= items.size()) {
                Item toSell = items.get(choice - 1);
                System.out.printf("  Sprzedać '%s' za %d zł? [t/n]: ", toSell.getName(), toSell.getSellPrice());
                if (readYesNo()) {
                    ShopResult result = shop.sellItem(player, toSell);
                    printResult(result, toSell.getName());
                }
                if (player.getInventory().getOwnedItems().isEmpty()) inSection = false;
            } else {
                System.out.println("  Nieprawidłowy wybór.");
            }
        }
    }

    // -------------------------------------------------------
    // EKWIPUNEK
    // -------------------------------------------------------

    private void showEquipment(Player player) {
        boolean inSection = true;
        while (inSection) {
            printHeader(player);
            System.out.println("=== EKWIPUNEK ===");

            Weapon w = player.getInventory().getEquippedWeapon();
            System.out.println("  Broń: " + (w != null
                    ? w.getName() + " (Siła: +" + w.getStrengthBonus() + ", Celność: +" + w.getAccuracyBonus() + ")"
                    : "brak"));

            for (ClothingSlot slot : ClothingSlot.values()) {
                Clothing c = player.getInventory().getEquippedClothing(slot);
                System.out.printf("  %-12s: %s%n", slot.name(), c != null ? c.getName() : "brak");
            }

            System.out.println();
            System.out.println("  [1] Załóż broń");
            System.out.println("  [2] Załóż odzież");
            System.out.println("  [3] Zdejmij broń");
            System.out.println("  [4] Zdejmij odzież");
            System.out.println("  [0] Wróć");
            System.out.print("  Wybór: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> equipWeaponMenu(player);
                case 2 -> equipClothingMenu(player);
                case 3 -> {
                    shop.unequipWeapon(player);
                    System.out.println("  Broń zdjęta.");
                }
                case 4 -> unequipClothingMenu(player);
                case 0 -> inSection = false;
                default -> System.out.println("  Nieprawidłowy wybór.");
            }
        }
    }

    private void equipWeaponMenu(Player player) {
        List<Item> weapons = player.getInventory().getOwnedItems().stream()
                .filter(i -> i instanceof Weapon)
                .toList();
        if (weapons.isEmpty()) {
            System.out.println("  Nie masz broni w plecaku.");
            return;
        }
        System.out.println("  Wybierz broń:");
        for (int i = 0; i < weapons.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, weapons.get(i).getName());
        }
        System.out.print("  Wybór: ");
        int choice = readInt();
        if (choice >= 1 && choice <= weapons.size()) {
            ShopResult result = shop.equipWeapon(player, (Weapon) weapons.get(choice - 1));
            printResult(result, weapons.get(choice - 1).getName());
        }
    }

    private void equipClothingMenu(Player player) {
        List<Item> clothes = player.getInventory().getOwnedItems().stream()
                .filter(i -> i instanceof Clothing)
                .toList();
        if (clothes.isEmpty()) {
            System.out.println("  Nie masz odzieży w plecaku.");
            return;
        }
        System.out.println("  Wybierz odzież:");
        for (int i = 0; i < clothes.size(); i++) {
            Clothing c = (Clothing) clothes.get(i);
            System.out.printf("  [%d] %-20s | Slot: %s%n", i + 1, c.getName(), c.getSlot().name());
        }
        System.out.print("  Wybór: ");
        int choice = readInt();
        if (choice >= 1 && choice <= clothes.size()) {
            ShopResult result = shop.equipClothing(player, (Clothing) clothes.get(choice - 1));
            printResult(result, clothes.get(choice - 1).getName());
        }
    }

    private void unequipClothingMenu(Player player) {
        ClothingSlot[] slots = ClothingSlot.values();
        System.out.println("  Wybierz slot do zdjęcia:");
        for (int i = 0; i < slots.length; i++) {
            Clothing c = player.getInventory().getEquippedClothing(slots[i]);
            System.out.printf("  [%d] %-12s: %s%n", i + 1, slots[i].name(), c != null ? c.getName() : "brak");
        }
        System.out.print("  Wybór: ");
        int choice = readInt();
        if (choice >= 1 && choice <= slots.length) {
            shop.unequipClothing(player, slots[choice - 1]);
            System.out.println("  Zdjęto ze slotu " + slots[choice - 1].name() + ".");
        }
    }

    // -------------------------------------------------------
    // ROZDANIE PUNKTÓW STATYSTYK
    // -------------------------------------------------------

    private void distributeStats(Player player) {
        while (player.getStatPointsAvailable() > 0) {
            printHeader(player);
            System.out.println("=== ROZDAJ PUNKTY STATYSTYK ===");
            System.out.println("  Dostępne punkty: " + player.getStatPointsAvailable());
            System.out.println();
            System.out.println("  [1]  Siła          (Strength)    = " + player.getAttributes().getStrength());
            System.out.println("  [2]  Obrona        (Defense)     = " + player.getAttributes().getDefense());
            System.out.println("  [3]  Celność       (Accuracy)    = " + player.getAttributes().getAccuracy());
            System.out.println("  [4]  Wytrzymałość  (Stamina)     = " + player.getAttributes().getStamina());
            System.out.println("  [5]  Odwaga        (Brave)       = " + player.getAttributes().getBrave());
            System.out.println("  [6]  Cwaniactwo    (Cunning)     = " + player.getAttributes().getCunning());
            System.out.println("  [7]  Szybkość      (Speed)       = " + player.getAttributes().getSpeed());
            System.out.println("  [8]  Patologia     (Pathology)   = " + player.getAttributes().getPathology());
            System.out.println("  [9]  Honor         (Valor)       = " + player.getAttributes().getValor());
            System.out.println("  [10] Znajomości    (Connections) = " + player.getAttributes().getConnections());
            System.out.println("  [0]  Wróć (punkty zostają na później)");
            System.out.print("  Wybór: ");

            int choice = readInt();
            if (choice == 0) break;

            CharacterAttributeType type = switch (choice) {
                case 1  -> CharacterAttributeType.STRENGTH;
                case 2  -> CharacterAttributeType.DEFENSE;
                case 3  -> CharacterAttributeType.ACCURACY;
                case 4  -> CharacterAttributeType.STAMINA;
                case 5  -> CharacterAttributeType.BRAVE;
                case 6  -> CharacterAttributeType.CUNNING;
                case 7  -> CharacterAttributeType.SPEED;
                case 8  -> CharacterAttributeType.PATHOLOGY;
                case 9  -> CharacterAttributeType.VALOR;
                case 10 -> CharacterAttributeType.CONNECTIONS;
                default -> null;
            };

            if (type != null) {
                int amount = askForAmount(player.getStatPointsAvailable());
                for (int i = 0; i < amount; i++) {
                    player.getAttributes().upgradeAttribute(type);
                    player.spendStatPoint();
                }
            } else {
                System.out.println("  Nieprawidłowy wybór.");
            }
        }
    }

    // -------------------------------------------------------
    // POMOCNICZE
    // -------------------------------------------------------

    private void printHeader(Player player) {
        System.out.println();
        System.out.println("+-----------------------------------------+");
        System.out.printf("| %-15s | Lvl: %-2d | Kasa: %-6d zł |%n",
                player.getProfile().getPlayerNickname(), player.getLevel(), player.getMoney());
        if (player.getStatPointsAvailable() > 0) {
            System.out.println("|  ** " + player.getStatPointsAvailable() + " punktów czeka na rozdanie! **        |");
        }
        System.out.println("+-----------------------------------------+");
    }

    private void printResult(ShopResult result, String itemName) {
        switch (result) {
            case SUCCESS        -> System.out.println("  OK: " + itemName);
            case NOT_ENOUGH_MONEY -> System.out.println("  Brak kasy!");
            case LEVEL_TOO_LOW  -> System.out.println("  Za niski poziom!");
            case ITEM_NOT_OWNED -> System.out.println("  Nie masz tego przedmiotu.");
            case ALREADY_OWNED    -> System.out.println("  Już to masz!");
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean readYesNo() {
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("t") || input.equals("tak");
    }

    private void showPlayerStats(Player player) {
        printHeader(player);
        System.out.println("=== STATYSTYKI ===");
        System.out.println("  XP:         " + player.getXp() + " / " + (player.getLevel() * 150));
        System.out.println("  Max HP:     " + player.getMaxHp());
        System.out.println();
        System.out.println("  Siła:       " + player.getAttributes().getStrength());
        System.out.println("  Obrona:     " + player.getAttributes().getDefense());
        System.out.println("  Celność:    " + player.getAttributes().getAccuracy());
        System.out.println("  Stamina:    " + player.getAttributes().getStamina());
        System.out.println("  Odwaga:     " + player.getAttributes().getBrave());
        System.out.println("  Cwaniactwo: " + player.getAttributes().getCunning());
        System.out.println("  Szybkość:   " + player.getAttributes().getSpeed());
        System.out.println("  Patologia:  " + player.getAttributes().getPathology());
        System.out.println("  Honor:      " + player.getAttributes().getValor());
        System.out.println("  Znajomości: " + player.getAttributes().getConnections());
        System.out.println();
        System.out.println("  Bonus obrony z ekwipunku:    +" + player.getInventory().getTotalDefenseBonus());
        System.out.println("  Bonus szybkości z ekwipunku: +" + player.getInventory().getTotalSpeedBonus());
        System.out.println();
        System.out.print("  [Enter] Wróć...");
        scanner.nextLine();
    }

    private int askForAmount(int available) {
        while (true) {
            System.out.print("  Ile punktów? (1-" + available + "): ");
            try {
                int amount = Integer.parseInt(scanner.nextLine().trim());
                if (amount < 1 || amount > available) {
                    System.out.println("  Podaj liczbę od 1 do " + available + ".");
                } else {
                    return amount;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Podaj liczbę!");
            }
        }
    }

    private String diffStr(int newVal, int oldVal) {
        int delta = newVal - oldVal;
        if (delta > 0) return "(+" + delta + ")";
        if (delta < 0) return "(" + delta + ")";
        return "(=)";
    }
}