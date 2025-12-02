package com.expandedevents.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This Event is fired whenever a player gains experience via {@link EntityPlayer#addExperience(int)}.
 * This Event is {@link Cancelable}.
 * This Event does not have a {@link Result}.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class PlayerExperienceGainEvent extends PlayerEvent {
    private int amount;
    private final boolean isLevelUp;

    public PlayerExperienceGainEvent(EntityPlayer player, int expAmount) {
        super(player);
        this.amount = expAmount;
        this.isLevelUp = (float) expAmount / player.xpBarCap() >= 1.0f;
    }

    public int getExperienceAmount() {
        return this.amount;
    }

    public void setExperienceAmount(int amount) {
        this.amount = amount;
    }

    public boolean isLevelUp() {
        return this.isLevelUp;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        this.amount = 0;
    }
}
