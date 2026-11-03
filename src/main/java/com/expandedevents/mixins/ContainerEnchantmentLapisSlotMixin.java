package com.expandedevents.mixins;

import com.expandedevents.tags.ItemTags;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.inventory.ContainerEnchantment$3")
public class ContainerEnchantmentLapisSlotMixin {
    /**
     * @author Invadermonky
     * @reason Modifies the logic of the Enchanting Table lapis slot so it can accept other items.
     *         This mixin only modifies mouse click interaction but does not change shift-click
     *         interactions.
     */
    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void validateEnchantingReagentMixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ItemTags.isEnchantingReagent(stack));
    }
}
