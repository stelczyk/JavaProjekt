package game.rival;

import game.player.attributes.PlayerAttributes;

/**
 * RivalInfo - Zawiera informacje o Rywalu na Arenie (widoczne przed walką).
 * Implementuje PlayerAttributes dla kompatybilności z systemem atrybutów.
 */
public class RivalInfo implements PlayerAttributes {
    private final String rivalName;
    private final int rivalAge;
    private final int rivalHeight;

    // Atrybuty (będą kopią z gracza)
    private int strength = 5;
    private int defense = 5;
    private int accuracy = 5;
    private int stamina = 5;
    private int brave = 5;
    private int cunning = 5;
    private int speed = 5;
    private int pathology = 5;
    private int valor = 5;
    private int connections = 5;

    public RivalInfo(String name, int rivalAge, int rivalHeight) {
        this.rivalName = name;
        this.rivalAge = rivalAge;
        this.rivalHeight = rivalHeight;
    }

    public String getName() { return rivalName; }
    public int getRivalAge() { return rivalAge; }
    public int getRivalHeight() { return rivalHeight; }

    @Override
    public int getStrength() { return strength; }

    @Override
    public int getDefense() { return defense; }

    @Override
    public int getAccuracy() { return accuracy; }

    @Override
    public int getStamina() { return stamina; }

    @Override
    public int getBrave() { return brave; }

    @Override
    public int getCunning() { return cunning; }

    @Override
    public int getSpeed() { return speed; }

    @Override
    public int getPathology() { return pathology; }

    @Override
    public int getValor() { return valor; }

    @Override
    public int getConnections() { return connections; }

    public void setAttributes(int strength, int defense, int accuracy, int stamina,
                             int brave, int cunning, int speed, int pathology,
                             int valor, int connections) {
        this.strength = strength;
        this.defense = defense;
        this.accuracy = accuracy;
        this.stamina = stamina;
        this.brave = brave;
        this.cunning = cunning;
        this.speed = speed;
        this.pathology = pathology;
        this.valor = valor;
        this.connections = connections;
    }
}
