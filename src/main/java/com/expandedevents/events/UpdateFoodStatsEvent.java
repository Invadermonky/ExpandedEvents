package com.expandedevents.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * UpdateFoodStatsEvent is fired when a player's food value is updated. Fired whenever {@link FoodStats#onUpdate(EntityPlayer)}
 * is ticked.
 * <p>
 * This event is {@link Cancelable}.
 * This Event does not have a {@link Result}.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class UpdateFoodStatsEvent extends PlayerEvent {
    private final FoodStats foodStats;
    private boolean naturalRegen;
    private float saturationLoss;
    private float slowHealExhaustion;
    private float fastHealExhaustion;

    public UpdateFoodStatsEvent(EntityPlayer player) {
        super(player);
        this.foodStats = player.getFoodStats();
        this.naturalRegen = this.getEntityPlayer().world.getGameRules().getBoolean("naturalRegeneration");
        this.saturationLoss = 1.0f;
        this.slowHealExhaustion = 6.0f;
        this.fastHealExhaustion = 6.0f;
    }

    /**
     * A helper method for retrieving player food stats. This value is the same as {@link EntityPlayer#getFoodStats()}.
     */
    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    /**
     * @return whether natural healing game rule is enabled. If no other subscribers modify this value,
     * its value is determined by the "naturalRegeneration" game rule.
     */
    public boolean getNaturalRegenerationEnabled() {
        return this.naturalRegen;
    }

    /**
     * Overrides the default "naturalRegeneration" game rule, allowing natural regeneration to be toggled
     * on and off as needed.
     */
    public void setNaturalRegenerationEnabled(boolean naturalRegen) {
        this.naturalRegen = naturalRegen;
    }

    /**
     * @return the food saturation loss per tick
     */
    public float getSaturationLoss() {
        return this.saturationLoss;
    }

    /**
     * Modifies the food saturation loss per tick.
     */
    public void setSaturationLoss(float saturationLoss) {
        this.saturationLoss = saturationLoss;
    }

    /**
     * @return the amount of exhaustion incurred by natural regeneration while food level is >= 10.
     */
    public float getSlowHealExhaustion() {
        return this.slowHealExhaustion;
    }

    /**
     * Modifies the amount of exhaustion incurred by natural healing while food level is >= 10.
     */
    public void setSlowHealExhaustion(float slowHealExhaustion) {
        this.slowHealExhaustion = slowHealExhaustion;
    }

    /**
     * @return the amount of exhaustion incurred by natural regeneration while food level is >= 18.
     */
    public float getFastHealExhaustion() {
        return this.fastHealExhaustion;
    }

    /**
     * Modifies the amount of exhaustion incurred by natural healing while food level is >= 18.
     */
    public void setFastHealExhaustion(float fastHealExhaustion) {
        this.fastHealExhaustion = fastHealExhaustion;
    }
}
