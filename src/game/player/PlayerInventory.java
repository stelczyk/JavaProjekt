package game.player;

import game.items.Clothing;
import game.items.ClothingSlot;
import game.items.Item;
import game.items.Weapon;

import java.util.*;


public class PlayerInventory {

    private final List<Item> ownedItems = new ArrayList<>();
    private Weapon equippedWeapon;
    private final Map<ClothingSlot, Clothing> equippedClothing = new EnumMap<>(ClothingSlot.class);

    public void addItem(Item item) {
        ownedItems.add(item);
    }

    public void removeItem(Item item) {
        ownedItems.remove(item);
    }

    public boolean hasItem(Item item) {
        return ownedItems.contains(item);
    }

    public List<Item> getOwnedItems() {
        return Collections.unmodifiableList(ownedItems);
    }

    public void equipWeapon(Weapon weapon){
        equippedWeapon = weapon;
    }

    public void unequipWeapon(){
        equippedWeapon = null;
    }

    public Weapon getEquippedWeapon(){
        return equippedWeapon;
    }

    public void equipClothing(Clothing clothing){
        equippedClothing.put(clothing.getSlot(), clothing);
    }

    public void unequipClothing(ClothingSlot slot){
        equippedClothing.remove(slot);
    }

    public Clothing getEquippedClothing(ClothingSlot slot){
        return equippedClothing.get(slot);
    }

    public Map<ClothingSlot, Clothing> getAllEquippedClothing(){
        return Collections.unmodifiableMap(equippedClothing);
    }

    public int getTotalDefenseBonus(){
        return equippedClothing.values().stream().mapToInt(Clothing::getDefenseBonus).sum();
    }

    public int getTotalSpeedBonus(){
        return equippedClothing.values().stream().mapToInt(Clothing::getSpeedBonus).sum();
    }
}