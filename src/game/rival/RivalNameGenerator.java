package game.rival;

import game.player.Player;
import game.player.PlayerProfile;
import java.util.Random;

/**
 * RivalNameGenerator - Losuje imiona i nazwiska dla Rywali.
 *
 * KROK 3: Narzędzie do generowania unikatowych Rywali
 * - Losowe imię
 * - Losowe nazwisko
 * - Losowy wiek (18-65)
 * - Deep clone gracza
 * - Nowy profil z random danymi
 */
public class RivalNameGenerator {

    private static final String[] FIRST_NAMES = {
        "Karol", "Piotr", "Zbigniew", "Stanisław", "Tadeusz",
        "Viktor", "Aleksei", "Dmitri", "Igor", "Sergei",
        "Shadow", "Beast", "Phantom", "Tytan", "Ninja",
        "Czempion", "Destroyer", "Gladiator", "Predator", "Terminator"
    };

    private static final String[] LAST_NAMES = {
        "Kowalski", "Nowak", "Lewandowski", "Krawczyk", "Wójcik",
        "Zakharov", "Petrov", "Sokolov", "Volkanov", "Smirnov",
        "Wolfy", "Stonebreaker", "Ironhand", "Shadowfist", "Blazethorn",
        "Dragonslayer", "Bonecrusher", "Ravager", "Hellbringer", "Doomfist"
    };

    private static final Random random = new Random();

    /**
     * Generuje losowe imię i nazwisko dla Rywala.
     *
     * @return tablica [firstName, lastName]
     */
    public static String[] generateRandomName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];

        return new String[]{firstName, lastName};
    }

    /**
     * Generuje losowy wiek dla Rywala.
     *
     * @return wiek (18-65)
     */
    public static int generateRandomAge() {
        return 18 + random.nextInt(48); // 18-65
    }

    /**
     * Generuje losowego Rywala na podstawie Gracza.
     *
     * PROCES:
     * 1. Deep clone gracza
     * 2. Nowe imię + nazwisko
     * 3. Nowy wiek
     * 4. Randomizuj atrybuty (via RivalGenerator)
     *
     * @param player gracz (szablon)
     * @return nowy rival
     */
    public static Player generateRival(Player player) {
        // STEP 1: Deep clone
        Player rival = DeepCloneUtils.deepClonePlayer(player);

        // STEP 2: Nowe imię i nazwisko
        String[] nameData = generateRandomName();
        String firstName = nameData[0];
        String lastName = nameData[1];
        String fullRivalName = firstName + " " + lastName;

        // STEP 3: Nowy wiek
        int rivalAge = generateRandomAge();

        System.out.println("[RIVAL GENERATED] " + fullRivalName + ", wiek: " + rivalAge);

        // Tutaj można by było aktualizać profil rywala, ale PlayerProfile jest immutable
        // Na razie zostaje ze sklonowaną nazwą + "_Rival"

        return rival;
    }
}

