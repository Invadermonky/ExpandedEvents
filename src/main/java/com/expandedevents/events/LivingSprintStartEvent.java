package com.expandedevents.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * LivingSprintStartEvent is fired when an entity starts sprinting. Fired when {@link EntityLivingBase#setSprinting(boolean)}
 * receives a true value.
 * <p>
 * This event is {@link Cancelable}.
 * This Event does not have a {@link Result}.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingSprintStartEvent extends LivingEvent {
    public LivingSprintStartEvent(EntityLivingBase entity) {
        super(entity);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
