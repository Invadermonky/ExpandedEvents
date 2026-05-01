package com.expandedevents.mixins;

import com.expandedevents.api.event.PlayerShieldDisabledEvent;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {
    @Unique
    @SuppressWarnings("DataFlowIssue")
    private EntityPlayer expandedEvents$getThis() {
        return (EntityPlayer) (Object) this;
    }

    /*
    ##############################################
    Player disable shield event
    ##############################################
    */

    @Inject(
            method = "disableShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;getCooldownTracker()Lnet/minecraft/util/CooldownTracker;"
            ),
            cancellable = true
    )
    private void disableShieldEventMixin(boolean disableShield, CallbackInfo ci, @Share("disableShieldEvent")LocalRef<PlayerShieldDisabledEvent> localRef) {
        PlayerShieldDisabledEvent event = new PlayerShieldDisabledEvent(this.expandedEvents$getThis(), 100);
        MinecraftForge.EVENT_BUS.post(event);
        localRef.set(event);
        if(event.isCanceled()) {
            ci.cancel();
        }
    }

    @ModifyConstant(
            method = "disableShield",
            constant = @Constant(intValue = 100, ordinal = 0)
    )
    private int modifyDisableDurationMixin(int original, @Share("disableShieldEvent")LocalRef<PlayerShieldDisabledEvent> localRef) {
        return Math.max(1, localRef.get().getShieldDisableDuration());
    }
}
