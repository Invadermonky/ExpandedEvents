package com.expandedevents.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.jetbrains.annotations.Nullable;

public class CapabilityBaublesAttributes {
    @CapabilityInject(IBaublesAttributesHandler.class)
    public static Capability<IBaublesAttributesHandler> BAUBLES_ATTRIBUTES_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IBaublesAttributesHandler.class, new Capability.IStorage<IBaublesAttributesHandler>() {
            @Override
            public @Nullable NBTBase writeNBT(Capability<IBaublesAttributesHandler> capability, IBaublesAttributesHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IBaublesAttributesHandler> capability, IBaublesAttributesHandler instance, EnumFacing side, NBTBase nbt) {

            }
        }, BaublesAttributeHandler::new);
    }
}
