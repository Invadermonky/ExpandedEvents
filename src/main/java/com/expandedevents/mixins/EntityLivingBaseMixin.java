package com.expandedevents.mixins;

import com.expandedevents.events.LivingSprintStartEvent;
import com.expandedevents.events.LivingSprintStopEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    @Unique
    @SuppressWarnings("DataFlowIssue")
    private EntityLivingBase expandedEvents$getThis() {
        return (EntityLivingBase) (Object) this;
    }

    /**
     * @author Invadermonky
     * @reason Adding a sprinting start event handler and a sprinting stop event listener.
     */
    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void injectSprintingEventMixin(boolean sprinting, CallbackInfo ci) {
        LivingEvent event = sprinting ? new LivingSprintStartEvent(this.expandedEvents$getThis()) : new LivingSprintStopEvent(this.expandedEvents$getThis());
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled()) {
            ci.cancel();
        }
    }
}
