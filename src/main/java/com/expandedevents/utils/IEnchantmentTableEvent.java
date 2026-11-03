package com.expandedevents.utils;

/**
 * A helper interface for communicating necissary information between the mixins used in
 * {@link com.expandedevents.mixins.ContainerEnchantmentMixin} and
 * {@link com.expandedevents.mixins.GuiEnchantmentMixin}.
 */
public interface IEnchantmentTableEvent {
    int ee$getEnchantmentLevelCost(int enchantmentTier);

    void ee$setEnchantmentLevelCost(int enchantmentTier, int levelCost);

    int ee$getReagentCost(int enchantmentTier);

    void ee$setReagentCost(int enchantmentTier, int reagentCost);
}
