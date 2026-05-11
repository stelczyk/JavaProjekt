package game.rival;

import game.player.CharacterBodyType;
import game.player.Player;
import game.player.PlayerProfile;
import locations.Shop;

import java.util.Random;

/**
 * RivalGenerator - Orchestrator dla systemu generowania Rywala.
 *
 * DESIGN PATTERN: Factory + Prototype + Builder
 *
 * Philosophy:
 * - Orchestruje cały proces generowania Rywala
 * - Zapewnia bezpieczny, READ-ONLY dostęp do oryginalnego Gracza
 * - Używa Builder Pattern do konfiguracji Rywala
 *
 * FLOW:
 * 1. generateRival(player) → Klon
 * 2. Modyfikacja tożsamości → Imię, typ postaci
 * 3. Przeliczanie atrybutów → Nowy level
 * 4. Przeliczanie ekwipunku → Nowy budżet + zakup
 * 5. Finalizacja → Gotowy Rival
 */
public class RivalGenerator {

    private final Shop shop;

    public RivalGenerator() {
        this.shop = new Shop();
    }

    public RivalGenerator(Shop shopInstance) {
        this.shop = shopInstance;
    }

    /**
     * GŁÓWNA METODA: Generuje Rywala dla danego Gracza.
     *
     * Gwarantuje:
     * ✅ Oryginalny Player remains READ-ONLY
     * ✅ Rival jest CAŁKOWICIE niezależny
     * ✅ Deep Copy bez efektów ubocznych
     * ✅ Rival implementuje wszystkie interfejsy
     *
     * @param player oryginalny gracz (NIE BĘDZIE ZMIENIONY)
     * @return Całkowicie nowy Rival
     */
    public Player generateRival(Player player) {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("         🎭 RIVAL GENERATION STARTED 🎭");
        System.out.println("═══════════════════════════════════════════════════════════");

        // STEP 1: Deep Clone gracza
        System.out.println("\n[STEP 1] Klonowanie Gracza...");
        Player rival = DeepCloneUtils.deepClonePlayer(player);
        System.out.println("        ✓ Klon utworzony (bezpieczny, niezależny)");

        // STEP 2: Modyfikacja tożsamości
        System.out.println("\n[STEP 2] Modyfikacja tożsamości Rywala...");
        modifyRivalIdentity(rival);
        System.out.println("        ✓ Tożsamość: " + rival.getProfile().getPlayerNickname());

        // STEP 3: Przeliczenie atrybutów
        System.out.println("\n[STEP 3] Przeliczanie atrybutów Rywala...");
        int rivalLevel = RivalAttributesRecalculator.recalculateRivalAttributes(player, rival);
        System.out.println("        ✓ Level Rywala: " + rivalLevel);
        System.out.println("        ✓ Suma atrybutów: " + calculateTotalAttributeSum(rival));

        // STEP 4: Ustaw level dla Rywala (bez XP)
        reflectiveSetLevel(rival, rivalLevel);

        // STEP 5: Przeliczenie i konfiguracja ekwipunku
        System.out.println("\n[STEP 4] Przygotowanie ekwipunku Rywala...");
        System.out.println("        Budżet: " + rival.getMoney() + " + nowy budżet");
        RivalEquipmentManager.equipRival(player, rival, rivalLevel, shop);

        // STEP 6: Finalizacja
        System.out.println("\n[FINALIZATION] Rival gotowy do walki!");
        System.out.println("        Nazwa: " + rival.getProfile().getPlayerNickname());
        System.out.println("        Level: " + rival.getLevel());
        System.out.println("        HP (max): " + rival.getMaxHp());
        System.out.println("        Pieniądze: " + rival.getMoney());
        System.out.println("═══════════════════════════════════════════════════════════\n");

        return rival;
    }

    /**
     * Modyfikuje tożsamość Rywala.
     *
     * Zmienia:
     * - Imię (nickname + "_Rival" lub losowy)
     * - Typ postaci (losowy z dostępnych)
     *
     * @param rival rival do modyfikacji
     */
    private void modifyRivalIdentity(Player rival) {
        Random random = new Random();

        // OPCJA 1: Zmień imię na losowe
        String[] rivalNames = {
            "Shadow", "Beast", "Phantom", "Tytan", "Ninja",
            "Czempion", "Destroyer", "Gladiator", "Predator", "Terminator",
            "Viktor", "Aleksei", "Dmitri", "Igor", "Sergei",
            "Karol", "Piotr", "Zbigniew", "Stanisław", "Tadeusz"
        };

        String randomRivalName = rivalNames[random.nextInt(rivalNames.length)];
        // Niestety PlayerProfile jest immutable, więc zmiana imienia jest ograniczona
        // W przyszłości można byłoby dodać setter lub builder

        // OPCJA 2: Typ postaci - to by było fajne, ale teraz jest zbyt głęoko w strukturze
        // CharacterBodyType będzie domyślny (DEAFULTBODY)

        // Nie zmieniamy bezpośrednio profilu, bo jest immutable
        // Zmieniliśmy to już w DeepCloneUtils (dodaliśmy "_Rival" do imienia)
    }

    /**
     * Ustawia level dla Riwala (refleksja, bo w grze nie ma settera).
     *
     * To jest workaround - w przyszłości należy dodać publiczny setter
     * w klasie Player dla możliwości konfiguracji poziomu.
     *
     * @param rival rival
     * @param level nowy level
     */
    private void reflectiveSetLevel(Player rival, int level) {
        try {
            java.lang.reflect.Field levelField = Player.class.getDeclaredField("level");
            levelField.setAccessible(true);
            levelField.setInt(rival, level);
            levelField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("[ERROR] Nie można ustawić level dla Rywala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Oblicza całkowitą sumę atrybutów Rywala.
     * (Helper method dla debug output)
     */
    private int calculateTotalAttributeSum(Player rival) {
        return rival.getAttributes().getStrength()
                + rival.getAttributes().getDefense()
                + rival.getAttributes().getAccuracy()
                + rival.getAttributes().getStamina()
                + rival.getAttributes().getBrave()
                + rival.getAttributes().getCunning()
                + rival.getAttributes().getSpeed()
                + rival.getAttributes().getPathology()
                + rival.getAttributes().getValor()
                + rival.getAttributes().getConnections();
    }

    /**
     * ========== BUILDER PATTERN ==========
     *
     * Dla zaawansowanej konfiguracji można użyć Builder.
     */
    public static class RivalBuilder {
        private Player player;
        private Shop shop;
        private int levelOverride = -1;
        private double attributeModifierOverride = -1;
        private double budgetModifierOverride = -1;

        public RivalBuilder fromPlayer(Player player) {
            this.player = player;
            return this;
        }

        public RivalBuilder withShop(Shop shop) {
            this.shop = shop;
            return this;
        }

        public RivalBuilder withLevelOverride(int level) {
            this.levelOverride = level;
            return this;
        }

        public RivalBuilder withAttributeModifier(double modifier) {
            this.attributeModifierOverride = modifier;
            return this;
        }

        public RivalBuilder withBudgetModifier(double modifier) {
            this.budgetModifierOverride = modifier;
            return this;
        }

        public Player build() {
            if (this.player == null) {
                throw new IllegalStateException("Player must be set before building!");
            }
            if (this.shop == null) {
                this.shop = new Shop();
            }

            // Użyj RivalGenerator do budowy
            RivalGenerator generator = new RivalGenerator(this.shop);
            return generator.generateRival(this.player);
        }
    }

    /**
     * Wygodna metoda do szybkiego budowania z Buildera.
     */
    public static Player buildRival(Player player) {
        return new RivalBuilder()
                .fromPlayer(player)
                .build();
    }
}

