package game.player;

/**
 * Ścieżka postaci — wybierana raz podczas tworzenia, wpływa na styl walki.
 *
 * WOJOWNIK  → bonus do Siły i ataków wręcz
 * LIDER     → bonus do Odwagi, motywuje drużynę (LeaderScream)
 * CWANIAK   → bonus do Szybkości i ataków z dystansu
 *
 * [KACPER] Pobierz ścieżkę przez: player.getPath()
 * Przykład użycia w silniku walki:
 *
 *   switch (attacker.getPath()) {
 *       case WOJOWNIK -> baseDamage = (int)(baseDamage * 1.15);
 *       case LIDER    -> { // odblokuj LeaderScream w menu ataków }
 *       case CWANIAK  -> dodgeChance += 0.10;
 *   }
 */
public enum CharacterPath {

    WOJOWNIK("Wojownik",
            "Mistrz walki wręcz. Mocniejsze ciosy, lepsza odporność na ból."),

    LIDER("Lider",
            "Dowódca kibiców. Krzyczy rozkazy, zastrasza i motywuje."),

    CWANIAK("Cwaniak",
            "Sprytny ulicznik. Atakuje z zaskoczenia, unika ciosów.");

    private final String displayName;
    private final String description;

    CharacterPath(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
