# Dokumentacja Architektoniczna - Arena Fighting Game

## 📐 Filozofia Projektu

Projekt oparty jest na **separacji odpowiedzialności** i **SOLID principles**. Kluczowa idea:
- **CO gracz może zrobić** (interfejsy `arena/*`)
- **CO wpływa na akcje** (interfejsy `types/*`)
- **JAK konkretne akcje działają** (implementacje w `combat/*`)

---

## 🏗️ Struktura Modułów

### 1. **constants/** - Stałe Globalne
```
GameConstants.java - Wszystkie wartości początkowe i limity gry
```

**Status**: ✅ Dobrze zaprojektowany
**Przyszłość**: Możliwy podział na `GameplayConstants` i `BalanceConstants`

---

### 2. **player/attributes/** - System Atrybutów

#### 2.1 **arena/** - CO GRACZ MOŻE ZROBIĆ
Definiuje **akcje** bez implementacji:

- `ArenaAttackPlayer` - Atak: damage, accuracy, crowd appeal, special effects
- `ArenaDefensePlayer` - Obrona: unik, redukcja obrażeń
- `ArenaMovementPlayer` - Ruch: kierunek, długość kroku, ograniczenia mapy
- `ArenaAudiencePlayer` - Wpływ na tłum: hype, mnożnik zarobków

**Dlaczego interfejsy?**
- System walki nie wie o konkretnych atakach (melee/ranged/special)
- Każda akcja zwraca **surowe wartości** (nie procenty!), procenty oblicza system walki
- **Single Responsibility**: atak nie wie o defensywie, defensywa nie wie o atakach

#### 2.2 **types/** - CO WPŁYWA NA AKCJE
Definiuje **atrybuty** używane przez akcje:

- `ArenaAttackPlayerAttributes` - Strength, Accuracy, Cunning, Valor, Brave, Connections
- `ArenaDefencePlayerAttributes` - Defense, Speed
- `ArenaMovementPlayerAttributes` - Speed
- `ArenaAudiencePlayerAttributes` - Connections, Valor

**Separacja atrybutów**: Każda akcja widzi TYLKO atrybuty której potrzebuje.

**❓ Pytanie otwarte**: Czy dodać metody dla broni/armoru teammate'a tutaj?
**Odpowiedź**: TAK, teammate powinien rozszerzyć te interfejsy o:
```java
// ArenaAttackPlayerAttributes
int getWeaponPower();
double getWeaponAccuracyModifier();

// ArenaDefencePlayerAttributes
int getArmorProtection();
```

#### 2.3 **Pliki do weryfikacji**:
- ❌ **ArenaAttributes.java** - ZBĘDNY, duplikuje PlayerAttributes → **DO USUNIĘCIA**
- ⚠️ **CharacterAttributeType.java** - Enum bez wartości startowych

**Propozycja poprawy** CharacterAttributeType:
```java
public enum CharacterAttributeType {
    STRENGTH(GameConstants.DEFAULT_START_ATTRIBUTE_VALUE),
    DEFENSE(GameConstants.DEFAULT_START_ATTRIBUTE_VALUE),
    // ... rest

    private final int defaultValue;
    CharacterAttributeType(int defaultValue) {
        this.defaultValue = defaultValue;
    }
    public int getDefaultValue() { return defaultValue; }
}
```

---

### 3. **player/combat/** - Implementacje Akcji

#### 3.1 **attack/** - Hierarchia Ataków

```
AbstractAttack (Template Method Pattern)
├── AbstractMeleeAttack (Strength + Stamina)
│   ├── QuickMeleeAttack
│   ├── MediumMeleeAttack
│   └── StrongMeleeAttack
├── AbstractRangedAttack (Accuracy + Cunning)
│   └── MediumRangedAttack
└── AbstractSpecialAttack (specjalne atrybuty)
    ├── CallToFriends (Connections + Valor + pasek postępu)
    └── LeaderScream (Brave + Valor, pomija defensywę, ogłuszenie)
```

**Wzorce użyte**:
- **Template Method** w AbstractAttack - szkielet calculateDamage()
- **Strategy Pattern** w AttackStyle (QUICK/NORMAL/STRONG)
- **Open/Closed Principle** - nowe typy ataków bez modyfikacji AbstractAttack

#### 3.2 **action/** - Akcje Specjalne (nie ataki)

```
ArenaAction (interfejs)
└── LeaveFromArena - Ucieczka z areny (tylko Confident)
```

**⚠️ PROBLEM LOKALIZACJI**: `action/` jest w `player/combat/`, powinno być w `game.arena/`

---

### 4. **player/combat/state/** - Stan Walki

#### **FighterState** - Interfejs stanu walki
```java
- getCurrentHp() / reduceHp() / increaseHp()
- getCurrentStamina() / consumeStamina() / increaseStamina()
- getCurrentArmor() / reduceArmor()
- getCrowdSatisfaction() / addCrowdSatisfaction() / reduceCrowdSatisfaction()
```

**🚨 KRYTYCZNY PROBLEM**: FighterState to **interfejs**, powinien być **KLASĄ**.

**Dlaczego klasa?**:
- Nigdy nie będziesz mieć wielu implementacji stanu walki
- To konkretny stan, nie abstrakcja
- Interfejs ma sens gdy masz polimorfizm

**Propozycja**: `ArenaFighterState.java` jako klasa

---

## 🔴 GŁÓWNE PROBLEMY ARCHITEKTONICZNE

### Problem #1: Player implementuje FighterState

**Obecny kod**:
```java
public class Player implements FighterState, ArenaAttackPlayerAttributes...
```

**Dlaczego to źle**:
- **Łamie Single Responsibility Principle**
- Player reprezentuje gracza w CAŁEJ grze (sklep, arena, progresja, money, xp)
- FighterState to stan TYLKO w walce (HP, stamina w tej konkretnej walce)
- Po walce `currentHp`, `currentStamina`, `crowdSatisfaction` są bezużyteczne

**Konsekwencje**:
- Nie można mieć dwóch symulacji walki równocześnie
- Stan walki "zaśmieca" klasę Player
- Trudno zresetować stan między walkami

---

### Problem #2: Brak separacji Player ↔ Arena

**Brakująca warstwa abstrakcji**:
```
Player (globalny) ──X──> System Walki
                        ↑
                Brak ArenaFighter!
```

---

## ✅ PROPONOWANA ARCHITEKTURA

### Struktura Player/Arena:

```
game.player
├── Player.java
│   ├── PlayerProfile profile     // Nickname, age, height, weight
│   ├── PlayerAttribute attributes // BAZOWE atrybuty (permanentne)
│   ├── PlayerInventory inventory
│   ├── int level, xp, money      // Progresja globalna
│   └── getMaxHp() / spendMoney() / gainXp()
│
├── PlayerAttribute.java
│   └── Wszystkie 10 atrybutów + upgrade/downgrade
│
└── PlayerInventory.java          // Ekwipunek (teammate)

game.arena (NOWY PAKIET)
├── ArenaFighter.java
│   ├── Player player             // Referencja do globalnego gracza
│   ├── ArenaFighterState state   // Stan W TEJ walce
│   ├── implements Arena*PlayerAttributes (deleguje do player.getAttributes())
│   └── resetForNewFight()
│
├── ArenaFighterState.java (KLASA, nie interfejs)
│   ├── int currentHp
│   ├── int currentStamina
│   ├── int currentArmor
│   ├── int crowdSatisfaction
│   ├── boolean isStunned
│   └── metody get/set
│
├── ArenaCombatEngine.java
│   └── executeTurn(ArenaFighter attacker, ArenaFighter defender, ArenaAttackPlayer attack)
│
└── ArenaAction.java / LeaveFromArena.java
```

### Przepływ walki:

```java
// Przed walką
Player player = new Player(profile);
ArenaFighter playerFighter = new ArenaFighter(player);
ArenaFighter opponentFighter = new ArenaFighter(opponent);

// Walka
ArenaCombatEngine engine = new ArenaCombatEngine();
engine.startFight(playerFighter, opponentFighter);
engine.executeTurn(playerFighter, opponentFighter, new QuickMeleeAttack());

// Po walce
player.gainXp(100);
player.earnMoney(500);
// playerFighter jest niszczony, stan walki znika
```

---

## 🎯 ZALETY NOWEJ ARCHITEKTURY

### 1. **Single Responsibility**
- `Player` = globalny stan gracza (money, xp, level, BASE attributes)
- `ArenaFighter` = reprezentacja W WALCE (combat state, temporary buffs)
- `ArenaFighterState` = czysty stan walki (HP, stamina)

### 2. **Separacja kontekstów**
- W sklepie używasz `Player`
- W walce używasz `ArenaFighter`
- Nie mieszasz stanu globalnego ze stanem walki

### 3. **Możliwość symulacji**
```java
// Symulacja "co by było gdyby"
ArenaFighter simulation = new ArenaFighter(player);
simulation.receiveDamage(50);
// Player.currentHp NIE został zmieniony
```

### 4. **Reset między walkami**
```java
playerFighter.resetForNewFight(); // Resetuje HP/stamina do MAX
```

### 5. **Łatwe dodawanie temporary effects**
```java
ArenaFighterState {
    boolean isStunned;
    boolean isPoisoned;
    int temporaryStrengthBonus;
}
```

---

## 📋 PLAN REFAKTORYZACJI

### Priorytet 1 (Krytyczne):
1. ✅ Usuń `ArenaAttributes.java` (zbędny)
2. ✅ Zmień `FighterState` z interfejsu na klasę `ArenaFighterState`
3. ✅ Utwórz `game.arena` package
4. ✅ Utwórz `ArenaFighter.java` jako wrapper nad Player
5. ✅ Przenieś `currentHp`, `currentStamina`, `crowdSatisfaction` z Player do ArenaFighterState
6. ✅ Player przestaje implementować FighterState

### Priorytet 2 (Ważne):
7. Dodaj wartości domyślne do `CharacterAttributeType` enum
8. Przenieś `ArenaAction` / `LeaveFromArena` do `game.arena`
9. Rozszerz `Arena*PlayerAttributes` o metody dla ekwipunku (teammate)

### Priorytet 3 (Nice to have):
10. Utwórz `ArenaCombatEngine` jako silnik walki
11. Utwórz klasę `Opponent` (dziedziczy lub podobna do ArenaFighter)

---

## 🎓 OCENA ZMYSŁU ARCHITEKTONICZNEGO

### ✅ CO ROBISZ ŚWIETNIE:

1. **Interface Segregation** (`arena/` vs `types/`) - **10/10**
   - To jest wzorcowy design, profesjonalny poziom

2. **Hierarchia ataków** (Abstract → Melee/Ranged/Special) - **10/10**
   - Perfekcyjne zastosowanie Template Method + OCP

3. **Intuicje o problemach** - **9/10**
   - Wyczuwasz że Player z FighterState to problem ✅
   - Wyczuwasz że ArenaAttributes jest zbędny ✅
   - Wyczuwasz że ArenaAction w złym miejscu ✅
   - **Twoje instynkty są PRAWIDŁOWE**

4. **Komentarze w kodzie** - **8/10**
   - Dobre wyjaśnienia "dlaczego wartość a nie procent"
   - Czasem zbyt szczegółowe w oczywistych miejscach

### ⚠️ OBSZARY DO POPRAWY:

1. **Nadmierne użycie interfejsów** - **6/10**
   - FighterState nie powinien być interfejsem
   - Nie każda klasa potrzebuje interfejsu - używaj gdy są >1 implementacje

2. **Mixing concerns** (Player + FighterState) - **4/10**
   - To największy problem, ale JUŻ GO WIDZISZ
   - Po refaktoryzacji: 9/10

3. **Package structure** (action w player.combat) - **7/10**
   - Drobny problem, łatwy do naprawy

---

## 💡 PODSUMOWANIE

### Twoja architektura to **7.5/10**

**Mocne strony**:
- Separacja interfejsów (CO / CO WPŁYWA / JAK) = profesjonalny poziom
- Hierarchia klas (Abstract → konkretne) = wzorcowa
- SOLID principles w większości miejscach
- **Bardzo dobre intuicje** o problemach

**Do poprawy**:
- Separacja Player ↔ Arena (główny problem, ale go widzisz!)
- Nadużycie interfejsów (FighterState)
- Drobne problemy z package structure

### **SZCZERZE**:
To jest **ponadprzeciętna** architektura jak na studencki projekt. Masz **bardzo dobry** zmysł architektoniczny. Problemy które zidentyfikowałeś (Player/Arena separation) pokazują że **rozumiesz** zasady projektowania.

Po wprowadzeniu refaktoryzacji (ArenaFighter + ArenaFighterState) będziesz mieć **9/10** - poziom production-ready.

---

## 🔗 RELACJE MIĘDZY MODUŁAMI

```
Player (global) ──creates──> ArenaFighter (combat)
                                   │
                                   ├──has──> ArenaFighterState
                                   ├──uses──> ArenaAttackPlayer
                                   ├──uses──> ArenaDefensePlayer
                                   └──uses──> ArenaMovementPlayer

AbstractAttack ──implements──> ArenaAttackPlayer
     ├── AbstractMeleeAttack
     ├── AbstractRangedAttack
     └── AbstractSpecialAttack

ArenaCombatEngine ──orchestrates──> ArenaFighter + Attacks + Defense
```

---

**Data utworzenia**: 2026-05-06
**Wersja**: 1.0
**Status**: Work in Progress - po refaktoryzacji zmień na "Stable"
