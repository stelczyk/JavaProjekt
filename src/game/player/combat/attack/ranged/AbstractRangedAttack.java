package game.player.combat.attack.ranged;

import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.state.FighterState;

public abstract class AbstractRangedAttack extends AbstractAttack {

    // Wagi dla atrybutów w atakach dystansowych
    // Accuracy jest kluczowa dla trafienia z dystansu, ale cunning pozwala znaleźć słabe punkty
    private static final double ACCURACY_WEIGHT = 0.8;  // Celność oddaje 80% swojej wartości
    private static final double CUNNING_WEIGHT = 0.6;   // Przebiegłość dodaje 60% - spryt w wyborze celu
    private static final double STAMINA_WEIGHT = 0.2;   // Ataki dystansowe są mniej wyczerpujące fizycznie

    @Override
    protected double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker) {
        // Dla Ranged głównym atrybutem jest ACCURACY (celność rzutu)
        // Dodatkowo CUNNING (przebiegłość) pozwala lepiej ocenić gdzie i kiedy rzucić
        return (attacker.getAccuracy() * ACCURACY_WEIGHT) + (attacker.getCunning() * CUNNING_WEIGHT);
    }

    @Override
    protected double calculateStaminaContribution(FighterState state) {
        // Rzut przedmiotem zużywa mniej staminy niż cios wręcz, ale wciąż wymaga wysiłku
        return state.getCurrentStamina() * STAMINA_WEIGHT;
    }

    @Override
    protected double calculateEquipmentContribution() {
        // MIEJSCE NA LOGIKĘ BRONI OD TEAMMATE
        // Tutaj powinna być implementacja systemu broni:
        // - Różne przedmioty (butelka, kamień, nóż) mają różne statystyki
        // - Każda broń ma swoją bazową moc (np. butelka: 15, kamień: 10, nóż: 25)
        // - Każda broń ma współczynnik accuracy (butelka: 0.7, kamień: 0.9, nóż: 0.6)
        // - Zalecane: enum WeaponType z metodami getPower() i getAccuracyModifier()
        // - Metoda powinna zwracać weaponType.getPower()
        return 0.0;
    }

    // Metoda pomocnicza dla teammate - zwraca współczynnik accuracy broni
    // Będzie wykorzystywana w calculateAccuracy() w przyszłości
    protected double getWeaponAccuracyModifier() {
        // MIEJSCE NA LOGIKĘ WSPÓŁCZYNNIKA ACCURACY BRONI
        // Tutaj teammate powinien zwrócić weaponType.getAccuracyModifier()
        // Przykład: butelka = 0.7 (trudna do rzucenia), kamień = 0.9 (łatwy do rzucenia)
        return 1.0; // Domyślnie brak modyfikacji
    }
}
