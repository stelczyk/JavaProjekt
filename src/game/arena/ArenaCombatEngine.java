package game.arena;

import game.player.Player;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.attack.melee.QuickMeleeAttack;
import game.player.combat.attack.melee.MediumMeleeAttack;
import game.player.combat.attack.melee.StrongMeleeAttack;
import game.player.combat.movement.Move;
import game.player.combat.movement.MovementDirection;
import game.player.combat.movement.Step;
import game.player.combat.movement.Jump;
import game.player.combat.regeneration.AbstractRegeneratePlayer;

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

    // Stany walki
    private ArenaPosition position;
    private ArenaFighter playerFighter;
    private ArenaFighter rivalFighter;
    private ArenaFighterState playerState;
    private ArenaFighterState rivalState;

    private int turnCount = 0;
    private boolean matchActive = true;
    private Player winner;
    private static final int MAX_TURNS = 50;
    private static final int TURN_LIMIT_WARNING = 30;

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
    public void startMatch(Player player, Player rival) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          🏛️  ARENA COMBAT (" + mode.getDescription() + ")  🏛️          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // STEP 1: Inicjalizacja
        this.playerFighter = new ArenaFighter(player);
        this.rivalFighter = new ArenaFighter(rival);
        this.playerState = playerFighter.getState();
        this.rivalState = rivalFighter.getState();
        this.position = new ArenaPosition();

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

        int baseDamage = attack.calculateDamage(playerFighter, playerState);
        int hitChance = attack.attackAccuracyValue(playerFighter, playerState);
        boolean hits = new Random().nextInt(100) < hitChance;

        if (!hits) {
            System.out.println("  ❌ CHYBIENIE! (" + hitChance + "% szansa) - Atak minął cel!");
            attack.applySpecialEffects(playerState); // Stamina burned anyway
            return;
        }

        System.out.println("  ✅ TRAFIENIE! (" + hitChance + "% szansa)");

        // Oblicz obrażenia
        DamageCalculator.DamageResult result = damageCalc.applyDamage(playerFighter.getPlayer(), rivalFighter.getPlayer(), baseDamage, rivalState, mode);
        System.out.println("  💥 Obrażenia: " + result.toString());

        if (result.wasCritical) {
            System.out.println("  ⚡ CIOS KRYTYCZNY!");
        }
        if (result.wasInstantKill) {
            System.out.println("  🔥 INSTANT KILL!");
        }

        // Konsumuj staminę
        attack.applySpecialEffects(playerState);

        // Publiczność
        int previousAudience = audienceBar.getCurrentAudience();
        int crowdAppeal = attack.getCrowdAppeal();
        playerFighter.addCrowdReaction(crowdAppeal);
        audienceBar.addAudienceFromAttack(crowdAppeal, playerFighter.getConnections(), playerFighter.getValor());
        int audienceDelta = audienceBar.getCurrentAudience() - previousAudience;

        System.out.println("  📢 Publiczność: " + audienceBar.getBarVisualization() + (audienceDelta >= 0 ? " ↑" : " ↓") + Math.abs(audienceDelta));
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

        System.out.println("[STAN RYWALA]");
        System.out.println("  HP: " + rivalState.getCurrentHp() + "/" + rivalState.getMaxHp());
        System.out.println("  Stamina: " + rivalState.getCurrentStamina() + "/" + rivalState.getMaxStamina());
        System.out.println("  Armor: " + rivalState.getCurrentArmor());
        System.out.println("  Pozycja: " + position.getRivalPosition());
        System.out.println();

        // Pobierz ataki i ruchy dla rywala
        java.util.List<AbstractAttack> rivalAttacks = initializeAttacks();
        java.util.List<Move> rivalMoves = initializeMoves();

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

        int baseDamage = attack.calculateDamage(rivalFighter, rivalState);
        int hitChance = attack.attackAccuracyValue(rivalFighter, rivalState);
        boolean hits = new Random().nextInt(100) < hitChance;

        if (!hits) {
            System.out.println("  ❌ CHYBIENIE! (" + hitChance + "% szansa) - Atak rywala minął!");
            attack.applySpecialEffects(rivalState);
            return;
        }

        System.out.println("  ✅ TRAFIENIE! (" + hitChance + "% szansa)");

        DamageCalculator.DamageResult result = damageCalc.applyDamage(rivalFighter.getPlayer(), playerFighter.getPlayer(), baseDamage, playerState, mode);
        System.out.println("  💥 Obrażenia: " + result.toString());

        if (result.wasCritical) {
            System.out.println("  ⚡ CIOS KRYTYCZNY!");
        }
        if (result.wasInstantKill) {
            System.out.println("  🔥 INSTANT KILL!");
        }

        attack.applySpecialEffects(rivalState);
        
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
                playerFighter.getPlayer().spendMoney(100);
            }
            // Jeśli rywal stracił HP (a gracz nie), gracz wygrywa
            else if (rivalHasBleeding && !playerHasBleeding) {
                winner = playerFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + playerFighter.getName() + " (Ty!)");
                System.out.println("🎉 Zwycięstwo! Zadałeś pierwszy cios!");
                int baseReward = 200;
                double multiplier = audienceBar.getMoneyMultiplier();
                int finalReward = (int) (baseReward * multiplier);
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
                    int baseReward = 200;
                    double multiplier = audienceBar.getMoneyMultiplier();
                    int finalReward = (int) (baseReward * multiplier);
                    playerFighter.grantVictoryRewards(50, finalReward);
                    System.out.println("💰 Nagroda: " + finalReward + " monet (multiplier: " + String.format("%.1f%%", multiplier * 100) + ")");
                } else {
                    winner = rivalFighter.getPlayer();
                    System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal)");
                    System.out.println("💔 Porażka... Rywal miał więcej zdrowia");
                    audienceBar.removeAudienceOnLoss(20);
                    playerFighter.getPlayer().spendMoney(100);
                }
            } else {
                // Nikt nie ma obrażeń? Niemożliwe - ale na wszelki wypadek rywalu dajemy zwycięstwo
                winner = rivalFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal - system error recovery)");
                System.out.println("⚠️  System Error: obaj bez obrażeń?!");
                audienceBar.removeAudienceOnLoss(20);
                playerFighter.getPlayer().spendMoney(100);
            }
        } else {
            // TOURNAMENT: Death Match
            if (playerState.getCurrentHp() <= 0) {
                winner = rivalFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + rivalFighter.getName() + " (Rywal)");
                System.out.println("💔 Porażka na polu bitwy...");
                audienceBar.removeAudienceOnLoss(35);
                playerFighter.getPlayer().spendMoney(200);
            } else {
                winner = playerFighter.getPlayer();
                System.out.println("👑 ZWYCIĘZCA: " + playerFighter.getName() + " (Ty!)");
                System.out.println("🎉 Zwycięstwo! Jesteś królem Areny!");
                int baseReward = 500;
                double multiplier = audienceBar.getMoneyMultiplier();
                int finalReward = (int) (baseReward * multiplier);
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

    private java.util.List<AbstractAttack> initializeAttacks() {
        java.util.List<AbstractAttack> attacks = new java.util.ArrayList<>();
        attacks.add(new QuickMeleeAttack());
        attacks.add(new MediumMeleeAttack());
        attacks.add(new StrongMeleeAttack());
        return attacks;
    }

    private java.util.List<Move> initializeMoves() {
        java.util.List<Move> moves = new java.util.ArrayList<>();
        moves.add(new game.player.combat.movement.Step(MovementDirection.LEFT));
        moves.add(new game.player.combat.movement.Step(MovementDirection.RIGHT));
        moves.add(new game.player.combat.movement.Jump(MovementDirection.LEFT));
        moves.add(new game.player.combat.movement.Jump(MovementDirection.RIGHT));
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

