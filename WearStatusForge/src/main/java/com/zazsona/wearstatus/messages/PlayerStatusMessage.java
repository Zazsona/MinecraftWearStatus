package com.zazsona.wearstatus.messages;

public class PlayerStatusMessage extends Message
{
    private float health;
    private float healthChange;
    private float maxHealth;
    private int hunger;

    public PlayerStatusMessage(float health, float healthChange, float maxHealth, int hunger)
    {
        super("PLAYER_STATUS");
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
     * Gets healthChange, clamping it off if it extends beyond 0 or maximum health
     * @return clamped healthChange
     */
    public float getClampedHealthChange()
    {
        float resultingHealth = health+healthChange;
        if (resultingHealth < 0.0f)
            return healthChange-resultingHealth;
        else if (resultingHealth > maxHealth)
            return healthChange-(resultingHealth-maxHealth);
        else
            return healthChange;
    }
}
