import game.arena.*;
import game.player.Player;
import game.player.PlayerProfile;
import locations.Shop;
import locations.ShopMenu;

import java.util.Scanner;

/**
 * Main2 - FINALNY GAME LOOP (TUROWY)
 *
 * Kompletna aplikacja konsolowa łącząca wszystkie systemy:
 * - Interfejs dla gracz (input imienia)
 * - Shop (ekwipunek z informacją o armor i bonusach)
 * - Arena (Sparring vs Tournament)
 * - Audience Bar & Economy
 * - Rival Generator
 * - Combat System (turowy z inputem gracza)
 *
 * FLOW:
 * 1. Gracz podaje imię
 * 2. Menu główne: Sklep / Sparring / Tournament / Profil
 * 3. Gracz zarządza ekwipunkiem (widzi armor i bonusy)
 * 4. Walka turowa - gracz decyduje o każdym ruchu
 * 5. Rezultat i nagrody/kary
 * 6. Powrót do menu
 */
public class Main2 {

    private static Player player;
    private static AudienceBar audienceBar;
    private static Scanner scanner;
    private static Shop shop;
    private static boolean gameActive = true;

    public static void main(String[] args) {
        initialize();

        while (gameActive) {
            displayMainMenu();
        }

        cleanup();
    }

    /**
     * Inicjalizacja gry.
     */
    private static void initialize() {
        scanner = new Scanner(System.in);
        shop = new Shop();
        audienceBar = new AudienceBar();

        welcomeScreen();
        createPlayer();
    }

    private static void welcomeScreen() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                 🎮 ARENA COMBAT SYSTEM 🎮                 ║");
        System.out.println("║                   Edycja: Turowa & Interaktywna            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("Witaj w świecie Areny Combat!");
        System.out.println("Tutaj zdecydujesz o każdym ruchu w walce.");
        System.out.println("Zostań mistrzem areny i zdobądź sławę i fortunę!\n");
    }

    private static void createPlayer() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TWORZENIE POSTACI                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.print("Jak się masz? Podaj nazwę swojej postaci: ");
        String nickname = scanner.nextLine().trim();

        if (nickname.isEmpty()) {
            nickname = "Wojownik";
        }

        player = new Player(new PlayerProfile(nickname, 25));
        player.earnMoney(1000); // Startowe pieniądze

        System.out.println("\n✅ Postać " + nickname + " została utworzona!");
        System.out.println("   Monety: " + player.getMoney());
        System.out.println("   Level: " + player.getLevel() + "\n");
    }

    /**
     * Menu główne gry.
     */
    private static void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.printf("║  Gracz: %-40s ║\n", player.getProfile().getPlayerNickname() + " (Lv." + player.getLevel() + ")");
        System.out.printf("║  Monety: %-41d ║\n", player.getMoney());
        System.out.printf("║  Pancerz: %-40d ║\n", player.getInventory().getTotalDefenseBonus());
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1) 🏪 Sklep - kup/sprzedaj ekwipunek i atrybuty         ║");
        System.out.println("║  2) ⚔️  Sparring - trening (walka do pierwszej krwi)     ║");
        System.out.println("║  3) 🏆 Tournament - turniej (walka na śmierć i życie)   ║");
        System.out.println("║  4) 📊 Profil - zobacz statystyki postaci               ║");
        System.out.println("║  5) 🚪 Wyjście - wyjdź z gry                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.print("Wybierz opcję (1-5): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    openShop();
                    break;
                case 2:
                    startSparring();
                    break;
                case 3:
                    startTournament();
                    break;
                case 4:
                    showProfile();
                    break;
                case 5:
                    gameActive = false;
                    break;
                default:
                    System.out.println("❌ Niepoprawny wybór!");
            }
        } catch (Exception e) {
            System.out.println("❌ Błąd inputu!");
            scanner.nextLine();
        }
    }

    /**
     * Otwiera sklep.
     */
    private static void openShop() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   🏪 SKLEP ARENY 🏪                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        ShopMenu shopMenu = new ShopMenu(shop, scanner);
        shopMenu.open(player);
    }

    /**
     * Sparring - Tryb treningowy.
     */
    private static void startSparring() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              🥊 SPARRING - TRENING BITEWNY 🥊             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        if (player.getMoney() < 50) {
            System.out.println("❌ Nie masz wystarczającej ilości monet na sparring (potrzeba 50 monet)!\n");
            return;
        }

        // Generuj rywala w lobby
        ArenaLobby lobbyArena = new ArenaLobby();
        ArenaLobby.ArenaMatch match = lobbyArena.enterArenaLobby(player);

        // Pytanie czy chcesz walczyć
        System.out.print("\nCzy chcesz walczyć z tym rywalem? (tak/nie): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("tak") && !answer.equals("t")  && !answer.equals("yes") && !answer.equals("y")) {
            System.out.println("Wracasz do głównego menu.\n");
            return;
        }

        player.spendMoney(50); // Koszt sparringu

        // WALKA TUROWA
        ArenaCombatEngine engine = new ArenaCombatEngine(CombatMode.SPARRING, scanner, audienceBar);
        engine.startMatch(player, match.getRival());

        System.out.print("\nNaciśnij Enter aby wrócić do menu...");
        scanner.nextLine();
    }

    /**
     * Tournament - Tryb rywalizacyjny.
     */
    private static void startTournament() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║            🏆 TURNIEJ - WALKA NA ŚMIERĆ I ŻYCIE 🏆        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        if (player.getMoney() < 100) {
            System.out.println("❌ Nie masz wystarczającej ilości monet (potrzeba 100 monet)!\n");
            return;
        }

        // Generuj rywala w lobby
        ArenaLobby lobbyArena = new ArenaLobby();
        ArenaLobby.ArenaMatch match = lobbyArena.enterArenaLobby(player);

        // Pytanie czy chcesz walczyć
        System.out.print("\nCzy chcesz walczyć w turnieju? (tak/nie): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("tak") && !answer.equals("t") && !answer.equals("yes") && !answer.equals("y")) {
            System.out.println("Wracasz do głównego menu.\n");
            return;
        }

        player.spendMoney(100); // Koszt turnieju

        // WALKA TUROWA
        ArenaCombatEngine engine = new ArenaCombatEngine(CombatMode.TOURNAMENT, scanner, audienceBar);
        engine.startMatch(player, match.getRival());

        System.out.print("\nNaciśnij Enter aby wrócić do menu...");
        scanner.nextLine();
    }

    /**
     * Wyświetla statystyki gracza.
     */
    private static void showProfile() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📊 PROFIL GRACZA 📊                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("┌─ INFORMACJE OGÓLNE ─────────────────────────┐");
        System.out.println("│ Imię: " + player.getProfile().getPlayerNickname());
        System.out.println("│ Wiek: " + player.getProfile().getPlayerAge());
        System.out.println("│ Level: " + player.getLevel());
        System.out.println("│ XP: " + player.getXp() + "/" + (player.getLevel() * 150));
        System.out.println("│ Monety: " + player.getMoney());
        System.out.println("└─────────────────────────────────────────────┘\n");

        System.out.println("┌─ ATRYBUTY ──────────────────────────────────┐");
        System.out.println("│ Siła (Strength): " + player.getAttributes().getStrength());
        System.out.println("│ Obrona (Defense): " + player.getAttributes().getDefense());
        System.out.println("│ Trafność (Accuracy): " + player.getAttributes().getAccuracy());
        System.out.println("│ Spryt (Cunning): " + player.getAttributes().getCunning());
        System.out.println("│ Odwaga (Valor): " + player.getAttributes().getValor());
        System.out.println("│ Wytrzymałość (Stamina): " + player.getAttributes().getStamina());
        System.out.println("│ Waga (Pathology): " + player.getAttributes().getPathology());
        System.out.println("│ Powiązania (Connections): " + player.getAttributes().getConnections());
        System.out.println("└─────────────────────────────────────────────┘\n");

        System.out.println("┌─ EKWIPUNEK ─────────────────────────────────┐");
        if (player.getInventory().getEquippedWeapon() != null) {
            System.out.println("│ Broń: " + player.getInventory().getEquippedWeapon().getName());
            System.out.println("│   Bonus Siły: +" + player.getInventory().getEquippedWeapon().getStrengthBonus());
            System.out.println("│   Bonus Trafności: +" + player.getInventory().getEquippedWeapon().getAccuracyBonus());
        } else {
            System.out.println("│ Broń: Brak");
        }

        int totalArmor = player.getInventory().getTotalDefenseBonus();
        int baseArmor = 0;
        System.out.println("│ Pancerz: " + totalArmor + " (bazowy: " + baseArmor + ")");
        System.out.println("│ Bonus Szybkości: " + player.getInventory().getTotalSpeedBonus());
        System.out.println("└─────────────────────────────────────────────┘\n");

        System.out.println("┌─ PUBLICZNOŚĆ ───────────────────────────────┐");
        System.out.println("│ Publiczność: " + audienceBar.getBarVisualization());
        System.out.println("└─────────────────────────────────────────────┘\n");

        System.out.print("Naciśnij Enter aby wrócić...");
        scanner.nextLine();
    }

    /**
     * Czyszczenie zasobów.
     */
    private static void cleanup() {
        scanner.close();
        System.out.println("\n👋 Dziękujemy za grę! Do widzenia!\n");
    }
}

