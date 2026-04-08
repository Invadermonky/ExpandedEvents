package com.expandedevents.mixins;

import com.expandedevents.ExpandedEvents;
import com.expandedevents.events.ItemAttributeModifierEvent;
import com.expandedevents.utils.BaublesAttributeHelper;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

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

    @SideOnly(Side.CLIENT)
    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/EntityEquipmentSlot;values()[Lnet/minecraft/inventory/EntityEquipmentSlot;"))
    private void addBaubleAttributesTooltipMixin(@Nullable EntityPlayer playerIn, ITooltipFlag advanced, CallbackInfoReturnable<List<String>> cir, @Local(ordinal = 0) List<String> tooltip, @Local(ordinal = 0) int hideFlags) {
        if(ExpandedEvents.isBaublesLoaded) {
            BaublesAttributeHelper.addBaublesAttributeTooltip(this.expandedEvents$getThis(), playerIn, tooltip, hideFlags);
        }
    }
}
