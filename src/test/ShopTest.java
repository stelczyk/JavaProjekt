package test;

import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Weapon;
import game.player.Player;
import game.player.PlayerProfile;
import game.player.attributes.CharacterAttributeType;
import locations.Shop;
import locations.ShopResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopTest {

    private Shop shop;
    private Player player;
    private Weapon kastet;
    private Clothing czapka;

    @BeforeEach
    void setUp() {
        shop   = new Shop();
        player = new Player(new PlayerProfile("Tester", 20));
        kastet = new Weapon("Kastet", 80, 1, 2, 3);
        czapka = new Clothing("Czapka", 40, 1, 1, 1, ClothingSlot.GLOWA);
    }

    // -------------------------------------------------------
    // buyItem
    // -------------------------------------------------------

    @Test
    void kupno_sukces() {
        player.earnMoney(200);
        ShopResult result = shop.buyItem(player, kastet);
        assertEquals(ShopResult.SUCCESS, result);
        assertTrue(player.getInventory().hasItem(kastet));
    }

    @Test
    void kupno_odejmuje_pieniadze() {
        int kasaPrzed = player.getMoney();
        int cena = shop.calculateDiscountedPrice(player, kastet);
        shop.buyItem(player, kastet);
        assertEquals(kasaPrzed - cena, player.getMoney());
    }

    @Test
    void kupno_za_malo_kasy() {
        player.spendMoney(player.getMoney()); // wyzeruj kasę
        ShopResult result = shop.buyItem(player, kastet);
        assertEquals(ShopResult.NOT_ENOUGH_MONEY, result);
        assertFalse(player.getInventory().hasItem(kastet));
    }

    @Test
    void kupno_za_niski_level() {
        Weapon maczeta = new Weapon("Maczeta", 300, 5, 5, 2);
        player.earnMoney(1000);
        // gracz jest na level 1, maczeta wymaga level 5
        ShopResult result = shop.buyItem(player, maczeta);
        assertEquals(ShopResult.LEVEL_TOO_LOW, result);
        assertFalse(player.getInventory().hasItem(maczeta));
    }

    @Test
    void kupno_juz_masz() {
        player.earnMoney(200);
        shop.buyItem(player, kastet);
        ShopResult result = shop.buyItem(player, kastet); // drugi raz ten sam
        assertEquals(ShopResult.ALREADY_OWNED, result);
    }

    @Test
    void kupno_juz_masz_nie_odejmuje_kasy() {
        player.earnMoney(500);
        shop.buyItem(player, kastet);
        int kasaPo = player.getMoney();
        shop.buyItem(player, kastet); // drugi raz
        assertEquals(kasaPo, player.getMoney()); // kasa bez zmian
    }

    // -------------------------------------------------------
    // sellItem
    // -------------------------------------------------------

    @Test
    void sprzedaz_sukces() {
        player.earnMoney(200);
        shop.buyItem(player, kastet);
        ShopResult result = shop.sellItem(player, kastet);
        assertEquals(ShopResult.SUCCESS, result);
        assertFalse(player.getInventory().hasItem(kastet));
    }

    @Test
    void sprzedaz_zwraca_polowe_ceny() {
        player.earnMoney(200);
        shop.buyItem(player, kastet);
        int kasaPrzed = player.getMoney();
        shop.sellItem(player, kastet);
        assertEquals(kasaPrzed + kastet.getSellPrice(), player.getMoney());
    }

    @Test
    void sprzedaz_nie_masz_przedmiotu() {
        ShopResult result = shop.sellItem(player, kastet);
        assertEquals(ShopResult.ITEM_NOT_OWNED, result);
    }

    // -------------------------------------------------------
    // calculateDiscountedPrice
    // -------------------------------------------------------

    @Test
    void cena_bez_rabatu_przy_domyslnych_connections() {
        // Connections = 5 (domyślne DEFAULT_START_ATTRIBUTE_VALUE), brak rabatu
        int cena = shop.calculateDiscountedPrice(player, kastet);
        assertEquals(kastet.getPrice(), cena);
    }

    @Test
    void cena_z_rabatem_1_procent_za_punkt() {
        // każdy punkt Connections powyżej 5 = 1% rabatu
        player.getAttributes().upgradeAttribute(CharacterAttributeType.CONNECTIONS); // connections = 6
        int cena = shop.calculateDiscountedPrice(player, kastet);
        int oczekiwana = (int)(kastet.getPrice() * 0.99);
        assertEquals(oczekiwana, cena);
    }

    @Test
    void cena_max_rabat_20_procent() {
        // wbij connections wysoko — rabat nie może przekroczyć 20%
        for (int i = 0; i < 30; i++) {
            player.getAttributes().upgradeAttribute(CharacterAttributeType.CONNECTIONS);
        }
        int cena = shop.calculateDiscountedPrice(player, kastet);
        int minimum = (int)(kastet.getPrice() * 0.80);
        assertEquals(minimum, cena);
    }

    // -------------------------------------------------------
    // equipWeapon / equipClothing
    // -------------------------------------------------------

    @Test
    void zalozenie_broni_sukces() {
        player.earnMoney(200);
        shop.buyItem(player, kastet);
        ShopResult result = shop.equipWeapon(player, kastet);
        assertEquals(ShopResult.SUCCESS, result);
        assertEquals(kastet, player.getInventory().getEquippedWeapon());
    }

    @Test
    void zalozenie_broni_ktorej_nie_masz() {
        ShopResult result = shop.equipWeapon(player, kastet);
        assertEquals(ShopResult.ITEM_NOT_OWNED, result);
        assertNull(player.getInventory().getEquippedWeapon());
    }

    @Test
    void zalozenie_odziezy_sukces() {
        player.earnMoney(200);
        shop.buyItem(player, czapka);
        ShopResult result = shop.equipClothing(player, czapka);
        assertEquals(ShopResult.SUCCESS, result);
        assertEquals(czapka, player.getInventory().getEquippedClothing(ClothingSlot.GLOWA));
    }

    @Test
    void zalozenie_odziezy_ktorej_nie_masz() {
        ShopResult result = shop.equipClothing(player, czapka);
        assertEquals(ShopResult.ITEM_NOT_OWNED, result);
        assertNull(player.getInventory().getEquippedClothing(ClothingSlot.GLOWA));
    }

    @Test
    void zdjecie_broni() {
        player.earnMoney(200);
        shop.buyItem(player, kastet);
        shop.equipWeapon(player, kastet);
        shop.unequipWeapon(player);
        assertNull(player.getInventory().getEquippedWeapon());
    }

    // -------------------------------------------------------
    // isConsumable
    // -------------------------------------------------------

    @Test
    void zwykla_bron_nie_jest_jednorazowa() {
        assertFalse(kastet.isConsumable());
    }

    @Test
    void raca_jest_jednorazowa() {
        Weapon raca = new Weapon("Raca", 150, 3, 1, 4, true);
        assertTrue(raca.isConsumable());
    }
}
