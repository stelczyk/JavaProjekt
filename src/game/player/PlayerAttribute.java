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

    public void upgradeAttribute(CharacterAttributeType type) {
        switch (type) {
            case STRENGTH:
                this.strength += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Strength upgraded! Currently value: " + this.strength);
                break;
            case DEFENSE:
                this.defence += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Defense upgraded! Currently value: " + this.defence);
                break;
            case ACCURACY:
                this.accuracy += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Accuracy upgraded! Currently value: " + this.accuracy);
                break;
            case STAMINA:
                this.stamina += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Stamina upgraded! Currently value: " + this.stamina);
                break;
            case BRAVE:
                this.brave += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Brave upgraded! Currently value: " + this.brave);
                break;
            case CUNNING:
                this.cunning += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Cunning upgraded! Currently value: " + this.cunning);
                break;
            case SPEED:
                this.speed += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Speed upgraded! Currently value: " + this.speed);
                break;
            case PATHOLOGY:
                this.pathology += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Pathology upgraded! Currently value: " + this.pathology);
                break;
            case VALOR:
                this.valor += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Valor upgraded! Currently value: " + this.valor);
                break;
            case CONNECTIONS:
                this.connections += GameConstants.DEFAULT_BOOST_ATTRIBUTE;
                System.out.println("Connections upgraded! Currently value: " + this.connections);
                break;
            default:
                throw new IllegalArgumentException("Unknown attribute type");
        }
    }

    private boolean canDowngrade(int currentValue) {
        return (currentValue - GameConstants.DEFAULT_UNBOOST_ATTRIBUTE) >= GameConstants.DEFAULT_START_ATTRIBUTE_VALUE;
    }

    public void unUpgradeAttribute(CharacterAttributeType type) {
        switch (type) {
            case STRENGTH:
                if (canDowngrade(this.strength)) {
                    this.strength -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Strength unupgraded! Currently value: " + this.strength);
                } else {
                    System.out.println("Cannot downgrade Strength below starting value!");
                }
                break;
            case DEFENSE:
                if (canDowngrade(this.defence)) {
                    this.defence -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Defense unupgraded! Currently value: " + this.defence);
                } else {
                    System.out.println("Cannot downgrade Defense below starting value!");
                }
                break;
            case ACCURACY:
                if (canDowngrade(this.accuracy)) {
                    this.accuracy -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Accuracy unupgraded! Currently value: " + this.accuracy);
                } else {
                    System.out.println("Cannot downgrade Accuracy below starting value!");
                }
                break;
            case STAMINA:
                if (canDowngrade(this.stamina)) {
                    this.stamina -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Stamina unupgraded! Currently value: " + this.stamina);
                } else {
                    System.out.println("Cannot downgrade Stamina below starting value!");
                }
                break;
            case BRAVE:
                if (canDowngrade(this.brave)) {
                    this.brave -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Brave unupgraded! Currently value: " + this.brave);
                } else {
                    System.out.println("Cannot downgrade Brave below starting value!");
                }
                break;
            case CUNNING:
                if (canDowngrade(this.cunning)) {
                    this.cunning -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Cunning unupgraded! Currently value: " + this.cunning);
                } else {
                    System.out.println("Cannot downgrade Cunning below starting value!");
                }
                break;
            case SPEED:
                if (canDowngrade(this.speed)) {
                    this.speed -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Speed unupgraded! Currently value: " + this.speed);
                } else {
                    System.out.println("Cannot downgrade Speed below starting value!");
                }
                break;
            case PATHOLOGY:
                if (canDowngrade(this.pathology)) {
                    this.pathology -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Pathology unupgraded! Currently value: " + this.pathology);
                } else {
                    System.out.println("Cannot downgrade Pathology below starting value!");
                }
                break;
            case VALOR:
                if (canDowngrade(this.valor)) {
                    this.valor -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Valor unupgraded! Currently value: " + this.valor);
                } else {
                    System.out.println("Cannot downgrade Valor below starting value!");
                }
                break;
            case CONNECTIONS:
                if (canDowngrade(this.connections)) {
                    this.connections -= GameConstants.DEFAULT_UNBOOST_ATTRIBUTE;
                    System.out.println("Connections unupgraded! Currently value: " + this.connections);
                } else {
                    System.out.println("Cannot downgrade Connections below starting value!");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown attribute type");
        }
    }
}

