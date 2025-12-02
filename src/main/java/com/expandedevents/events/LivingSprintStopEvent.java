package com.expandedevents.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * LivingSprintStopEvent is fired when an entity stops sprinting. Fired when {@link EntityLivingBase#setSprinting(boolean)}
 * receives a false value.
 * <p>
 * This event is {@link Cancelable}.
 * This Event does not have a {@link Event.Result}.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingSprintStopEvent extends LivingEvent {
    public LivingSprintStopEvent(EntityLivingBase entity) {
        super(entity);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
