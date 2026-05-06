package game.player;

public class PlayerProfile {

    private final String playerNickname; // zakładam że w grze nie bedzie możliwosci zmiany kryptonimy
    private final int playerAge; // zakładam ze w grze nie bedzie mozliwosci zmiany wieku (być może potem dodamy zmiane wieku pomiedzy levelami)
    private final int playerHeight;
    private int playerWeight; // waga++ -> siła++

    public PlayerProfile(String playerNickname, int playerAge) {
        if (playerAge < 0 || playerAge > 100)
        {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        this.playerNickname = playerNickname;
        this.playerAge = playerAge;
        this.playerHeight = CharacterBodyType.DEAFULTBODY.getCharacterBodyHeight();
        this.playerWeight = CharacterBodyType.DEAFULTBODY.getCharacterBodyWeight();

    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public int getPlayerAge() {
        return playerAge;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }

    public int getPlayerWeight() {
        return playerWeight;
    }

    public void setPlayerWeight(int playerWeight) {
        this.playerWeight = playerWeight;
    }
}
