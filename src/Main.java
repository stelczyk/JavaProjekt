import game.player.Player;
import locations.CharacterCreationMenu;
import locations.Shop;
import locations.ShopMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Tworzenie postaci
        CharacterCreationMenu creation = new CharacterCreationMenu(scanner);
        Player player = creation.create();

        // Sklep
        Shop shop = new Shop();
        ShopMenu shopMenu = new ShopMenu(shop, scanner);
        shopMenu.open(player);

        scanner.close();
    }
}