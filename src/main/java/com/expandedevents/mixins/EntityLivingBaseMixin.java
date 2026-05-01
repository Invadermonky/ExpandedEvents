package com.expandedevents.mixins;

import com.expandedevents.api.event.LivingBlockDamageEvent;
import com.expandedevents.api.event.LivingSprintStartEvent;
import com.expandedevents.api.event.LivingSprintStopEvent;
import com.expandedevents.api.event.PlayerShieldDisabledEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    @Unique
    @SuppressWarnings("DataFlowIssue")
    private EntityLivingBase expandedEvents$getThis() {
        return (EntityLivingBase) (Object) this;
    }

    /*
    ##############################################
    Entity set sprinting event handler
    ##############################################
    */

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

    /*
    ##############################################
    Entity block damage event handlers
    ##############################################
    */

    /**
     * @author Invadermonky
     * @reason Adds an event that occurs whenever an entity blocks damage with a shield.
     */
    @ModifyExpressionValue(
            method = "attackEntityFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;canBlockDamageSource(Lnet/minecraft/util/DamageSource;)Z"
            )
    )
    private boolean injectShieldBlockEventMixin(boolean original, @Local(ordinal = 0, argsOnly = true) DamageSource source, @Local(ordinal = 0, argsOnly = true) LocalFloatRef amountRef, @Share("blockEvent")LocalRef<LivingBlockDamageEvent> localRef) {
        if(original) {
            EntityLivingBase entity = this.expandedEvents$getThis();
            EnumHand activeHand = entity.getActiveHand();
            ItemStack blockingStack = entity.getActiveItemStack();

            LivingBlockDamageEvent event = new LivingBlockDamageEvent(entity, blockingStack, activeHand, source, amountRef.get());
            MinecraftForge.EVENT_BUS.post(event);
            amountRef.set(event.getShieldDamage());
            localRef.set(event);
            if(event.getShieldCooldown() > 0 && entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).getCooldownTracker().setCooldown(blockingStack.getItem(), event.getShieldCooldown());
            }
            return event.getResult() == Event.Result.DEFAULT ? original : event.getResult() == Event.Result.ALLOW;
        }
        return original;
    }

    /**
     * @author Invadermonky
     * @reason Modifies entity damage taken after blocking damage with a shield using the previous {@link LivingBlockDamageEvent} event.
     */
    @ModifyConstant(
            method = "attackEntityFrom",
            constant = @Constant(floatValue = 0, ordinal = 2)
    )
    private float modifyShieldBlockDamageTakenMixin(float original, @Share("blockEvent")LocalRef<LivingBlockDamageEvent> localRef) {
        return localRef.get().getEntityDamage();
    }

    /**
     * @author Invadermonky
     * @reason Handles adding a shield cooldown following the {@link LivingBlockDamageEvent}.
     */
    @Inject(
            method = "attackEntityFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;blockUsingShield(Lnet/minecraft/entity/EntityLivingBase;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void addBlockingCooldownMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Share("blockEvent")LocalRef<LivingBlockDamageEvent> localRef) {
        if(this.expandedEvents$getThis() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) this.expandedEvents$getThis();
            ItemStack shield = localRef.get().getShield();
            if (!shield.isEmpty()) {
                int cooldown = localRef.get().getShieldCooldown();
                if(cooldown > 0) {
                    PlayerShieldDisabledEvent event = new PlayerShieldDisabledEvent(player, cooldown);
                    MinecraftForge.EVENT_BUS.post(event);
                    if(!event.isCanceled()) {
                        player.getCooldownTracker().setCooldown(shield.getItem(), event.getShieldDisableDuration());
                        player.resetActiveHand();
                        player.world.setEntityState(player, (byte) 30);
                    }
                }
            }
        }
    }
}
