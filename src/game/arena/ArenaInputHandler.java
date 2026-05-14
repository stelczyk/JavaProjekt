package game.arena;

import game.player.CharacterPath;
import game.player.combat.attack.AbstractAttack;
import game.player.combat.attack.melee.QuickMeleeAttack;
import game.player.combat.attack.melee.MediumMeleeAttack;
import game.player.combat.attack.melee.StrongMeleeAttack;
import game.player.combat.attack.special.LeaderScream;
import game.player.combat.movement.Move;
import game.player.combat.movement.Step;
import game.player.combat.movement.Jump;
import game.player.combat.movement.MovementDirection;
import game.player.combat.regeneration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

/**
 * ArenaInputHandler - Obsługuje input gracza podczas walki na Arenie.
 *
 * ODPOWIEDZIALNOŚĆ:
 * - Wyświetlanie dostępnych opcji
 * - Sprawdzenie czy akcja jest dostępna (dystans, stamina, itp.)
 * - Pobieranie i walidacja inputu
 * - Blokowanie niedostępnych akcji (np. atak gdy gracz jest za daleko)
 */
public class ArenaInputHandler {

    private final Scanner scanner;
    private final Random random = new Random();

    // Predefiniowane ataki
    private final List<AbstractAttack> availableAttacks;
    private final List<Move> availableMoves;
    private final List<AbstractRegeneratePlayer> regenerationMethods;

    public ArenaInputHandler(Scanner scanner) {
        this.scanner = scanner;
        this.availableAttacks = initializeAttacks();
        this.availableMoves = initializeMoves();
        this.regenerationMethods = initializeRegenerationMethods();
    }

    /**
     * Wyświetla menu opcji dostępnych dla gracza w danym stanie walki.
     * Zwraca wybraną akcję (atak, ruch, regeneracja).
     */
    public Object displayActionMenu(ArenaFighter playerFighter, ArenaFighter rivalFighter, ArenaPosition position) {
        System.out.println("\n  ╔════════════════════════════════════════════════╗");
        System.out.println("  ║        TWÓJ RUCH - Wybierz akcję               ║");
        System.out.println("  ╚════════════════════════════════════════════════╝\n");

        int currentDistance = position.getDistance();
        int playerMaxReach = playerFighter.getSpeed() / 2; // Speed scaling

        System.out.println("  📏 Dystans do rywala: " + currentDistance + " jednostek");
        System.out.println("  ⚡ Twój zasięg ruchu: " + playerMaxReach + " jednostek\n");

        // ==================== ATAKI ====================
        System.out.println("  ┌─ ATAKI ─────────────────────────────────────┐");
        int attackIndex = 1;
        for (AbstractAttack attack : availableAttacks) {
            boolean canAttack = canExecuteAttack(playerFighter, attack, currentDistance);
            String status = canAttack ? "✓" : "✗";
            System.out.printf("  │ %d) %s %s\n", attackIndex, attack.getAttackName(), status);
            if (!canAttack) {
                if (attack instanceof LeaderScream
                        && playerFighter.getPlayer().getPath() != CharacterPath.LIDER) {
                    System.out.println("  │    → Tylko dla ścieżki LIDER!");
                } else if (playerFighter.getState().getCurrentStamina() < attack.getStaminaCost()) {
                    System.out.println("  │    → Zbyt mało staminy!");
                } else if (currentDistance > playerMaxReach) {
                    System.out.println("  │    → Za daleko! (dystans " + currentDistance + " > zasięg " + playerMaxReach + ")");
                }
            } else {
                // Wyświetl procent trafienia
                int accuracy = attack.attackAccuracyValue(playerFighter, playerFighter.getState());
                System.out.printf("  │    → Procent trafienia: ~%d%% | Damage: ~%d | Stamina: -%d\n",
                    accuracy, attack.calculateDamage(playerFighter, playerFighter.getState()), attack.getStaminaCost());
            }
            attackIndex++;
        }
        System.out.println("  └─────────────────────────────────────────────┘\n");

         // ==================== RUCHY ====================
         System.out.println("  ┌─ RUCHY ─────────────────────────────────────┐");
         int moveIndex = attackIndex;
         for (Move move : availableMoves) {
             boolean canMove = canExecuteMove(playerFighter, move, position);
             String status = canMove ? "✓" : "✗";
             System.out.printf("  │ %d) %s %s\n", moveIndex, move.getMovementName(), status);
             if (!canMove) {
                 double movementRange = move.sizeOfStep(playerFighter);
                 if (playerFighter.getState().getCurrentStamina() < move.getStaminaCost()) {
                     System.out.println("  │    → Zbyt mało staminy!");
                 } else if (move.moveDirection() != MovementDirection.LEFT && currentDistance <= movementRange) {
                     System.out.println("  │    → Za blisko! (dystans " + currentDistance + " <= zasięg " + (int)movementRange + ")");
                 } else {
                     System.out.println("  │    → Nie możesz się poruszać!");
                 }
             } else {
                 double movementRange = move.sizeOfStep(playerFighter);
                 System.out.printf("  │    → Koszt: -%d staminy | Zasięg: ~%d\n",
                     (int)move.getStaminaCost(), (int)movementRange);
             }
             moveIndex++;
         }
         System.out.println("  └─────────────────────────────────────────────┘\n");

        // ==================== REGENERACJA ====================
        System.out.println("  ┌─ REGENERACJA ────────────────────────────────┐");
        int regenIndex = moveIndex;
        for (AbstractRegeneratePlayer regen : regenerationMethods) {
            System.out.printf("  │ %d) %s\n", regenIndex, regen.getRegenerationName());
            System.out.println("  │    → " + regen.getDescription());
            regenIndex++;
        }
        System.out.println("  └─────────────────────────────────────────────┘\n");

        // INPUT
        while (true) {
            System.out.print("  🎯 Wybierz numer akcji: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                int attackCount = availableAttacks.size();
                int moveCount = availableMoves.size();
                int regenCount = regenerationMethods.size();

                // ATAKI
                if (choice >= 1 && choice <= attackCount) {
                    AbstractAttack selectedAttack = availableAttacks.get(choice - 1);
                    if (canExecuteAttack(playerFighter, selectedAttack, currentDistance)) {
                        return selectedAttack;
                    } else {
                        System.out.println("  ❌ Nie możesz użyć tego ataku!");
                        continue;
                    }
                }

                // RUCHY
                if (choice > attackCount && choice <= (attackCount + moveCount)) {
                    Move selectedMove = availableMoves.get(choice - attackCount - 1);
                    if (canExecuteMove(playerFighter, selectedMove, position)) {
                        return selectedMove;
                    } else {
                        System.out.println("  ❌ Nie możesz się w ten sposób poruszać!");
                        continue;
                    }
                }

                // REGENERACJA
                if (choice > (attackCount + moveCount) && choice <= (attackCount + moveCount + regenCount)) {
                    AbstractRegeneratePlayer selectedRegen = regenerationMethods.get(choice - attackCount - moveCount - 1);
                    return selectedRegen;
                }

                System.out.println("  ❌ Niepoprawny wybór!");

            } catch (Exception e) {
                System.out.println("  ❌ Błąd inputu! Spróbuj ponownie.");
                scanner.nextLine();
            }
        }
    }

    // ==================== VALIDATION ====================

    private boolean canExecuteAttack(ArenaFighter fighter, AbstractAttack attack, int distance) {
        // Konieczna stamina
        if (fighter.getState().getCurrentStamina() < attack.getStaminaCost()) {
            return false;
        }

        // LEADER SCREAM - dostępny tylko dla ścieżki LIDER
        // (atak psychologiczny, nie wymaga zasięgu fizycznego)
        if (attack instanceof LeaderScream) {
            return fighter.getPlayer().getPath() == CharacterPath.LIDER;
        }

        // Dystans - musi być w zwarciu (distance <= speed/2)
        int maxReach = fighter.getSpeed() / 2;
        return distance <= maxReach;
    }

    private boolean canExecuteMove(ArenaFighter fighter, Move move, ArenaPosition position) {
        // Musi być stamina
        if (fighter.getState().getCurrentStamina() < move.getStaminaCost()) {
            return false;
        }

        double movementRange = move.sizeOfStep(fighter);
        MovementDirection direction = move.moveDirection();

        // Dla ruchu w prawo (zbliżenia do rywala): możliwy tylko gdy dystans > zasięg
        if (direction == MovementDirection.RIGHT) {
            return position.canApproach(movementRange);
        }

        // Dla ruchu w lewo (wycofywania się): zawsze możliwy jeśli wystarczy staminy
        if (direction == MovementDirection.LEFT) {
            return true;
        }

        return true;
    }

    // ==================== INITIALIZATION ====================

    private List<AbstractAttack> initializeAttacks() {
        List<AbstractAttack> attacks = new ArrayList<>();
        attacks.add(new QuickMeleeAttack());
        attacks.add(new MediumMeleeAttack());
        attacks.add(new StrongMeleeAttack());
        // LeaderScream — odblokowany TYLKO dla ścieżki LIDER (filtrowane w displayActionMenu)
        attacks.add(new LeaderScream());
        return attacks;
    }

    private List<Move> initializeMoves() {
        List<Move> moves = new ArrayList<>();
        // Step - małe ruchy (prawo/lewo)
        moves.add(new Step(MovementDirection.LEFT));   // Wycofanie się
        moves.add(new Step(MovementDirection.RIGHT));  // Zbliżenie
        // Jump - duże ruchy (prawo/lewo)
        moves.add(new Jump(MovementDirection.LEFT));   // Szybkie wycofanie
        moves.add(new Jump(MovementDirection.RIGHT));  // Szybkie zbliżenie
        return moves;
    }

    private List<AbstractRegeneratePlayer> initializeRegenerationMethods() {
        List<AbstractRegeneratePlayer> methods = new ArrayList<>();
        methods.add(new InjectionRegeneration());
        methods.add(new EatCakeRegeneration());
        methods.add(new DrinkEnergyDrinkRegeneration());
        return methods;
    }

    // ==================== GETTERS ====================

    public AbstractAttack getAttackByIndex(int index) {
        return availableAttacks.get(index);
    }

    public Move getMoveByIndex(int index) {
        return availableMoves.get(index);
    }
}

