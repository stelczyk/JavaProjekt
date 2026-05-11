package locations;

import game.constants.GameConstants;
import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Item;
import game.items.ItemCatalog;
import game.items.Weapon;
import game.player.Player;

import java.util.List;

public class Shop {

    private final List<Weapon> weaponsForSale;
    private final List<Clothing> clothingForSale;

    public Shop() {
        this.weaponsForSale = ItemCatalog.WEAPONS;
        this.clothingForSale = ItemCatalog.CLOTHING;
    }

    public List<Weapon> getWeaponsForSale() {
        return weaponsForSale;
    }

    public List<Clothing> getClothingForSale() {
        return clothingForSale;
    }

    public ShopResult buyItem(Player player, Item item) {

        if(player.getInventory().hasItem(item)) {
            return ShopResult.ALREADY_OWNED;
        }
        if (player.getLevel() < item.getRequiredLevel()) {
            return ShopResult.LEVEL_TOO_LOW;
        }
        int price = calculateDiscountedPrice(player, item);
        if (!player.spendMoney(price)) {
            return ShopResult.NOT_ENOUGH_MONEY;
        }
        player.getInventory().addItem(item);
        return ShopResult.SUCCESS;
    }

    public ShopResult sellItem(Player player, Item item) {
        if (!player.getInventory().hasItem(item)) {
            return ShopResult.ITEM_NOT_OWNED;
        }
        unequipIfNeeded(player, item);
        player.getInventory().removeItem(item);
        player.earnMoney(item.getSellPrice());
        return ShopResult.SUCCESS;
    }

    public ShopResult equipWeapon(Player player, Weapon weapon) {
        if (!player.getInventory().hasItem(weapon)) {
            return ShopResult.ITEM_NOT_OWNED;
        }
        player.getInventory().equipWeapon(weapon);
        return ShopResult.SUCCESS;
    }

    public ShopResult equipClothing(Player player, Clothing clothing) {
        if (!player.getInventory().hasItem(clothing)) {
            return ShopResult.ITEM_NOT_OWNED;
        }
        player.getInventory().equipClothing(clothing);
        return ShopResult.SUCCESS;
    }

    public void unequipWeapon(Player player) {
        player.getInventory().unequipWeapon();
    }

    public void unequipClothing(Player player, ClothingSlot slot) {
        player.getInventory().unequipClothing(slot);
    }

    // Znajomości powyżej wartości startowej (5) dają 1% rabatu za punkt, max 20%
    public int calculateDiscountedPrice(Player player, Item item) {
        int connectionsAboveBase = Math.max(0, player.getAttributes().getConnections() - GameConstants.DEFAULT_START_ATTRIBUTE_VALUE);
        double discount = Math.min(connectionsAboveBase * 0.01, 0.20);
        return (int) (item.getPrice() * (1 - discount));
    }

    // Sprzedanie założonego przedmiotu od razu go zdejmuje
    private void unequipIfNeeded(Player player, Item item) {
        if (item instanceof Weapon && item.equals(player.getInventory().getEquippedWeapon())) {
            player.getInventory().unequipWeapon();
        }
        if (item instanceof Clothing clothing &&
                clothing.equals(player.getInventory().getEquippedClothing(clothing.getSlot()))) {
            player.getInventory().unequipClothing(clothing.getSlot());
        }
    }
}