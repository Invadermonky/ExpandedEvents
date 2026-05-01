package com.expandedevents.api;

import com.expandedevents.tags.MushroomSoilTag;
import net.minecraft.block.Block;

public class ExpandedEventsAPI {

    /**
     * Registers a block as valid mushroom soil, allowing vanilla mushrooms to survive
     * regardless of light level.
     */
    public static void registerMushroomSoil(Block block) {
        MushroomSoilTag.registerMushroomSoil(block);
    }
}
