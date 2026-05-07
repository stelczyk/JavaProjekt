import game.player.Player;
import game.player.PlayerProfile;
import locations.Shop;
import locations.ShopMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Tworzenie gracza
        PlayerProfile profile = new PlayerProfile("Janusz", 25);
        Player player = new Player(profile);

        // Opcjonalnie: daj kasę i XP żeby przetestować więcej rzeczy
        player.earnMoney(2000);          // dodatkowa kasa na testy
        player.gainXp(150);              // level 1 → 2 (próg to 1 * 150), dostaniesz +4 punkty
        // player.gainXp(450);           // od razu level 3 jeśli chcesz więcej

        // Otwórz sklep
        Shop shop = new Shop();
        ShopMenu menu = new ShopMenu(shop, scanner);
        menu.open(player);

        scanner.close();
    }
}