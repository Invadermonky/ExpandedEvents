package com.expandedevents.mixins;

import com.expandedevents.tags.BlockTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockMushroom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockMushroom.class)
public class BlockMushroomMixin {
    /**
     * @author Invadermonky
     * @reason Allows configured blocks to support mushrooms regardless of light level.
     */
    @ModifyReturnValue(method = "canBlockStay", at = @At("RETURN"))
    private boolean richSoilSurvivalMixin(boolean original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos) {
        if(!original) {
            return BlockTags.isMushroomSoil(world.getBlockState(pos.down()).getBlock());
        }
        return original;
    }
}
