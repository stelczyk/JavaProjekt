package game.player.combat.attack.ranged;

import game.player.combat.attack.AttackStyle;

public class MediumRangedAttack extends AbstractRangedAttack {

    @Override
    public String getAttackName() {
        return "Rzut Przedmiotem";
    }

    @Override
    public String getActionDescription() {
        // Opis informuje gracza że efektywność zależy od broni, ale nie ujawnia dokładnych liczb
        // Zachowuje element tajemnicy - gracz wie że broń ma znaczenie, ale nie zna dokładnych wartości
        return "Celny rzut przedmiotem z dystansu. Skuteczność i szansa trafienia zależą od użytego ekwipunku.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        // Medium/Normal - balans między mocą a precyzją
        // Odpowiada charakterowi ataków dystansowych które są bardziej taktyczne niż brutalne
        return AttackStyle.NORMAL;
    }

    @Override
    protected int getBaseStaminaCost() {
        // Rzut kosztuje mniej niż atak wręcz, ale wciąż wymaga wysiłku
        // 15 to wartość pomiędzy Quick Melee (10) a Medium Melee (20)
        return 15;
    }

    // NOTATKA DLA TEAMMATE ODPOWIEDZIALNEGO ZA SYSTEM BRONI:
    //
    // Należy nadpisać metodę calculateEquipmentContribution() w tej klasie lub w AbstractRangedAttack
    // aby zwracała wartość bazową broni (np. butelka: 15, kamień: 10, nóż: 25)
    //
    // Należy również nadpisać metodę getWeaponAccuracyModifier() aby zwracała współczynnik accuracy
    // (np. butelka: 0.7, kamień: 0.9, nóż: 0.6)
    //
    // Przykładowa implementacja:
    // @Override
    // protected double calculateEquipmentContribution() {
    //     return currentWeapon.getPower(); // np. 15 dla butelki
    // }
    //
    // @Override
    // protected double getWeaponAccuracyModifier() {
    //     return currentWeapon.getAccuracyModifier(); // np. 0.7 dla butelki
    // }
    //
    // System wyświetlania informacji o broni graczowi:
    // - Pokaż "Moc broni: 20x" (gdzie x to mnożnik, nie ujawniaj dokładnych damage)
    // - Pokaż "Szansa trafienia: Średnia/Wysoka/Niska" (zamiast dokładnych procentów)
    // Dzięki temu gracz ma informację zwrotną, ale zachowujemy element niepewności
}
