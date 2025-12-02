package com.expandedevents.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This Event is fired whenever a player gains levels via {@link EntityPlayer#addExperienceLevel(int)}.
 * This Event is not {@link Cancelable}.
 * This Event does not have a result.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class PlayerLevelUpEvent extends PlayerEvent {
    private int levels;

    public PlayerLevelUpEvent(EntityPlayer player, int levels) {
        super(player);
        this.levels = levels;
    }

    /**
     * @return the number of levels gained by the player.
     */
    public int getExperienceLevels() {
        return this.levels;
    }
}
