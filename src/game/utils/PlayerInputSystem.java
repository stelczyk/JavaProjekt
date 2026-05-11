package game.utils;

import java.util.Scanner;

/**
 * PlayerInputSystem - Interfejs do zbierania danych od użytkownika.
 *
 * KROK 0: Prosty interfejs pytający o dane gracza:
 * - Imię
 * - Nazwisko
 * - Wiek
 */
public class PlayerInputSystem {

    private final Scanner scanner;

    public PlayerInputSystem(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Zbiera dane gracza z konsoli.
     *
     * @return tablica [imię, nazwisko, wiek_jako_string]
     */
    public String[] gatherPlayerData() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   Witaj w ARENA FIGHTER!               ║");
        System.out.println("║   Stwórz swojego bohatera             ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.print("Podaj imię swojego gracza: ");
        String nickname = scanner.nextLine().trim();
        if (nickname.isEmpty()) {
            nickname = "Nieznany";
        }

        System.out.print("Podaj wiek gracza (rekomendacja 18-65): ");
        int age = 25; // default
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
            if (age < 18 || age > 100) {
                age = 25;
                System.out.println("(Wiek poza zakresem-> ustawiamy 25)");
            }
        } catch (NumberFormatException e) {
            System.out.println("(Błąd wejścia -> ustawiamy wiek 25)");
        }

        return new String[]{nickname, String.valueOf(age)};
    }

    /**
     * Menu główne gry.
     *
     * @return wybór użytkownika
     */
    public String showMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         GŁÓWNE MENU                    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Sklep                               ║");
        System.out.println("║ 2. Sparring (Trening)                  ║");
        System.out.println("║ 3. Tournament (Turniej)                ║");
        System.out.println("║ 4. Statystyki                          ║");
        System.out.println("║ 0. Wyjście                             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Wybór: ");

        return scanner.nextLine().trim();
    }

    /**
     * Wybór akcji w walce.
     *
     * @return kod akcji
     */
    public String showCombatMenu() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│         TWOJA TURA - AKCJE             │");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ 1. Atak - Szybki                       │");
        System.out.println("│ 2. Atak - Normalny                     │");
        System.out.println("│ 3. Atak - Silny                        │");
        System.out.println("│ 4. Krok (Step)                         │");
        System.out.println("│ 5. Skok (Jump)                         │");
        System.out.println("│ 6. Blok/Wycofanie                      │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("Wybór: ");

        return scanner.nextLine().trim();
    }
}

