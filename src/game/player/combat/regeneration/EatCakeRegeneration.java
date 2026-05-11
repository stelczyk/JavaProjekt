package game.player.combat.regeneration;

import game.player.combat.state.FighterState;

/**
 * Regeneracja poprzez Ciastko (Eat Cake) - słodycz dla morale.
 *
 * - Umiarkowana regeneracja staminiy
 * - Solidny bonus HP (postać czuje się lepiej)
 * - Powolna metoda, ale dostępna zawsze
 */
public class EatCakeRegeneration extends AbstractRegeneratePlayer {

    public EatCakeRegeneration() {
        super(
            "🍰 Ciastko do jedzenia",
            30,  // Umiarkowana regeneracja staminiy
            20,  // Solidny HP (śmieszny bonus, ale uczucia się lepiej)
            "Słodziutkie ciastko przywracające energię ducha i ciała"
        );
    }

    @Override
    public void executeRegeneration(FighterState fighterState) {
        super.executeRegeneration(fighterState);
        System.out.println("  🍰 Gracz zjadł ciastko! Mmm, puszysty lukier... [Stamina: +" + staminaRestored + ", HP: +" + healthRestored + "]");
    }
}

