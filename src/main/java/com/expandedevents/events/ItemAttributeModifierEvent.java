package com.expandedevents.events;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This event is fired when the attributes for an ItemStack are being calculated.
 * Attributes are calculated on the server when equipping and unequipping an item to add and remove attributes respectively, both must be consistent.
 * Attributes are calculated on the client when rendering an item's tooltip to show relevant attributes.
 * <p>
 * Note that this event is fired regardless of if the stack has NBT overriding attributes or not. If your attribute should be
 * ignored when attributes are overridden, you can check for the presence of the {@code AttributeModifiers} tag.
 * <p>
 * This event is {@link Cancelable}.
 * <br>
 * This Event does not have a {@link Result}.
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public class ItemAttributeModifierEvent extends Event {
    private final ItemStack stack;
    private final EntityEquipmentSlot slotType;
    private final Multimap<String, AttributeModifier> originalModifiers;
    private Multimap<String, AttributeModifier> unmodifiableModifiers;
    @Nullable
    private Multimap<String, AttributeModifier> modifiableModifiers;

    public ItemAttributeModifierEvent(ItemStack stack, EntityEquipmentSlot slotType, Multimap<String, AttributeModifier> modifiers) {
        this.stack = stack;
        this.slotType = slotType;
        this.originalModifiers = modifiers;
        this.unmodifiableModifiers = modifiers;
    }

    /**
     * Returns an unmodifiable view of the attribute multimap. Use other methods from this event to modify the attributes map.
     * Note that adding attributes based on existing attributes may lead to inconsistent results between the tooltip (client)
     * and the actual attributes (server) if the listener order is different. Using {@link #getOriginalModifiers()} instead will give more consistent results.
     */
    public Multimap<String, AttributeModifier> getModifiers() {
        return this.unmodifiableModifiers;
    }

    /**
     * Returns the attribute map before any changes from other event listeners was made.
     */
    public Multimap<String, AttributeModifier> getOriginalModifiers()
    {
        return this.originalModifiers;
    }

    /**
     * Gets a modifiable map instance, creating it if the current map is currently unmodifiable
     */
    private Multimap<String, AttributeModifier>  getModifiableMap()
    {
        if (this.modifiableModifiers == null) {
            this.modifiableModifiers = HashMultimap.create(this.originalModifiers);
            this.unmodifiableModifiers = Multimaps.unmodifiableMultimap(this.modifiableModifiers);
        }
        return this.modifiableModifiers;
    }

    /**
     * Adds a new attribute modifier to the given stack.
     * Modifier must have a consistent UUID for consistency between equipping and unequipping items.
     * Modifier name should clearly identify the mod that added the modifier.
     * @param attribute  Attribute
     * @param modifier   Modifier instance.
     * @return  True if the attribute was added, false if it was already present
     */
    public boolean addModifier(IAttribute attribute, AttributeModifier modifier) {
        return this.addModifier(attribute.getName(), modifier);
    }

    /**
     * Adds a new attribute modifier to the given stack.
     * Modifier must have a consistent UUID for consistency between equipping and unequipping items.
     * Modifier name should clearly identify the mod that added the modifier.
     * @param attributeName  The attribute name
     * @param modifier   Modifier instance.
     * @return  True if the attribute was added, false if it was already present
     */
    public boolean addModifier(String attributeName, AttributeModifier modifier) {
        return this.getModifiableMap().put(attributeName, modifier);
    }

    /**
     * Removes a single modifier for the given attribute
     * @param attribute  Attribute
     * @param modifier   Modifier instance
     * @return  True if an attribute was removed, false if no change
     */
    public boolean removeModifier(IAttribute attribute, AttributeModifier modifier) {
        return this.removeModifier(attribute.getName(), modifier);
    }

    /**
     * Removes a single modifier for the given attribute
     * @param attributeName  The attribute name
     * @param modifier   Modifier instance
     * @return  True if an attribute was removed, false if no change
     */
    public boolean removeModifier(String attributeName, AttributeModifier modifier) {
        return this.getModifiableMap().remove(attributeName, modifier);
    }

    /**
     * Removes all modifiers for the given attribute
     * @param attribute  Attribute
     * @return  Collection of removed modifiers
     */
    public Collection<AttributeModifier> removeAttribute(IAttribute attribute) {
        return this.removeAttribute(attribute.getName());
    }

    /**
     * Removes all modifiers for the given attribute
     * @param attributeName  The attribute name
     * @return  Collection of removed modifiers
     */
    public Collection<AttributeModifier> removeAttribute(String attributeName) {
        return this.getModifiableMap().removeAll(attributeName);
    }

    /**
     * Removes all modifiers for all attributes
     */
    public void clearModifiers() {
        this.getModifiableMap().clear();
    }

    /** Gets the slot containing this stack */
    public EntityEquipmentSlot getSlotType() {
        return this.slotType;
    }

    /** Gets the item stack instance */
    public ItemStack getItemStack() {
        return this.stack;
    }
}
