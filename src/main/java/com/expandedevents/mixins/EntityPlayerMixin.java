package com.expandedevents.mixins;

import com.expandedevents.events.PlayerExperienceGainEvent;
import com.expandedevents.events.PlayerLevelUpEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: Figure out why these methods aren't remapping correctly in obfuscated environments.
@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {
    @Unique
    @SuppressWarnings("DataFlowIssue")
    private EntityPlayer expandedEvents$getThis() {
        return (EntityPlayer) (Object) this;
    }

    /*
    ##############################################
    Player level-up listener
    ##############################################
    */

    /**
     * @author Invadermonky
     * @reason Adds an event listener to player level ups.
     */
    @Inject(method = "addExperienceLevel", at = @At("HEAD"), remap = false)
    private void onLevelUpEventMixin(int levels, CallbackInfo ci) {
        PlayerLevelUpEvent event = new PlayerLevelUpEvent(this.expandedEvents$getThis(), levels);
        MinecraftForge.EVENT_BUS.post(event);
    }

    /*
    ##############################################
    Player experience gain event handler
    ##############################################
    */

    /**
     * @author Invadermonky
     * @reason Adds a cancellable event handler whenever a player gains experience.
     */
    @Inject(method = "addExperience", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGainExperienceEventMixin(int amount, CallbackInfo ci, @Local(argsOnly = true, ordinal = 0)LocalIntRef amountRef) {
        PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this.expandedEvents$getThis(), amount);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled()) {
            ci.cancel();
            return;
        }
        amountRef.set(event.getExperienceAmount());
    }
}
