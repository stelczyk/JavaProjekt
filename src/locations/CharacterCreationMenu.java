package locations;

import game.player.CharacterPath;
import game.player.Player;
import game.player.PlayerProfile;
import game.player.attributes.CharacterAttributeType;

import java.util.Scanner;

public class CharacterCreationMenu {
    private final Scanner scanner;

    public CharacterCreationMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public Player create(){
        printTitle();

        String nick = askForNick();
        int age = askForAge();

        PlayerProfile profile = new PlayerProfile(nick, age);
        Player player = new Player(profile);

        System.out.println();
        System.out.println("  Witaj, " + nick + "!");
        System.out.println("  Masz " + player.getStatPointsAvailable() + " punktów do rozdania na start.");
        System.out.println("  Zdecyduj kim chcesz być na arenie.");

        distributeStartingPoints(player);
        choosePath(player);

        System.out.println();
        System.out.println("  *** Postać gotowa! Powodzenia na arenie. ***");
        System.out.printf("  Ścieżka: %s — %s%n",
                player.getPath().getDisplayName(), player.getPath().getDescription());
        System.out.print("  [Enter] Kontynuuj...");
        scanner.nextLine();

        return player;
    }

    private String askForNick() {
        while (true) {
            System.out.print("\n  Pseudonim (max 20 znaków): ");
            String nick = scanner.nextLine().trim();
            if (nick.isEmpty()) {
                System.out.println("  Pseudonim nie może być pusty!");
            } else if (nick.length() > 20) {
                System.out.println("  Za długi — maksymalnie 20 znaków.");
            } else {
                return nick;
            }
        }
    }

    private int askForAge() {
        while (true) {
            System.out.print("  Wiek (16-45): ");
            try {
                int age = Integer.parseInt(scanner.nextLine().trim());
                if (age < 16 || age > 45) {
                    System.out.println("  Wiek musi być między 16 a 45.");
                } else {
                    return age;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Podaj liczbę!");
            }
        }
    }

    private void distributeStartingPoints(Player player) {
        while (player.getStatPointsAvailable() > 0) {
            printStats(player);

            int choice = readInt();
            if (choice == 0 && player.getStatPointsAvailable() == 0) break;

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
                System.out.println("  Nieprawidłowy wybór — musisz rozdać wszystkie punkty.");
            }
        }
    }

    private void printStats(Player player) {
        System.out.println();
        System.out.println("+-----------------------------------------+");
        System.out.println("|  ROZDAJ PUNKTY STARTOWE                 |");
        System.out.printf("|  Zostało: %-30s|%n", player.getStatPointsAvailable() + " pkt");
        System.out.println("+-----------------------------------------+");
        System.out.println("  [1]  Siła          = " + player.getAttributes().getStrength()
                + tipFor(CharacterAttributeType.STRENGTH));
        System.out.println("  [2]  Obrona        = " + player.getAttributes().getDefense()
                + tipFor(CharacterAttributeType.DEFENSE));
        System.out.println("  [3]  Celność       = " + player.getAttributes().getAccuracy()
                + tipFor(CharacterAttributeType.ACCURACY));
        System.out.println("  [4]  Stamina       = " + player.getAttributes().getStamina()
                + tipFor(CharacterAttributeType.STAMINA));
        System.out.println("  [5]  Odwaga        = " + player.getAttributes().getBrave()
                + tipFor(CharacterAttributeType.BRAVE));
        System.out.println("  [6]  Cwaniactwo    = " + player.getAttributes().getCunning()
                + tipFor(CharacterAttributeType.CUNNING));
        System.out.println("  [7]  Szybkość      = " + player.getAttributes().getSpeed()
                + tipFor(CharacterAttributeType.SPEED));
        System.out.println("  [8]  Patologia     = " + player.getAttributes().getPathology()
                + tipFor(CharacterAttributeType.PATHOLOGY));
        System.out.println("  [9]  Honor         = " + player.getAttributes().getValor()
                + tipFor(CharacterAttributeType.VALOR));
        System.out.println("  [10] Znajomości    = " + player.getAttributes().getConnections()
                + tipFor(CharacterAttributeType.CONNECTIONS));
        System.out.print("  Wybór: ");
    }

    /**
     * Krótka podpowiedź co daje każdy atrybut — widoczna przy wyborze.
     */
    private String tipFor(CharacterAttributeType type) {
        return switch (type) {
            case STRENGTH    -> "  ← obrażenia wręcz";
            case DEFENSE     -> "  ← redukcja obrażeń";
            case ACCURACY    -> "  ← celność, ataki dystansowe";
            case STAMINA     -> "  ← max HP (" + "×10" + "), koszt ataków";
            case BRAVE       -> "  ← LeaderScream, szansa stunu";
            case CUNNING     -> "  ← ataki dystansowe (×0.6)";
            case SPEED       -> "  ← uniki, ruch";
            case PATHOLOGY   -> "  ← ???";
            case VALOR       -> "  ← tłum, CallToFriends";
            case CONNECTIONS -> "  ← rabat w sklepie (max 20%)";
        };
    }

    /**
     * Wybór ścieżki postaci — decyduje o stylu walki.
     * Kacper używa player.getPath() w atakach specjalnych.
     */
    private void choosePath(Player player) {
        System.out.println();
        System.out.println("+-----------------------------------------+");
        System.out.println("|  WYBIERZ ŚCIEŻKĘ POSTACI                |");
        System.out.println("+-----------------------------------------+");

        CharacterPath[] paths = CharacterPath.values();
        for (int i = 0; i < paths.length; i++) {
            System.out.printf("  [%d] %-10s — %s%n",
                    i + 1, paths[i].getDisplayName(), paths[i].getDescription());
        }

        while (true) {
            System.out.print("  Wybór (1-" + paths.length + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= paths.length) {
                    player.setPath(paths[choice - 1]);
                    System.out.println("  Wybrałeś: " + paths[choice - 1].getDisplayName());
                    return;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("  Nieprawidłowy wybór.");
        }
    }

    // -------------------------------------------------------
    // POMOCNICZE
    // -------------------------------------------------------

    private void printTitle() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        ARENA KIBOLÓW                 ║");
        System.out.println("║      -- Tworzenie postaci --         ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
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
}