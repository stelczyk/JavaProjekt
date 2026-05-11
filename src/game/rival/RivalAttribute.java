package game.rival;

public class RivalAttribute {
    public int getGameStartStamina() {
        return gameStartStamina;
    }

    public int getGameStartHp() {
        return gameStartHp;
    }

    public int getGameStartArmor() {
        return gameStartArmor;
    }

    // zmienne stałe
    private final int strength;
    private final int defence;
    private final int accuracy;
    private final int stamina;
    private final int brave;
    private final int cunning;
    private final int speed;
    private final int pathology;
    private final int valor;
    private final int connections;
    // zmienne zmienne (ale tylko do odczytu wiec wciąż pirvate final)
    private final int gameStartStamina;
    private final int gameStartHp;
    private final int gameStartArmor;

    public RivalAttribute(int strength, int defence, int accuracy, int stamina, int brave, int cunning,
                          int speed, int pathology, int valor, int connections,
                          int gameStartStamina, int gameStartHp, int gameStartArmor) {
        this.strength = strength;
        this.defence = defence;
        this.accuracy = accuracy;
        this.stamina = stamina;
        this.brave = brave;
        this.cunning = cunning;
        this.speed = speed;
        this.pathology = pathology;
        this.valor = valor;
        this.connections = connections;
        this.gameStartStamina = gameStartStamina;
        this.gameStartHp = gameStartHp;
        this.gameStartArmor = gameStartArmor;
    }
}
