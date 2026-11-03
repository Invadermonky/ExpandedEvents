package com.expandedevents.tags;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ItemTags {
    private static final Set<Item> ENCHANTING_REAGENT_ITEMS = new HashSet<>();
    private static final Set<Predicate<ItemStack>> ENCHANTING_REAGENT_PREDICATES = new HashSet<>();
    private static boolean enableLapis = true;

    public static boolean isEnchantingReagent(ItemStack stack) {
        return !stack.isEmpty() && (ENCHANTING_REAGENT_ITEMS.contains(stack.getItem()) || ENCHANTING_REAGENT_PREDICATES.stream().anyMatch(predicate -> predicate.test(stack)));
    }

    public static void registerEnchantingReagent(Item item) {
        ENCHANTING_REAGENT_ITEMS.add(item);
    }

    public static void registerEnchantingReagentPredicate(Predicate<ItemStack> predicate) {
        ENCHANTING_REAGENT_PREDICATES.add(predicate);
    }

    /**
     * Disables the built-in lapis lazuli check for the enchanting reagent slot. This is not an API method
     * and it disables vanilla logic, so it should only be used if absolutely necessary.
     */
    public static void disableLapisEnchantingReagent() {
        enableLapis = false;
    }

    public static void init() {
        if(enableLapis) {
            registerEnchantingReagentPredicate(stack -> {
                int lapisId = OreDictionary.getOreID("gemLapis");
                return Arrays.stream(OreDictionary.getOreIDs(stack)).anyMatch(id -> id == lapisId);
            });
        }
    }
}
