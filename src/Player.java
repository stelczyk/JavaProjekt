public class Player {
    String nickname;
    int health;
    int level;
    int attackPower;

    public Player(String nickname) {
        this.nickname = nickname;
        this.health = 100;
        this.level = 1;
        this.attackPower = 10;
    }

    public void showStats() {
        System.out.println("Gracz: " + nickname + " | HP: " + health + " | LVL: " + level);
    }
}