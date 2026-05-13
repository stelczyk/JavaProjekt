package game.player;

public class PlayerProfile {

    private final String playerNickname;
    private final int playerAge;

    public PlayerProfile(String playerNickname, int playerAge) {
        if (playerAge < 0 || playerAge > 100) {
            throw new IllegalArgumentException("Age must be between 0 and 100, got: " + playerAge);
        }
        this.playerNickname = playerNickname;
        this.playerAge = playerAge;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public int getPlayerAge() {
        return playerAge;
    }
}
