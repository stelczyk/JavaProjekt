package game.player;

import game.constants.GameConstants;
import game.player.attributes.CharacterAttributeType;
import game.player.attributes.PlayerAttributes;
import game.player.attributes.ShopAttributes;
import game.player.attributes.types.ArenaAttackPlayerAttributes;
import game.player.attributes.types.ArenaDefencePlayerAttributes;
import game.player.attributes.types.ArenaMovementPlayerAttributes;

public class PlayerAttribute implements PlayerAttributes, ShopAttributes,
        ArenaAttackPlayerAttributes, ArenaDefencePlayerAttributes, ArenaMovementPlayerAttributes {
    private int strength = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int defence = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int accuracy = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int stamina = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int brave = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int cunning = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int speed = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int pathology = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int valor = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    private int connections = GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public int getDefense() {
        return defence;
    }

    @Override
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public int getStamina() {
        return stamina;
    }

    @Override
    public int getBrave() {
        return brave;
    }

    @Override
    public int getCunning() {
        return cunning;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getPathology() {
        return pathology;
    }

    @Override
    public int getValor() {
        return valor;
    }

    @Override
    public int getConnections() {
        return connections;
    }

    /**
     * Kopiuje wszystkie atrybuty bezpośrednio ze źródła.
     * Używane przez DeepCloneUtils — szybsze niż N-krotne upgradeAttribute().
     */
    public void copyFrom(PlayerAttribute source) {
        this.strength    = source.strength;
        this.defence     = source.defence;
        this.accuracy    = source.accuracy;
        this.stamina     = source.stamina;
        this.brave       = source.brave;
        this.cunning     = source.cunning;
        this.speed       = source.speed;
        this.pathology   = source.pathology;
        this.valor       = source.valor;
        this.connections = source.connections;
    }

    public void upgradeAttribute(CharacterAttributeType type) {
        switch (type) {
            case STRENGTH    -> this.strength    += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case DEFENSE     -> this.defence     += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case ACCURACY    -> this.accuracy    += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case STAMINA     -> this.stamina     += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case BRAVE       -> this.brave       += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case CUNNING     -> this.cunning     += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case SPEED       -> this.speed       += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case PATHOLOGY   -> this.pathology   += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case VALOR       -> this.valor       += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            case CONNECTIONS -> this.connections += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
            default          -> throw new IllegalArgumentException("Unknown attribute type: " + type);
        }
    }

    private boolean canDowngrade(int currentValue) {
        return (currentValue - GameConstants.DEFAULT_UNBOOST_ATTRIBUTE) >= GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    }

    public void unUpgradeAttribute(CharacterAttributeType type) {
        switch (type) {
            case STRENGTH    -> { if (canDowngrade(this.strength))    this.strength    -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case DEFENSE     -> { if (canDowngrade(this.defence))     this.defence     -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case ACCURACY    -> { if (canDowngrade(this.accuracy))    this.accuracy    -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case STAMINA     -> { if (canDowngrade(this.stamina))     this.stamina     -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case BRAVE       -> { if (canDowngrade(this.brave))       this.brave       -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case CUNNING     -> { if (canDowngrade(this.cunning))     this.cunning     -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case SPEED       -> { if (canDowngrade(this.speed))       this.speed       -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case PATHOLOGY   -> { if (canDowngrade(this.pathology))   this.pathology   -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case VALOR       -> { if (canDowngrade(this.valor))       this.valor       -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            case CONNECTIONS -> { if (canDowngrade(this.connections)) this.connections -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE; }
            default          -> throw new IllegalArgumentException("Unknown attribute type: " + type);
        }
    }
}

