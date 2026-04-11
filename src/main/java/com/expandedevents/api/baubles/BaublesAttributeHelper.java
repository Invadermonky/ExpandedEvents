package com.expandedevents.api.baubles;

import baubles.api.BaubleType;
import com.expandedevents.api.event.BaubleAttributeModifierEvent;
import com.expandedevents.capabilities.baubles.CapabilityBaublesAttributes;
import com.expandedevents.capabilities.baubles.IBaublesAttributesHandler;
import com.expandedevents.utils.ConstantsEE;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BaublesAttributeHelper {
    /**
     * Gets the base attribute modifiers that belong to the passed ItemStack.
     * <p>
     * This method <STRONG>DOES</STRONG> fire {@link BaubleAttributeModifierEvent}.
     *
     * @param stack the bauble ItemStack
     * @param type the bauble type
     * @return A map containing all bauble attribute modifiers.
     */
    public static Multimap<String, AttributeModifier> getBaubleAttributeModifiers(ItemStack stack, BaubleType type) {
        Multimap<String, AttributeModifier> modifiers = getBaubleItemAttributeModifiers(stack, type);
        BaubleAttributeModifierEvent attributeEvent = new BaubleAttributeModifierEvent(stack, type, modifiers);
        MinecraftForge.EVENT_BUS.post(attributeEvent);
        return attributeEvent.getModifiers();
    }

    /**
     * Gets the base attribute modifiers that belong to the passed ItemStack. This will be the value returned by the method
     * {@link IAttributeBauble#getBaubleAttributeModifiers(BaubleType, ItemStack)} if the item implements {@link IAttributeBauble}
     * or an empty map.
     * <p>
     * This method <STRONG>DOES NOT</STRONG> fire {@link BaubleAttributeModifierEvent}.
     *
     * @param stack the bauble ItemStack
     * @param type the bauble type
     * @return A map containing all bauble attribute modifiers.
     */
    public static Multimap<String, AttributeModifier> getBaubleItemAttributeModifiers(ItemStack stack, BaubleType type) {
        if(stack.getItem() instanceof IAttributeBauble) {
            return ((IAttributeBauble) stack.getItem()).getBaubleAttributeModifiers(type, stack);
        } else {
            return HashMultimap.create();
        }
    }

    /** Gets the current player bauble attribute modifiers. */
    public static Multimap<String, AttributeModifier> getBaublesAttributes(EntityPlayer player) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            return handler.getBaublesAttributes();
        }
        return HashMultimap.create();
    }

    /** Removes all current bauble attribute modifiers from the player. */
    public static void removeBaublesAttributes(EntityPlayer player) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            handler.clearBaublesAttributes();
        }
    }

    /** Adds the passed bauble attribute modifiers to the player. */
    public static void addBaublesAttributes(EntityPlayer player, Multimap<String, AttributeModifier> multimap) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            handler.addBaublesAttributes(multimap);
        }
    }

    /** Gets the baubles attribute capability for the passed player. */
    @Nullable
    public static IBaublesAttributesHandler getBaublesAttributeHandler(EntityPlayer player) {
        return player.getCapability(CapabilityBaublesAttributes.BAUBLES_ATTRIBUTES_CAPABILITY, null);
    }

    /**
     * Appends the bauble attribute modifier tooltip to the existing item tooltip.
     *
     * @param stack the ItemStack object
     * @param player the player querying the item tooltip
     * @param tooltip the current ItemStack tooltip
     * @param hideFlags whether the attribute modifier information should be excluded from this item
     */
    public static void addBaublesAttributeTooltip(ItemStack stack, @Nullable EntityPlayer player, List<String> tooltip, int hideFlags) {
        for (BaubleType type : BaubleType.values()) {
            Multimap<String, AttributeModifier> multimap = getBaubleAttributeModifiers(stack, type);
            if (!multimap.isEmpty() && (hideFlags & 2) == 0) {
                tooltip.add("");
                tooltip.add(I18n.format("bauble.modifiers." + type.name().toLowerCase()));

                for (Map.Entry<String, AttributeModifier> entry : multimap.entries()) {
                    AttributeModifier attributemodifier = entry.getValue();
                    double amount = attributemodifier.getAmount();
                    boolean flag = false;

                    if (player != null) {
                        if (attributemodifier.getID() == ConstantsEE.ATTACK_DAMAGE_MODIFIER) {
                            amount = amount + player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                            amount = amount + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                            flag = true;
                        } else if (attributemodifier.getID() == ConstantsEE.ATTACK_SPEED_MODIFIER) {
                            amount += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                            flag = true;
                        }
                    }

                    double displayAmount;

                    if (attributemodifier.getOperation() != Constants.AttributeModifierOperation.ADD_MULTIPLE && attributemodifier.getOperation() != Constants.AttributeModifierOperation.MULTIPLY) {
                        displayAmount = amount;
                    } else {
                        displayAmount = amount * 100.0D;
                    }

                    if (flag) {
                        tooltip.add(" " + I18n.format("attribute.modifier.equals." + attributemodifier.getOperation(),
                                ItemStack.DECIMALFORMAT.format(displayAmount), I18n.format("attribute.name." + entry.getKey())));
                    } else if (amount > 0.0D) {
                        tooltip.add(TextFormatting.BLUE + " " + I18n.format("attribute.modifier.plus." + attributemodifier.getOperation(),
                                ItemStack.DECIMALFORMAT.format(displayAmount), I18n.format("attribute.name." + entry.getKey())));
                    } else if (amount < 0.0D) {
                        displayAmount = displayAmount * -1.0D;
                        tooltip.add(TextFormatting.RED + " " + I18n.format("attribute.modifier.take." + attributemodifier.getOperation(),
                                ItemStack.DECIMALFORMAT.format(displayAmount), I18n.format("attribute.name." + entry.getKey())));
                    }
                }
            }
        }
    }
}
