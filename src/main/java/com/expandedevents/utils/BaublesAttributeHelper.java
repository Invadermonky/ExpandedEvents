package com.expandedevents.utils;

import baubles.api.BaubleType;
import com.expandedevents.capabilities.CapabilityBaublesAttributes;
import com.expandedevents.capabilities.IBaublesAttributesHandler;
import com.expandedevents.events.BaubleAttributeModifierEvent;
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
    public static Multimap<String, AttributeModifier> getBaubleAttributeModifiers(ItemStack stack, BaubleType type) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        BaubleAttributeModifierEvent attributeEvent = new BaubleAttributeModifierEvent(stack, type, modifiers);
        MinecraftForge.EVENT_BUS.post(attributeEvent);
        return attributeEvent.getModifiers();
    }

    public static Multimap<String, AttributeModifier> getBaublesAttributes(EntityPlayer player) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            return handler.getBaublesAttributes();
        }
        return HashMultimap.create();
    }

    public static void removeBaublesAttributes(EntityPlayer player) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            handler.clearBaublesAttributes();
        }
    }

    public static void addBaublesAttributes(EntityPlayer player, Multimap<String, AttributeModifier> multimap) {
        IBaublesAttributesHandler handler = getBaublesAttributeHandler(player);
        if(handler != null) {
            handler.addBaublesAttributes(multimap);
        }
    }

    @Nullable
    public static IBaublesAttributesHandler getBaublesAttributeHandler(EntityPlayer player) {
        return player.getCapability(CapabilityBaublesAttributes.BAUBLES_ATTRIBUTES_CAPABILITY, null);
    }

    public static void addBaublesAttributeTooltip(ItemStack stack, @Nullable EntityPlayer player, List<String> tooltip, int hideFlags) {
        for (BaubleType type : BaubleType.values()) {
            Multimap<String, AttributeModifier> multimap = getBaubleAttributeModifiers(stack, type);
            if (!multimap.isEmpty() && (hideFlags & 2) == 0) {
                tooltip.add("");
                tooltip.add(I18n.format("item.modifiers." + type.name().toLowerCase()));

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
