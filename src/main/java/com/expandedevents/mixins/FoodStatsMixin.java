package com.expandedevents.mixins;

import com.expandedevents.events.UpdateFoodStatsEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
    /**
     * @author Invadermonky
     * @reason
     */
    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void updateFoodStatsEventMixin(EntityPlayer player, CallbackInfo ci, @Share("statsEvent")LocalRef<UpdateFoodStatsEvent> localRef) {
        UpdateFoodStatsEvent event = new UpdateFoodStatsEvent(player);
        MinecraftForge.EVENT_BUS.post(event);
        localRef.set(event);
        if(event.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @author Invadermonky
     * @reason modifying the amount of food saturation lost per tick.
     */
    @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(floatValue = 1.0f, ordinal = 0)
    )
    private float modifySaturationDecayMixin(float original, @Share("statsEvent")LocalRef<UpdateFoodStatsEvent> localRef) {
        return localRef.get().getSaturationLoss();
    }

    /**
     * @author Invadermonky
     * @reason modifying the amount of exhaustion incurred when healing slowly while food value is >= 10.
     */
    @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(floatValue = 6.0f, ordinal = 0)
    )
    private float modifySlowHealExhaustionMixin(float original, @Share("statsEvent")LocalRef<UpdateFoodStatsEvent> localRef) {
        return localRef.get().getSlowHealExhaustion();
    }

    /**
     * @author Invadermonky
     * @reason modifying the amount of exhaustion incurred when healing quickly while food value is >= 18.
     */
    @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(floatValue = 6.0f, ordinal = 2)
    )
    private float modifyFastHealExhaustionMixin(float original, @Share("statsEvent")LocalRef<UpdateFoodStatsEvent> localRef) {
        return localRef.get().getFastHealExhaustion();
    }

    /**
     * @author Invadermonky
     * @reason enabling or disabling natural regeneration regardless of game rule setting
     */
    @ModifyExpressionValue(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z")
    )
    private boolean doNaturalRegenerationMixin(boolean original, @Share("statsEvent")LocalRef<UpdateFoodStatsEvent> localRef) {
        return localRef.get().getNaturalRegenerationEnabled();
    }
}
