package game.player.combat.regeneration;

import game.player.combat.state.FighterState;

/**
 * Regeneracja poprzez Napój Energetyczny (Drink Energy Drink) - stimulant.
 *
 * - Masywna regeneracja staminiy
 * - Brak regeneracji HP (czysty stimulant, nie leczy)
 * - Szybka, ale riskancka metoda
 */
public class DrinkEnergyDrinkRegeneration extends AbstractRegeneratePlayer {

    public DrinkEnergyDrinkRegeneration() {
        super(
            "⚡ Napój energetyczny",
            60,  // Masywna regeneracja staminiy
            0,   // Brak HP - to tylko stimulant!
            "Energetyczny napój elektryzujący ciało i umysł. Biorze wszystko z siebie!"
        );
    }

    @Override
    public void executeRegeneration(FighterState fighterState) {
        super.executeRegeneration(fighterState);
        System.out.println("  ⚡ Gracz wyciągnął napój energetyczny! WSHHH! [Stamina: +" + staminaRestored + "]");
    }
}

