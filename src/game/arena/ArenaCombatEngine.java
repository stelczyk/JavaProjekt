package game.arena;

import game.items.Weapon;
import game.player.CharacterPath;
import game.player.Player;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.attack.melee.AbstractMeleeAttack;
import game.player.combat.attack.melee.QuickMeleeAttack;
import game.player.combat.attack.melee.MediumMeleeAttack;
import game.player.combat.attack.melee.StrongMeleeAttack;
import game.player.combat.attack.special.LeaderScream;
import game.player.combat.movement.Move;
import game.player.combat.movement.MovementDirection;
import game.player.combat.movement.Step;
import game.player.combat.movement.Jump;
import game.player.combat.regeneration.AbstractRegeneratePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * ArenaCombatEngine - Nowy system walki TUROWY (NOT SIMULATION).
 *
 * ARCHITEKTURA:
 * - Gracz podejmuje decyzje przez terminal (ArenaInputHandler)
 * - Komputer (AI) podejmuje decyzje automatycznie
 * - Turowa walka: Gracz → Komputer → Gracz...
 * - Pokazanie każdego ruchu w logach
 * - Limitowana liczba tur (~20-30 max)
 */
public class ArenaCombatEngine {

    private final CombatMode mode;
    private final Scanner scanner;
    private final ArenaInputHandler inputHandler;
    private final AIDecisionSystem aiSystem;
    private final DamageCalculator damageCalc;
    private final AudienceBar audienceBar;
    private final Random random = new Random();

    // Stany walki
    private ArenaPosition position;
    private ArenaFighter playerFighter;
    private ArenaFighter rivalFighter;
    private ArenaFighterState playerState;
    private ArenaFighterState rivalState;

    // Listy ruchów i ataków — inicjalizowane raz na walkę w startMatch()
    private List<AbstractAttack> rivalAttacks;
    private List<Move> rivalMoves;

    private int turnCount = 0;
    private boolean matchActive = true;
    private Player winner;

    // Śledzenie do FightResult i statystyk
    private int playerDamageDealt = 0;
    private int rivalDamageDealt  = 0;
    private int rewardMoney       = 0;

    private static final int MAX_TURNS = 50;
    private static final int TURN_LIMIT_WARNING = 30;

    // Bonusy ścieżek - czytelne stałe zamiast magic numbers
    private static final double WOJOWNIK_MELEE_DAMAGE_BONUS = 1.15; // +15%
    private static final int    CWANIAK_DODGE_PENALTY       = 10;   // -10 do hitChance

    /**
     * Konstruktor - inicjalizacja silnika walki.
     *
     * @param mode tryb walki (SPARRING lub TOURNAMENT)
     * @param scanner dla inputu gracza
     * @param audienceBar dla zarządzania widownią
     */
    public ArenaCombatEngine(CombatMode mode, Scanner scanner, AudienceBar audienceBar) {
        this.mode = mode;
        this.scanner = scanner;
        this.audienceBar = audienceBar;
        this.inputHandler = new ArenaInputHandler(scanner);
        this.aiSystem = new AIDecisionSystem();
        this.damageCalc = new DamageCalculator();
    }

    /**
     * Główna metoda - rozpoczyna walkę.
     */
    public FightResult startMatch(Player player, Player rival) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          🏛️  ARENA COMBAT (" + mode.getDescription() + ")  🏛️          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // STEP 1: Inicjalizacja
        this.playerFighter = new ArenaFighter(player);
        this.rivalFighter = new ArenaFighter(rival);
        this.playerState = playerFighter.getState();
        this.rivalState = rivalFighter.getState();
        this.position = new ArenaPosition();
        this.rivalAttacks = initializeAttacks();
        this.rivalMoves   = initializeMoves();

        // STEP 2: Inicjatywa
        InitiativeSystem initiative = new InitiativeSystem();
        boolean playerFirst = initiative.rollInitiative(player, rival);
        System.out.println("[INITIATIVE] " + (playerFirst ? "🟢 " + player.getProfile().getPlayerNickname() + " rozpoczyna!" : "🔴 " + rival.getProfile().getPlayerNickname() + " rozpoczyna!"));
        System.out.println("[POSITION] " + position.toString());
        System.out.println("[AUDIENCE] " + audienceBar.toString());
        System.out.println();

        // STEP 3: Główna pętla walki
        executeMatchLoop(playerFirst);

        // STEP 4: Zakończenie
        determineWinner();

        // STEP 5: Zbuduj wynik dla frontendu/Main2 + zaktualizuj statystyki gracza
        return buildAndApplyResult(player);
    }

    /**
     * Buduje FightResult na podstawie stanu po walce i aktualizuje statystyki gracza.
     */
    private FightResult buildAndApplyResult(Player player) {
        boolean playerKO = !playerState.isAlive();
        boolean rivalKO  = !rivalState.isAlive();

        FightResult.Outcome outcome;
        if (winner == player) {
            outcome = FightResult.Outcome.PLAYER_WIN;
            player.recordWin();
        } else if (winner != null) {
            outcome = FightResult.Outcome.PLAYER_LOSS;
            player.recordLoss();
        } else {
            outcome = FightResult.Outcome.DRAW;
        }

        player.addDamageDealt(playerDamageDealt);
        if (rivalKO) player.recordKnockout();

        return new FightResult(
                outcome, winner, turnCount, rewardMoney,
                playerDamageDealt, rivalDamageDealt,
                playerKO, rivalKO
        );
    }

    /**
     * Główna pętla walki - turnie aż do zwycięstwa.
     * TURA = Ruch Gracza + Ruch Rywala (lub odwrotnie, zależnie od inicjatywy)
     * Inicjatywa określa kolejność WEWNĄTRZ tury, a nie między turami!
     */
    private void executeMatchLoop(boolean playerFirst) {
        boolean currentPlayerTurn = playerFirst;

        while (matchActive && turnCount < MAX_TURNS) {
            turnCount++;

            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.printf("║ TURA %d  |  Gracz: %d/%d HP  |  Rywal: %d/%d HP          ║\n",
                turnCount,
                playerState.getCurrentHp(), playerState.getMaxHp(),
                rivalState.getCurrentHp(), rivalState.getMaxHp());
            System.out.println("╚════════════════════════════════════════════════════════════╝");

            // Wyświetl pozycje bezwzględne
            System.out.println("[POZYCJE] Gracz: " + position.getPlayerPosition() +
                              " | Rywal: " + position.getRivalPosition() +
                              " | Dystans: " + position.getDistance());
            System.out.println("[PUBLICZNOŚĆ] " + audienceBar.getBarVisualization());

            // ===== FAZA 1: GRACZ (lub RYWAL jeśli wygrał inicjatywę)
            if (currentPlayerTurn) {
                executePlayerTurn();
            } else {
                executeRivalTurn();
            }

            // Sprawdź warunek zwycięstwa po ruchu pierwszej osoby
            if (checkWinCondition()) {
                matchActive = false;
                break;
            }

            System.out.println();  // Separacja między fazami

            // ===== FAZA 2: RYWAL (lub GRACZ jeśli wygrał inicjatywę)
            System.out.println("─────────────────────────────────────────────────────────────");

            if (currentPlayerTurn) {
                executeRivalTurn();
            } else {
                executePlayerTurn();
            }

            // Sprawdź warunek zwycięstwa po ruchu drugiej osoby
            if (checkWinCondition()) {
                matchActive = false;
                break;
            }

            // Warning jeśli za wiele tur
            if (turnCount == TURN_LIMIT_WARNING) {
                System.out.println("\n⚠️  UWAGA: Walka trwa już " + TURN_LIMIT_WARNING + " tur! Przyspiesz! ⚠️\n");
            }

            System.out.println();
        }

        if (turnCount >= MAX_TURNS) {
            System.out.println("\n⏰ TURA LIMITU OSIĄGNIĘTA! Walka kończy się remisem.\n");
        }
    }

    /**
     * Tura GRACZA - wczytaj input i wykonaj akcję.
     */
    private void executePlayerTurn() {
        System.out.println("\n>>> RUCH GRACZA <<<\n");

        // STUN: ogłuszony gracz traci turę
        if (playerState.isStunned()) {
            System.out.println("  💫 OGŁUSZONY! Tracisz tę turę.");
            playerState.setStunned(false);
            return;
        }

        // Wyświetl obecny stan
        System.out.println("[STAN GRACZA]");
        System.out.println("  HP: " + playerState.getCurrentHp() + "/" + playerState.getMaxHp());
        System.out.println("  Stamina: " + playerState.getCurrentStamina() + "/" + playerState.getMaxStamina());
        System.out.println("  Armor: " + playerState.getCurrentArmor());
        System.out.println();

        // Pobierz akcję od gracza
        Object action = inputHandler.displayActionMenu(playerFighter, rivalFighter, position);

        if (action instanceof AbstractAttack) {
            executePlayerAttack((AbstractAttack) action);
        } else if (action instanceof Move) {
            executePlayerMove((Move) action);
        } else if (action instanceof AbstractRegeneratePlayer) {
            executePlayerRegeneration((AbstractRegeneratePlayer) action);
        }
    }

    /**
     * Gracz wykonuje atak.
     */
    private void executePlayerAttack(AbstractAttack attack) {
        System.out.println("\n⚔️  ATAK: " + attack.getAttackName());
        System.out.println("  Pozycja Gracza: " + position.getPlayerPosition());

        // Uwzględnij broń jeśli atak jest melee (calculateDamageWithEquipment zamiast calculateDamage)
        int baseDamage = (attack instanceof AbstractMeleeAttack melee)
                ? melee.calculateDamageWithEquipment(playerFighter.getPlayer(), playerState)
                : attack.calculateDamage(playerFighter, playerState);

        // PATH BONUS: WOJOWNIK +15% dla ataków wręcz
        if (attack instanceof AbstractMeleeAttack
                && playerFighter.getPlayer().getPath() == CharacterPath.WOJOWNIK) {
            baseDamage = (int) (baseDamage * WOJOWNIK_MELEE_DAMAGE_BONUS);
            System.out.println("  🥊 Bonus WOJOWNIKA: +15% obrażeń wręcz");
        }

        int hitChance = attack.attackAccuracyValue(playerFighter, playerState);
        // PATH BONUS: CWANIAK broniący się ma +10% szansy na unik
        if (rivalFighter.getPlayer().getPath() == CharacterPath.CWANIAK) {
            hitChance -= CWANIAK_DODGE_PENALTY;
            System.out.println("  🐺 Rywal-CWANIAK próbuje uniknąć: -10% trafienia");
        }
        boolean hits = random.nextInt(100) < hitChance;

        if (!hits) {
            System.out.println("  ❌ CHYBIENIE! (" + hitChance + "% szansa) - Atak minął cel!");
            attack.applySpecialEffects(playerState); // Stamina burned anyway
            consumeWeaponIfNeeded(playerFighter.getPlayer());
            return;
        }

        System.out.println("  ✅ TRAFIENIE! (" + hitChance + "% szansa)");

        // Oblicz obrażenia
        DamageCalculator.DamageResult result = damageCalc.applyDamage(playerFighter.getPlayer(), rivalFighter.getPlayer(), baseDamage, rivalState, mode);
        System.out.println("  💥 Obrażenia: " + result.toString());

        // Śledzenie obrażeń dla statystyk i FightResult
        playerDamageDealt += result.totalDamage;

        if (result.wasCritical) {
            System.out.println("  ⚡ CIOS KRYTYCZNY!");
        }
        if (result.wasInstantKill) {
            System.out.println("  🔥 INSTANT KILL!");
        }

        // LEADER SCREAM: roll na stuna jeśli atak to ryk
        if (attack instanceof LeaderScream scream) {
            double stunChance = scream.calculateStunChance(playerFighter);
            if (random.nextDouble() < stunChance) {
                rivalState.setStunned(true);
                System.out.println("  💫 RYWAL OGŁUSZONY! Traci następną turę.");
            }
        }

        // Konsumuj staminę
        attack.applySpecialEffects(playerState);

        // Zużyj broń jednorazową (Raca / Mołotow)
        consumeWeaponIfNeeded(playerFighter.getPlayer());

        // Publiczność
        int previousAudience = audienceBar.getCurrentAudience();
        int crowdAppeal = attack.getCrowdAppeal();
        playerFighter.addCrowdReaction(crowdAppeal);
        audienceBar.addAudienceFromAttack(crowdAppeal, playerFighter.getConnections(), playerFighter.getValor());
        int audienceDelta = audienceBar.getCurrentAudience() - previousAudience;

        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization() + (audienceDelta >= 0 ? " ↑" : " ↓") + Math.abs(audienceDelta));
    }

    /**
     * Jednorazowa broń (Raca, Mołotow) jest zużywana po użyciu w walce.
     */
    private void consumeWeaponIfNeeded(Player attacker) {
        Weapon used = attacker.getInventory().getEquippedWeapon();
        if (used != null && used.isConsumable()) {
            attacker.getInventory().unequipWeapon();
            attacker.getInventory().removeItem(used);
            System.out.println("  🔥 " + used.getName() + " zostało zużyte!");
        }
    }

    /**
     * Gracz się porusza.
     */
    private void executePlayerMove(Move move) {
        System.out.println("\n🚶 RUCH: " + move.getMovementName());

        double stepSize = move.sizeOfStep(playerFighter);
        MovementDirection direction = move.moveDirection();
        int previousPosition = position.getPlayerPosition();

        if (direction == MovementDirection.LEFT) {
            System.out.println("  ← Gracz porusza się w lewo (wycofanie)!");
            position.movePlayer(MovementDirection.LEFT, stepSize);
        } else {
            System.out.println("  → Gracz porusza się w prawo (zbliżenie)!");
            position.movePlayer(MovementDirection.RIGHT, stepSize);
        }

        int newPosition = position.getPlayerPosition();
        int positionDelta = newPosition - previousPosition;
        System.out.println("  Pozycja: " + previousPosition + " → " + newPosition + " (zmiana: " + (positionDelta >= 0 ? "+" : "") + positionDelta + ")");
        System.out.println("  Nowy dystans: " + position.getDistance() + " jednostek");
        playerState.consumeStamina((int)move.getStaminaCost());
        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization());
    }

    /**
     * Gracz się regeneruje.
     */
    private void executePlayerRegeneration(AbstractRegeneratePlayer regen) {
        System.out.println("\n💚 REGENERACJA: " + regen.getRegenerationName());
        System.out.println("  Pozycja Gracza: " + position.getPlayerPosition());
        regen.executeRegeneration(playerState);
        playerState.consumeStamina(10);
        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization());
    }

    /**
     * Tura RYWALA (AI).
     */
    private void executeRivalTurn() {
        System.out.println("\n>>> RUCH RYWALA (AI) <<<\n");

        // STUN: ogłuszony rywal traci turę
        if (rivalState.isStunned()) {
            System.out.println("  💫 Rywal jest OGŁUSZONY! Traci turę.");
            rivalState.setStunned(false);
            return;
        }

        System.out.println("[STAN RYWALA]");
        System.out.println("  HP: " + rivalState.getCurrentHp() + "/" + rivalState.getMaxHp());
        System.out.println("  Stamina: " + rivalState.getCurrentStamina() + "/" + rivalState.getMaxStamina());
        System.out.println("  Armor: " + rivalState.getCurrentArmor());
        System.out.println("  Pozycja: " + position.getRivalPosition());
        System.out.println();

        // AI decyzja - makeDecision zwraca Object (AbstractAttack, Move, lub AbstractRegeneratePlayer)
        Object action = aiSystem.makeDecision(rivalFighter.getPlayer(), playerFighter.getPlayer(), rivalAttacks, rivalMoves, position);

        // Zaloguj decyzję AI
        if (action instanceof AbstractAttack) {
            AbstractAttack attack = (AbstractAttack) action;
            System.out.println("[AI DECYZJA] Wybrał: ATAK (" + attack.getAttackName() + ")");
            executeRivalAttack(attack);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            System.out.println("[AI DECYZJA] Wybrał: RUCH (" + move.getMovementName() + ")");
            executeRivalMove(move);
        } else if (action instanceof AbstractRegeneratePlayer) {
            AbstractRegeneratePlayer regen = (AbstractRegeneratePlayer) action;
            System.out.println("[AI DECYZJA] Wybrał: REGENERACJA (" + regen.getRegenerationName() + ")");
            executeRivalRegeneration(regen);
        } else {
            System.out.println("[AI DECYZJA] Brak dostępnych akcji!");
        }
    }

    /**
     * Rywal wykonuje atak.
     * WAŻNE: Sprawdza walidację zasięgu - jeśli atak spoza zasięgu, to automatycznie chybienie!
     */
    private void executeRivalAttack(AbstractAttack attack) {
        System.out.println("⚔️  ATAK: " + attack.getAttackName());
        System.out.println("  Pozycja Rywala: " + position.getRivalPosition());

        int currentDistance = position.getDistance();
        int rivalReach = rivalFighter.getSpeed() / 2;

        // WALIDACJA ZASIĘGU - Symetryczne zasady jak dla Gracza!
        if (currentDistance > rivalReach) {
            System.out.println("  ⚠️  BŁĄD AI: Rywal posiada zasięg ataku [" + rivalReach + "], dystans wynosi [" + currentDistance + "]");
            System.out.println("  ❌ Rywal popełnia błąd i próbuje zaatakować [" + attack.getAttackName() + "], tracąc ruch i staminę!");
            attack.applySpecialEffects(rivalState); // Tracisz staminę mimo chybienia
            return;
        }

        // Uwzględnij broń rywala jeśli atak jest melee
        int baseDamage = (attack instanceof AbstractMeleeAttack melee)
                ? melee.calculateDamageWithEquipment(rivalFighter.getPlayer(), rivalState)
                : attack.calculateDamage(rivalFighter, rivalState);

        // PATH BONUS: WOJOWNIK +15% dla rywala (rywal też może mieć ścieżkę)
        if (attack instanceof AbstractMeleeAttack
                && rivalFighter.getPlayer().getPath() == CharacterPath.WOJOWNIK) {
            baseDamage = (int) (baseDamage * WOJOWNIK_MELEE_DAMAGE_BONUS);
        }

        int hitChance = attack.attackAccuracyValue(rivalFighter, rivalState);
        // PATH BONUS: gracz-CWANIAK ma +10% szansy na unik
        if (playerFighter.getPlayer().getPath() == CharacterPath.CWANIAK) {
            hitChance -= CWANIAK_DODGE_PENALTY;
            System.out.println("  🐺 CWANIAK próbuje uniknąć: -10% trafienia rywala");
        }
        boolean hits = random.nextInt(100) < hitChance;

        if (!hits) {
            System.out.println("  ❌ CHYBIENIE! (" + hitChance + "% szansa) - Atak rywala minął!");
            attack.applySpecialEffects(rivalState);
            consumeWeaponIfNeeded(rivalFighter.getPlayer());
            return;
        }

        System.out.println("  ✅ TRAFIENIE! (" + hitChance + "% szansa)");

        DamageCalculator.DamageResult result = damageCalc.applyDamage(rivalFighter.getPlayer(), playerFighter.getPlayer(), baseDamage, playerState, mode);
        System.out.println("  💥 Obrażenia: " + result.toString());

        // Śledzenie obrażeń zadanych przez rywala
        rivalDamageDealt += result.totalDamage;

        if (result.wasCritical) {
            System.out.println("  ⚡ CIOS KRYTYCZNY!");
        }
        if (result.wasInstantKill) {
            System.out.println("  🔥 INSTANT KILL!");
        }

        // LEADER SCREAM rywala → stun gracza
        if (attack instanceof LeaderScream scream) {
            double stunChance = scream.calculateStunChance(rivalFighter);
            if (random.nextDouble() < stunChance) {
                playerState.setStunned(true);
                System.out.println("  💫 GRACZ OGŁUSZONY! Tracisz następną turę.");
            }
        }

        attack.applySpecialEffects(rivalState);
        consumeWeaponIfNeeded(rivalFighter.getPlayer());
        
        // Publiczność - reakoja na atak rywala
        int crowdAppeal = attack.getCrowdAppeal();
        rivalFighter.addCrowdReaction(crowdAppeal);
        audienceBar.addAudienceFromAttack(crowdAppeal, rivalFighter.getConnections(), rivalFighter.getValor());
        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization());
    }

    /**
     * Rywal się porusza.
     */
    private void executeRivalMove(Move move) {
        System.out.println("🚶 RUCH: " + move.getMovementName());
        double stepSize = move.sizeOfStep(rivalFighter);
        MovementDirection direction = move.moveDirection();
        int previousPosition = position.getRivalPosition();

        if (direction == MovementDirection.RIGHT) {
            System.out.println("  → Rywal porusza się w prawo (wycofanie)!");
            position.moveRival(MovementDirection.RIGHT, stepSize);
        } else {
            System.out.println("  ← Rywal porusza się w lewo (zbliżenie)!");
            position.moveRival(MovementDirection.LEFT, stepSize);
        }

        int newPosition = position.getRivalPosition();
        int positionDelta = newPosition - previousPosition;
        System.out.println("  Pozycja: " + previousPosition + " → " + newPosition + " (zmiana: " + (positionDelta >= 0 ? "+" : "") + positionDelta + ")");
        System.out.println("  Nowy dystans: " + position.getDistance() + " jednostek");
        rivalState.consumeStamina((int)move.getStaminaCost());
        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization());
    }

    /**
     * Rywal się regeneruje.
     */
    private void executeRivalRegeneration(AbstractRegeneratePlayer regen) {
        System.out.println("💚 REGENERACJA: " + regen.getRegenerationName());
        System.out.println("  Pozycja Rywala: " + position.getRivalPosition());
        regen.executeRegeneration(rivalState);
        rivalState.consumeStamina(10);
        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization());
    }

    /**
     * Sprawd warunek zwycięstwa.
     * KRYTYCZNE POPRAWKI:
     * - Sparring: "First Blood" - ten kto PIERWSZY straci HP przegrywa
     * - Tournament: "Death Match" - walka do HP = 0
     */
    private boolean checkWinCondition() {
        if (mode.isTraining()) {
            // Sparring: first blood
            // To znaczy: jeśli KAŻDY z nich stracił HP, walka się kończy
            // Ale KTÓRY z nich PIERWSZY stracił HP - to jest zwycięzca
            boolean playerHasBleeding = playerState.getCurrentHp() < playerState.getMaxHp();
            boolean rivalHasBleeding = rivalState.getCurrentHp() < rivalState.getMaxHp();

            // Jeśli obaj mają bleeding, walka się kończy (pierwszy kto dostał uraz, wygrywa ten drugi)
            if (playerHasBleeding || rivalHasBleeding) {
                return true; // Koniec sparingu
            }
        } else {
            // Tournament: death match
            if (!playerState.isAlive() || !rivalState.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Określ zwycięzcę i zakończ walkę.
     * KRYTYCZNE: W sparringu - KTO PIERWSZY STRACIŁ HP, ten przegrywa!
     */
    private void determineWinner() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   🎊 WALKA ZAKOŃCZONA! 🎊                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        boolean playerHasBleeding = playerState.getCurrentHp() < playerState.getMaxHp();
        boolean rivalHasBleeding = rivalState.getCurrentHp() < rivalState.getMaxHp();

        if (mode.isTraining()) {
            // SPARRING: First Blood Mode
            // Jeśli gracz stracił HP (a rywal nie), gracz przegrywa
            if (playerHasBleeding && !rivalHasBleeding) {
                winner = rivalFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal)");
                System.out.println("💔 Porażka... Dostalenś pierwszy cios!");
                audienceBar.removeAudienceOnLoss(20);
                playerFighter.getPlayer().spendMoney(EconomySystem.calculateSparringLossPenalty());
            }
            // Jeśli rywal stracił HP (a gracz nie), gracz wygrywa
            else if (rivalHasBleeding && !playerHasBleeding) {
                winner = playerFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + playerFighter.getName() + " (Ty!)");
                System.out.println("🎉 Zwycięstwo! Zadałeś pierwszy cios!");
                double multiplier = audienceBar.getMoneyMultiplier();
                int finalReward = EconomySystem.calculateSparringReward(multiplier);
                rewardMoney = finalReward;
                playerFighter.grantVictoryRewards(50, finalReward);
                System.out.println("💰 Nagroda: " + finalReward + " monet (multiplier: " + String.format("%.1f%%", multiplier * 100) + ")");
            }
            // Obaj stracili HP - to nie powinno się zdarzyć w sparringu, ale gdyby...
            else if (playerHasBleeding && rivalHasBleeding) {
                // Niewyznaczalny wynik - zwycięzca to ten z więcej HP
                if (playerState.getCurrentHp() > rivalState.getCurrentHp()) {
                    winner = playerFighter.getPlayer();
                    System.out.println("👑 ZWYCIĘZCA: " + playerFighter.getName() + " (Ty!)");
                    System.out.println("🎉 Zwycięstwo! Miałeś więcej zdrowia");
                    double multiplier = audienceBar.getMoneyMultiplier();
                    int finalReward = EconomySystem.calculateSparringReward(multiplier);
                    playerFighter.grantVictoryRewards(50, finalReward);
                    System.out.println("💰 Nagroda: " + finalReward + " monet (multiplier: " + String.format("%.1f%%", multiplier * 100) + ")");
                } else {
                    winner = rivalFighter.getPlayer();
                    System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal)");
                    System.out.println("💔 Porażka... Rywal miał więcej zdrowia");
                    audienceBar.removeAudienceOnLoss(20);
                    playerFighter.getPlayer().spendMoney(EconomySystem.calculateSparringLossPenalty());
                }
            } else {
                // Nikt nie ma obrażeń? Niemożliwe - ale na wszelki wypadek rywalu dajemy zwycięstwo
                winner = rivalFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal - system error recovery)");
                System.out.println("⚠️  System Error: obaj bez obrażeń?!");
                audienceBar.removeAudienceOnLoss(20);
                playerFighter.getPlayer().spendMoney(EconomySystem.calculateSparringLossPenalty());
            }
        } else {
            // TOURNAMENT: Death Match
            if (playerState.getCurrentHp() <= 0) {
                winner = rivalFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal)");
                System.out.println("💔 Porażka na polu bitwy...");
                audienceBar.removeAudienceOnLoss(35);
                playerFighter.getPlayer().spendMoney(EconomySystem.calculateTournamentLossPenalty());
            } else {
                winner = playerFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + playerFighter.getName() + " (Ty!)");
                System.out.println("🎉 Zwycięstwo! Jesteś królem Areny!");
                double multiplier = audienceBar.getMoneyMultiplier();
                int finalReward = EconomySystem.calculateTournamentReward(multiplier);
                rewardMoney = finalReward;
                playerFighter.grantVictoryRewards(50, finalReward);
                System.out.println("💰 Nagroda: " + finalReward + " monet (multiplier: " + String.format("%.1f%%", multiplier * 100) + ")");
            }
        }

        System.out.println("\n[STATYSTYKI]");
        System.out.println("  Tury: " + turnCount);
        System.out.println("  Publiczność: " + audienceBar.getBarVisualization());
        System.out.println("  Stan Gracza: " + playerState.toString());
        System.out.println("  Stan Rywala: " + rivalState.toString());
    }

    // ==================== HELPER METHODS ====================

    private List<AbstractAttack> initializeAttacks() {
        List<AbstractAttack> attacks = new ArrayList<>();
        attacks.add(new QuickMeleeAttack());
        attacks.add(new MediumMeleeAttack());
        attacks.add(new StrongMeleeAttack());
        return attacks;
    }

    private List<Move> initializeMoves() {
        List<Move> moves = new ArrayList<>();
        moves.add(new Step(MovementDirection.LEFT));
        moves.add(new Step(MovementDirection.RIGHT));
        moves.add(new Jump(MovementDirection.LEFT));
        moves.add(new Jump(MovementDirection.RIGHT));
        return moves;
    }

    // ==================== GETTERS ====================

    public Player getWinner() {
        return winner;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public CombatMode getMode() {
        return mode;
    }

    public AudienceBar getAudienceBar() {
        return audienceBar;
    }
}

