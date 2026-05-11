package game.player.combat.regeneration;

import game.player.combat.state.FighterState;

/**
 * Regeneracja poprzez Wstrzyknięcie (Injection) - medyczne zastrzyknięcie.
 *
 * - Szybka, masywna regeneracja staminiy
 * - Minimalny bonus HP
 * - Cooldown: możliwe tylko raz na kilka tur
 */
public class InjectionRegeneration extends AbstractRegeneratePlayer {

    public InjectionRegeneration() {
        super(
            "💉 Wstrzyknięcie",
            50,  // Duża regeneracja staminiy
            5,   // Minimalny HP
            "Medyczne zastrzykniecie przywracające energię i minimalnie zdolność do walki"
        );
    }

    @Override
    public void executeRegeneration(FighterState fighterState) {
        super.executeRegeneration(fighterState);
        System.out.println("  💉 Gracz wykonał zastrzyknięcie! [Stamina: +" + staminaRestored + ", HP: +" + healthRestored + "]");
    }
}

