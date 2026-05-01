package com.expandedevents.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerDisableShieldEvent is fired whenever a player is hit by an attack that will disable their shield.
 * Fired when {@link EntityPlayer#disableShield(boolean)} receives a true value or when a shield is randomly
 * disabled when taking damage.
 * <p>
 * This event is {@link Cancelable}.<br>
 * This event does not have a {@link Result}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class PlayerShieldDisabledEvent extends PlayerEvent {
    private int shieldDisableDuration;

    public PlayerShieldDisabledEvent(EntityPlayer player, int disableDuration) {
        super(player);
        this.shieldDisableDuration = disableDuration;
    }

    /**
     * Returns the duration the shield will be disabled.
     */
    public int getShieldDisableDuration() {
        return shieldDisableDuration;
    }

    /**
     * Sets the duration the shield will be disabled. For more control over this value, use {@link LivingBlockDamageEvent}.
     * @param duration The shield disable duration value
     */
    public void setShieldDisableDuration(int duration) {
        this.shieldDisableDuration = duration;
    }
}
