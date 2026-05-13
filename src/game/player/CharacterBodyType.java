package game.player;

public enum CharacterBodyType {
    DEFAULTBODY(180, 75),
    KARKBODY(195,110),
    DILERBODY(165,60),
    KONFIDENTBODY(170,70),
    GNIAZDOWYBODY(180,85);

    private final int characterBodyHeight;
    private final int characterBodyWeight;

    CharacterBodyType(int characterBodyHeight, int characterBodyWeight) {
        this.characterBodyHeight = characterBodyHeight;
        this.characterBodyWeight = characterBodyWeight;
    }
    public int getCharacterBodyHeight() {   return characterBodyHeight; }
    public int getCharacterBodyWeight() {   return characterBodyWeight; }

}
