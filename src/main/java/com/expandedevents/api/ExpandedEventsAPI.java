package com.expandedevents.api;

import com.expandedevents.tags.BlockTags;
import com.expandedevents.tags.ItemTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ExpandedEventsAPI {

    /**
     * Registers a block as valid mushroom soil, allowing vanilla mushrooms to survive
     * regardless of light level.
     */
    public static void registerMushroomSoil(Block block) {
        BlockTags.registerMushroomSoil(block);
    }

    /**
     * Registers an item as an enchanting reagent, allowing the item to be placed in
     * the Enchanting Table's Lapis Lazuli slot.
     */
    public static void registerEnchantingReagent(Item item) {
        ItemTags.registerEnchantingReagent(item);
    }

    /**
     * Registers a predicate for enchanting table reagents, allowing the custom logic to
     * determine if an item can be placed in the Enchanting Table's Lapis Lazuli slot.
     */
    public static void registerEnchantingReagentPredicate(Predicate<ItemStack> predicate) {
        ItemTags.registerEnchantingReagentPredicate(predicate);
    }
}
