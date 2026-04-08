package com.expandedevents.capabilities;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IAttributeBauble extends IBauble {
    @NotNull
    Multimap<String, AttributeModifier> getBaubleAttributeModifiers(BaubleType baubleType, ItemStack stack);
}
