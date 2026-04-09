package com.expandedevents.api.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for attribute modifying items that can be worn in baubles slots.
 */
public interface IAttributeBauble extends IBauble {

    /**
     * An implementation of {@link Item#getAttributeModifiers(EntityEquipmentSlot, ItemStack)} for use with bauble items.
     * <p>
     * For attribute values that do not stack, define a static UUID. For attribute values that stack with similar baubles (such as rings),
     * do not define an attribute modifier UUID.
     * @param baubleType The bauble type. Baubles that use Bubbles <code>IBaubleType</code> implementation should return {@link BaubleType#TRINKET}.
     * @param stack The ItemStack object.
     * @return A list of attribute modifiers applied when the bauble is equipped.
     */
    @NotNull
    Multimap<String, AttributeModifier> getBaubleAttributeModifiers(BaubleType baubleType, ItemStack stack);
}
