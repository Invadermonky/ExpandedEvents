package com.expandedevents.mixins;

import com.expandedevents.utils.IEnchantmentTableEvent;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(GuiEnchantment.class)
public abstract class GuiEnchantmentMixin extends GuiContainer {
    @Shadow @Final private ContainerEnchantment container;

    public GuiEnchantmentMixin(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    /**
     * @author Invadermonky
     * @reason Modifies the logic to allow no required level enchanting when drawing the enchanting slot views.
     */
    @Definition(id = "k1", local = @Local(type = int.class, ordinal = 8))
    @Expression("k1 == 0")
    @ModifyExpressionValue(method = "drawGuiContainerBackgroundLayer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean allowNoLevelEnchanting(boolean original, @Local(ordinal = 8) int enchantLevel) {
        return enchantLevel < 0;
    }

    /**
     * @author Invadermonky
     * @reason Performs additional event fired logic checks to determine the correct enchant logic
     *         when drawing the enchantment slot views.
     */
    @Definition(id = "k", local = @Local(type = int.class, ordinal = 4))
    @Definition(id = "l", local = @Local(type = int.class, ordinal = 5))
    @Expression("k < l + 1")
    @ModifyExpressionValue(method = "drawGuiContainerBackgroundLayer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean performEventChecks$drawBackground(boolean original, @Local(ordinal = 5) int enchantTier) {
        if(this.container instanceof IEnchantmentTableEvent) {
            EntityPlayer player = this.mc.player;
            IEnchantmentTableEvent helper = (IEnchantmentTableEvent) this.container;
            int reagentCount = this.container.getLapisAmount();
            int reagentCost = helper.ee$getReagentCost(enchantTier);
            int levelRequired = Math.max(this.container.enchantLevels[enchantTier], helper.ee$getEnchantmentLevelCost(enchantTier));
            return reagentCount < reagentCost || player.experienceLevel < levelRequired;
        }
        return original;
    }

    /**
     * @author Invadermonky
     * @reason Allows tooltip logic to run when the enchanting tier required level is set to 0.
     */
    @Definition(id = "k", local = @Local(type = int.class, ordinal = 4))
    @Expression("k > 0")
    @ModifyExpressionValue(method = "drawScreen", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean allowNoLevelEnchanting$drawScreen(boolean original, @Local(ordinal = 3) int enchantTier) {
        return this.container.enchantLevels[enchantTier] >= 0;
    }

    /**
     * @author Invadermonky
     * @reason Changes the reagent cost tooltip display to instead use the event reagent cost.
     */
    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 1
            )
    )
    private void displayEventReagentCost(int mouseX, int mouseY, float partialTicks, CallbackInfo ci, @Local(ordinal = 3) int enchantTier, @Local(ordinal = 6) LocalIntRef ref) {
        if(this.container instanceof IEnchantmentTableEvent) {
            ref.set(((IEnchantmentTableEvent) this.container).ee$getReagentCost(enchantTier));
        }
    }

    /**
     * @author Invadermonky
     * @reason Changes the level cost tooltip display to instead use the event level cost.
     */
    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void displayEventLevelCost(int mouseX, int mouseY, float partialTicks, CallbackInfo ci, @Local(ordinal = 3) int enchantTier, @Local(ordinal = 6)LocalIntRef ref) {
        if(this.container instanceof IEnchantmentTableEvent) {
            ref.set(((IEnchantmentTableEvent) this.container).ee$getEnchantmentLevelCost(enchantTier));
        }
    }
}
