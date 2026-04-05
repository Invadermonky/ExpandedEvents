package com.expandedevents.mixins;

import com.expandedevents.events.ItemAttributeModifierEvent;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique
    private ItemStack expandedEvents$getThis() {
        return (ItemStack) (Object) this;
    }

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"))
    private Multimap<String, AttributeModifier> itemAttributeModifiersEventMixin(Multimap<String, AttributeModifier> original, @Local(argsOnly = true, ordinal = 0) EntityEquipmentSlot slotType) {
        ItemAttributeModifierEvent event = new ItemAttributeModifierEvent(this.expandedEvents$getThis(), slotType, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }
}
