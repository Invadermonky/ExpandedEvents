package com.expandedevents.tags;

import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.Set;

public class MushroomSoilTag {
    private static final Set<Block> MUSHROOM_SOIL_BLOCKS = new HashSet<>();

    public static boolean isMushroomSoil(Block block) {
        return MUSHROOM_SOIL_BLOCKS.contains(block);
    }

    public static void registerMushroomSoil(Block block) {
        MUSHROOM_SOIL_BLOCKS.add(block);
    }
}
