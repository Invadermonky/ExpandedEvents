package com.expandedevents.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * LivingBlockDamageEvent is fired whenever a living entity blocks damage with an item. Fired when
 * {@link EntityLivingBase#attackEntityFrom(DamageSource, float)} detects successfully blocked
 * damage.
 * <p>
 * This event is not {@link Cancelable}.<br>
 * This event has a {@link Result}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Event.HasResult
public class LivingBlockDamageEvent extends LivingEvent {
    private final ItemStack shield;
    private final EnumHand hand;
    private final DamageSource source;
    private Result blockResult;
    private float shieldDamage;
    private float entityDamage;
    private int shieldCooldown;

    public LivingBlockDamageEvent(EntityLivingBase entity, ItemStack shield, EnumHand hand, DamageSource source, float shieldDamage) {
        super(entity);
        this.shield = shield;
        this.hand = hand;
        this.source = source;
        this.blockResult = Result.DEFAULT;
        this.shieldDamage = shieldDamage;
        this.entityDamage = 0;
        this.shieldCooldown = 0;
    }

    /**
     * Returns the shield block result from this event. A value of {@link Result#ALLOW} or {@link Result#DEFAULT} will
     * allow normal processing. {@link Result#DENY} will prevent the shield from blocking the damage.
     */
    @Override
    public Result getResult() {
        return blockResult;
    }

    /**
     * Sets the shield block result from this event. A value of {@link Result#ALLOW} or {@link Result#DEFAULT} will
     * allow normal processing. {@link Result#DENY} will prevent the shield from blocking the damage.
     * @param value The new shield block result
     */
    @Override
    public void setResult(Result value) {
        this.blockResult = value;
    }

    /**
     * Returns the item used to block the damage.
     */
    public ItemStack getShield() {
        return shield;
    }

    /**
     * Returns the currently active hand.
     */
    public EnumHand getHand() {
        return hand;
    }

    /**
     * Returns the damage source.
     */
    public DamageSource getSource() {
        return source;
    }

    /**
     * Returns the amount of damage that will be dealt to the entity after a successful block.
     */
    public float getEntityDamage() {
        return entityDamage;
    }

    /**
     * Sets a new amount of damage the entity will take after a successful block.
     * @param entityDamage The amount of damage the entity will take
     */
    public void setEntityDamage(float entityDamage) {
        this.entityDamage = entityDamage;
    }

    /**
     * Returns the amount of durability damage that will be applied to the shield.
     */
    public float getShieldDamage() {
        return shieldDamage;
    }

    /**
     * Sets the amount of durability damage the shield will take after a successful block.
     * @param shieldDamage The shield durability damage
     */
    public void setShieldDamage(float shieldDamage) {
        this.shieldDamage = shieldDamage;
    }

    /**
     * Returns the shield cooldown after a successful block.
     */
    public int getShieldCooldown() {
        return shieldCooldown;
    }

    /**
     * Sets the cooldown of the shield after blocking damage. This method should only be used if you want to
     * create custom shield disable durations.
     * <p>
     * This <strong>SHOULD NOT</strong> be used to attempt to cancel the shield cooldown.
     * Use {@link PlayerShieldDisabledEvent} instead.
     * @param shieldCooldown The duration the shield will be disabled
     */
    public void setShieldCooldown(int shieldCooldown) {
        this.shieldCooldown = shieldCooldown;
    }
}
