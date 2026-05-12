package test;

import game.constants.GameConstants;
import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Weapon;
import game.player.CharacterPath;
import game.player.Player;
import game.player.PlayerProfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(new PlayerProfile("Tester", 20));
    }

    // -------------------------------------------------------
    // gainXp / levelUp
    // -------------------------------------------------------

    @Test
    void level_up_przy_150_xp() {
        // level 1 wymaga 1 * 150 = 150 XP
        player.gainXp(150);
        assertEquals(2, player.getLevel());
    }

    @Test
    void level_up_daje_4_punkty_statystyk() {
        int punktyPrzed = player.getStatPointsAvailable();
        player.gainXp(150);
        assertEquals(punktyPrzed + GameConstants.DEFAULT_STAT_POINTS_TO_DISTRIBUTE,
                     player.getStatPointsAvailable());
    }

    @Test
    void dwa_level_upy_naraz() {
        // XP liczy się od początku, nie od ostatniego levelu:
        // level 1→2: totalXP >= 1*150 = 150
        // level 2→3: totalXP >= 2*150 = 300
        // level 3→4: totalXP >= 3*150 = 450  ← NIE chcemy tego
        // więc 300 XP = dokładnie level 3
        player.gainXp(300);
        assertEquals(3, player.getLevel());
    }

    @Test
    void brak_level_up_przy_za_malo_xp() {
        player.gainXp(149);
        assertEquals(1, player.getLevel());
    }

    @Test
    void xp_nie_przekracza_limitu() {
        player.gainXp(GameConstants.MAX_XP_LIMIT + 9999);
        assertEquals(GameConstants.MAX_XP_LIMIT, player.getXp());
    }

    // -------------------------------------------------------
    // spendStatPoint
    // -------------------------------------------------------

    @Test
    void wydanie_punktu_zmniejsza_licznik() {
        int przed = player.getStatPointsAvailable();
        player.spendStatPoint();
        assertEquals(przed - 1, player.getStatPointsAvailable());
    }

    @Test
    void punkty_nie_schodza_ponizej_zera() {
        int ile = player.getStatPointsAvailable();
        for (int i = 0; i < ile + 5; i++) {
            player.spendStatPoint();
        }
        assertEquals(0, player.getStatPointsAvailable());
    }

    // -------------------------------------------------------
    // initLevel — używane przez Kacpra w RivalGenerator
    // -------------------------------------------------------

    @Test
    void init_level_ustawia_poprawnie() {
        player.initLevel(10);
        assertEquals(10, player.getLevel());
    }

    @Test
    void init_level_nie_ustawia_ponizej_1() {
        player.initLevel(0);
        assertEquals(1, player.getLevel()); // zostaje 1
    }

    @Test
    void init_level_nie_ustawia_powyzej_max() {
        player.initLevel(GameConstants.MAX_PLAYER_LEVEL + 10);
        assertEquals(1, player.getLevel()); // nie zmienia się
    }

    @Test
    void init_level_na_granicy_max() {
        player.initLevel(GameConstants.MAX_PLAYER_LEVEL);
        assertEquals(GameConstants.MAX_PLAYER_LEVEL, player.getLevel());
    }

    // -------------------------------------------------------
    // CharacterPath — używane przez Kacpra w ArenaCombatEngine
    // -------------------------------------------------------

    @Test
    void domyslna_sciezka_to_wojownik() {
        assertEquals(CharacterPath.WOJOWNIK, player.getPath());
    }

    @Test
    void zmiana_sciezki_na_lidera() {
        player.setPath(CharacterPath.LIDER);
        assertEquals(CharacterPath.LIDER, player.getPath());
    }

    @Test
    void zmiana_sciezki_na_cwaniaka() {
        player.setPath(CharacterPath.CWANIAK);
        assertEquals(CharacterPath.CWANIAK, player.getPath());
    }

    // -------------------------------------------------------
    // pieniądze
    // -------------------------------------------------------

    @Test
    void earn_money_dodaje_do_stanu() {
        int przed = player.getMoney();
        player.earnMoney(100);
        assertEquals(przed + 100, player.getMoney());
    }

    @Test
    void spend_money_odejmuje_od_stanu() {
        player.spendMoney(100);
        assertEquals(GameConstants.DEFAULT_STARTING_MONEY - 100, player.getMoney());
    }

    @Test
    void spend_money_zwraca_true_gdy_starcza() {
        assertTrue(player.spendMoney(100));
    }

    @Test
    void spend_money_zwraca_false_gdy_za_malo() {
        assertFalse(player.spendMoney(player.getMoney() + 1));
    }

    @Test
    void spend_money_nie_odejmuje_gdy_za_malo() {
        int przed = player.getMoney();
        player.spendMoney(przed + 1);
        assertEquals(przed, player.getMoney()); // stan bez zmian
    }

    // -------------------------------------------------------
    // PlayerInventory — podstawowe operacje
    // -------------------------------------------------------

    @Test
    void dodanie_przedmiotu_do_plecaka() {
        Weapon kastet = new Weapon("Kastet", 80, 1, 2, 3);
        player.getInventory().addItem(kastet);
        assertTrue(player.getInventory().hasItem(kastet));
    }

    @Test
    void usuniecie_przedmiotu_z_plecaka() {
        Weapon kastet = new Weapon("Kastet", 80, 1, 2, 3);
        player.getInventory().addItem(kastet);
        player.getInventory().removeItem(kastet);
        assertFalse(player.getInventory().hasItem(kastet));
    }

    @Test
    void bonus_obrony_z_kilku_czesci_odziezy() {
        Clothing czapka  = new Clothing("Czapka",  40, 1, 2, 0, ClothingSlot.GLOWA);
        Clothing bluza   = new Clothing("Bluza",   80, 1, 3, 0, ClothingSlot.TULOWIE);
        player.getInventory().addItem(czapka);
        player.getInventory().addItem(bluza);
        player.getInventory().equipClothing(czapka);
        player.getInventory().equipClothing(bluza);
        assertEquals(5, player.getInventory().getTotalDefenseBonus()); // 2 + 3
    }

    @Test
    void max_hp_rosnie_ze_stamina() {
        int hpPrzed = player.getMaxHp();
        player.getAttributes().upgradeAttribute(
            game.player.attributes.CharacterAttributeType.STAMINA);
        assertTrue(player.getMaxHp() > hpPrzed);
    }
}
