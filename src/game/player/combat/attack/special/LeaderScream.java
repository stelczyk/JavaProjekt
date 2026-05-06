package game.player.combat.attack.special;

import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AttackStyle;
import game.player.combat.state.FighterState;

/**
 * Specjalny atak dostępny dla postaci idących ścieżką Przywódcy.
 * Gracz wydaje potężny, ogłuszający krzyk który może nie tylko zadać obrażenia, ale również
 * ogłuszyć przeciwnika, pozbawiając go możliwości ruchu w następnej turze.
 *
 * Unikalne cechy:
 * - Zadaje obrażenia POMIJAJĄCE standardową defensywę (mentalny, a nie fizyczny atak)
 * - Może ogłuszyć przeciwnika, odbierając mu turę (efekt probabilistyczny)
 * - Opiera się na Brave i Valor - cechach przywódczych
 * - Nie wymaga specjalnych warunków - można używać zawsze, ale ma wysoki koszt staminy
 */
public class LeaderScream extends AbstractSpecialAttack {

    // Wagi dla atrybutów przywódczych
    // BRAVE (odwaga) - główny czynnik, determinuje siłę krzyku
    private static final double BRAVE_WEIGHT = 1.5;

    // VALOR (honor/reputacja) - dodatkowy czynnik, wzmacnia psychologiczny wpływ
    private static final double VALOR_WEIGHT = 1.0;

    // Szansa bazowa na ogłuszenie (30%)
    // Będzie modyfikowana przez różnicę atrybutów między graczami
    private static final double BASE_STUN_CHANCE = 0.30;

    // Współczynnik wpływu Brave na szansę ogłuszenia (1% za każdy punkt)
    private static final double BRAVE_STUN_MODIFIER = 0.01;

    @Override
    public String getAttackName() {
        return "Przywódczy Ryk";
    }

    @Override
    public String getActionDescription() {
        return "Wydajesz potężny krzyk, który może ogłuszyć przeciwnika. " +
                "Twoja odwaga i reputacja przywódcy nadają temu atakowi psychologiczną moc, " +
                "która przebija standardową obronę. Może pozbawić wroga możliwości ruchu.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        // Strong style - krzyk jest potężny i wymaga pełnego zaangażowania
        // Dodatkowo high risk (niska accuracy) = high reward (możliwość ogłuszenia)
        return AttackStyle.STRONG;
    }

    @Override
    protected int getBaseStaminaCost() {
        // Potężny krzyk wymaga dużo energii mentalnej i fizycznej
        // Koszt 35 - między Medium Melee (20) a Strong Melee (40)
        return 35;
    }

    @Override
    protected double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker) {
        // Siła krzyku opiera się na cechach przywódczych:
        // 1. BRAVE (odwaga) - główny atrybut, determinuje jak przerażający jest krzyk
        // 2. VALOR (honor/reputacja) - psychologiczne wzmocnienie, strach przed przywódcą
        double braveValue = attacker.getBrave() * BRAVE_WEIGHT;
        double valorValue = attacker.getValor() * VALOR_WEIGHT;

        return braveValue + valorValue;
    }

    @Override
    public boolean canBeUsed(ArenaAttackPlayerAttributes attacker, FighterState state) {
        // Krzyk może być użyty zawsze, o ile gracz ma wystarczająco staminy
        // Nie wymaga specjalnych warunków - jest to podstawowa zdolność przywódcy
        return state.getCurrentStamina() >= getBaseStaminaCost();
    }

    @Override
    public void applySpecialEffects(FighterState state) {
        // Po użyciu ataku:
        // 1. Konsumuj staminy (wysiłek mental + fizyczny)
        state.consumeStamina(getBaseStaminaCost());

        // 2. Krzyk przywódcy elektrizuje publiczność
        // To demonstracja siły i dominacji - ludzie to uwielbiają
        state.addCrowdSatisfaction(20);

        // NOTATKA: Efekt ogłuszenia musi być obsłużony przez system walki
        // Tutaj tylko zapisujemy damage, ale system areny musi sprawdzić:
        // if (calculateStunChance(attacker) > random(0, 1)) {
        //     opponent.setStunned(true); // Przeciwnik traci następną turę
        // }
    }

    /**
     * Oblicza prawdopodobieństwo ogłuszenia przeciwnika.
     * Szansa bazowa to 30%, ale zwiększa się wraz z atrybutem Brave.
     *
     * NOTATKA DLA SYSTEMU WALKI:
     * Ta metoda powinna być wywołana po trafieniu atakiem.
     * Jeśli random(0, 1) < calculateStunChance(), przeciwnik jest ogłuszony.
     *
     * @param attacker atrybuty atakującego
     * @return szansa na ogłuszenie (0.0 - 1.0)
     */
    public double calculateStunChance(ArenaAttackPlayerAttributes attacker) {
        // Szansa rośnie wraz z Brave: 30% base + 1% za każdy punkt Brave
        // Przy 50 Brave = 30% + 50% = 80% szansy na ogłuszenie
        double stunChance = BASE_STUN_CHANCE + (attacker.getBrave() * BRAVE_STUN_MODIFIER);

        // Maksymalna szansa to 90% - zawsze jest minimalne ryzyko że się nie uda
        return Math.min(stunChance, 0.90);
    }

    @Override
    public int getCrowdAppeal() {
        // Krzyk przywódcy to spektakularna demonstracja dominacji
        // Publiczność uwielbia takie pokazy siły charakteru
        return 35; // Wysoki crowd appeal
    }

    // WAŻNA NOTATKA DLA SYSTEMU WALKI:
    //
    // Ten atak ma unikalną cechę: POMIJA STANDARDOWĄ DEFENSYWĘ
    // W systemie walki, przy obliczaniu finalnych obrażeń, należy:
    //
    // if (attack instanceof LeaderScream) {
    //     // NIE odejmuj defense/armor - to atak psychologiczny
    //     finalDamage = rawDamage;
    //
    //     // Sprawdź efekt ogłuszenia
    //     LeaderScream scream = (LeaderScream) attack;
    //     double stunChance = scream.calculateStunChance(attacker);
    //     if (Math.random() < stunChance) {
    //         defender.setStunned(true); // Dodaj status ogłuszenia
    //     }
    // }
    //
    // Status "stunned" powinien być sprawdzany na początku tury:
    // if (player.isStunned()) {
    //     player.setStunned(false); // Usuń status
    //     return; // Gracz traci turę
    // }
}
