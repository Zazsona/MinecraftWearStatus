package com.zazsona.wearstatus.messages;

public class PlayerStatusMessage extends Message
{
    public static final String MESSAGE_TYPE = "PLAYER_STATUS";
    private float health;
    private float healthChange;
    private float maxHealth;
    private int hunger;

    public PlayerStatusMessage(float health, float healthChange, float maxHealth, int hunger)
    {
        super(MESSAGE_TYPE);
        this.health = health;
        this.healthChange = healthChange;
        this.maxHealth = maxHealth;
        this.hunger = hunger;
    }

    /**
     * Gets health
     * @return health
     */
    public float getHealth()
    {
        return health;
    }

    /**
     * Gets hunger
     * @return hunger
     */
    public int getHunger()
    {
        return hunger;
    }

    /**
     * Gets healthChange
     *
     * @return healthChange
     */
    public float getHealthChange()
    {
        return healthChange;
    }

    /**
     * Gets maxHealth
     *
     * @return maxHealth
     */
    public float getMaxHealth()
    {
        return maxHealth;
    }

    /**
     * Sets the value of health
     *
     * @param health the value to set
     */
    public void setHealth(float health)
    {
        this.health = health;
    }

    /**
     * Sets the value of healthChange
     *
     * @param healthChange the value to set
     */
    public void setHealthChange(float healthChange)
    {
        this.healthChange = healthChange;
    }

    /**
     * Sets the value of maxHealth
     *
     * @param maxHealth the value to set
     */
    public void setMaxHealth(float maxHealth)
    {
        this.maxHealth = maxHealth;
    }

    /**
     * Sets the value of hunger
     *
     * @param hunger the value to set
     */
    public void setHunger(int hunger)
    {
        this.hunger = hunger;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof PlayerStatusMessage)
        {
            PlayerStatusMessage compareMessage = (PlayerStatusMessage) o;
            return
                    compareMessage.health == health
                    && compareMessage.healthChange == healthChange
                    && compareMessage.maxHealth == maxHealth
                    && compareMessage.hunger == hunger;
        }
        return false;
    }
}
