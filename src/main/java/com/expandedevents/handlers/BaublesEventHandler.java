package com.expandedevents.handlers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.expandedevents.ExpandedEvents;
import com.expandedevents.capabilities.BaublesAttributeHandler;
import com.expandedevents.utils.BaublesAttributeHelper;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaublesEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if(!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);

            Multimap<String, AttributeModifier> prevModifiers = BaublesAttributeHelper.getBaublesAttributes(player);
            player.getAttributeMap().removeAttributeModifiers(prevModifiers);
            BaublesAttributeHelper.removeBaublesAttributes(player);

            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.getItem() instanceof IBauble) {
                    BaubleType type = ((IBauble) stack.getItem()).getBaubleType(stack);
                    Multimap<String, AttributeModifier> currModifiers = BaublesAttributeHelper.getBaubleAttributeModifiers(stack, type);
                    player.getAttributeMap().applyAttributeModifiers(currModifiers);
                    BaublesAttributeHelper.addBaublesAttributes(player, currModifiers);
                }
            }
        }
    }

    @SubscribeEvent
    public void cloneCapabilities(PlayerEvent.Clone event) {
        try {
            BaublesAttributeHelper.addBaublesAttributes(event.getEntityPlayer(), BaublesAttributeHelper.getBaublesAttributes(event.getOriginal()));
        } catch (Exception e) {
            ExpandedEvents.LOGGER.error("Could not clone player [{}] bauble attributes when changing dimensions.", event.getOriginal().getName());
        }
    }

    @SubscribeEvent
    public void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(ExpandedEvents.MOD_ID, "bauble_attributes"), new BaublesAttributeHandler());
        }
    }

}
