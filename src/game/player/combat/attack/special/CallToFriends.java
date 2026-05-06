package game.player.combat.attack.special;

import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.combat.attack.AttackStyle;
import game.player.combat.special.SpecialAbilityProgress;
import game.player.combat.state.FighterState;

/**
 * Specjalny atak dostępny dla postaci idących ścieżką Confidenta.
 * Gracz dzwoni do swoich znajomych z przestępczego świata, którzy interweniują w walce.
 *
 * Mechanika:
 * - Wymaga pełnego paska postępu (ładowanego poprzez Valor)
 * - Siła ataku zależy od Connections (znajomości w świecie przestępczym)
 * - Po użyciu pasek resetuje się do zera
 * - Atak jest potężny ale dostępny rzadko - strategiczne użycie jest kluczowe
 */
public class CallToFriends extends AbstractSpecialAttack {

    // Waga dla atrybutu Connections - główny czynnik mocy tego ataku
    // Im więcej znajomości, tym potężniejsza jest interwencja przyjaciół
    private static final double CONNECTIONS_WEIGHT = 2.0;

    // Waga dla Valor - odwaga również wpływa na efektywność wezwania pomocy
    // Pewny siebie przestępca potrafi lepiej wykorzystać swoich ludzi
    private static final double VALOR_WEIGHT = 0.5;

    // Referencja do systemu paska postępu - każda instancja postaci powinna mieć własny
    private final SpecialAbilityProgress abilityProgress;

    public CallToFriends(SpecialAbilityProgress abilityProgress) {
        this.abilityProgress = abilityProgress;
    }

    @Override
    public String getAttackName() {
        return "Wezwanie Ekipy";
    }

    @Override
    public String getActionDescription() {
        return "Dzwonisz do swoich ludzi z miasta. Twoja reputacja i znajomości sprawiają, że natychmiast ruszą ci na pomoc. " +
                "Wymaga pełnego paska odwagi.";
    }

    @Override
    protected AttackStyle getAttackStyle() {
        // Normalny styl - nie jest to atak szybki ani specjalnie wolny
        // Interwencja przyjaciół następuje szybko ale nie jest bezmyślnie brutalna
        return AttackStyle.NORMAL;
    }

    @Override
    protected int getBaseStaminaCost() {
        // Zadzwonienie do kogoś nie wymaga wysiłku fizycznego
        // Minimalny koszt 5 reprezentuje stres psychiczny
        return 5;
    }

    @Override
    protected double calculatePrimaryAttributeContribution(ArenaAttackPlayerAttributes attacker) {
        // Siła tego ataku opiera się na dwóch atrybutach:
        // 1. CONNECTIONS (znajomości) - główny czynnik, determinuje KTO przychodzi z pomocą
        //    Im więcej connections, tym potężniejsi ludzie odpowiadają na wezwanie
        // 2. VALOR (odwaga) - dodatkowy czynnik, pokazuje jak pewnie gracz wykorzystuje swoich ludzi
        double connectionsValue = attacker.getConnections() * CONNECTIONS_WEIGHT;
        double valorValue = attacker.getValor() * VALOR_WEIGHT;

        return connectionsValue + valorValue;
    }

    @Override
    public boolean canBeUsed(ArenaAttackPlayerAttributes attacker, FighterState state) {
        // Atak może być użyty tylko gdy pasek postępu jest pełny
        // To gwarantuje że będzie to rzadka, ale potężna opcja strategiczna
        return abilityProgress.isAbilityReady();
    }

    @Override
    public void applySpecialEffects(FighterState state) {
        // Po użyciu ataku:
        // 1. Konsumuj minimalną ilość staminy (wysiłek psychiczny)
        state.consumeStamina(getBaseStaminaCost());

        // 2. RESETUJ pasek postępu - gracz musi ponownie go naładować
        abilityProgress.resetProgress();

        // 3. Interwencja przyjaciół wzbudza ogromne emocje na trybunach
        // Publiczność uwielbia dramatyczne zwroty akcji
        state.addCrowdSatisfaction(30);
    }

    @Override
    public int getCrowdAppeal() {
        // Wezwanie pomocy to kontrowersyjna akcja:
        // - Z jednej strony ekscytująca (duży crowd appeal)
        // - Z drugiej strony niehonorowa (ale to już część narracji, nie mechaniki)
        return 40; // Bardzo wysoki crowd appeal - publiczność kocha dramaty
    }

    // NOTATKA DLA SYSTEMU WALKI:
    // Pasek postępu powinien ładować się automatycznie w każdej turze:
    // int progressGain = abilityProgress.calculateProgressGain(player.getValor(), opponent.getLevel());
    // abilityProgress.increaseProgress(progressGain);
    //
    // To zapewni że gracze z wysokim Valor będą mogli używać tej zdolności częściej
}
