package com.expandedevents.capabilities;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BaublesAttributeHandler implements IBaublesAttributesHandler, ICapabilityProvider, INBTSerializable<NBTTagCompound> {
    protected final Multimap<String, AttributeModifier> multimap;

    public BaublesAttributeHandler() {
        this.multimap = HashMultimap.create();
    }

    @Override
    public Multimap<String, AttributeModifier> getBaublesAttributes() {
        return this.multimap;
    }

    @Override
    public void addBaublesAttributes(Multimap<String, AttributeModifier> multimap) {
        this.multimap.putAll(multimap);
    }

    @Override
    public void clearBaublesAttributes() {
        this.multimap.clear();
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityBaublesAttributes.BAUBLES_ATTRIBUTES_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityBaublesAttributes.BAUBLES_ATTRIBUTES_CAPABILITY) {
            return CapabilityBaublesAttributes.BAUBLES_ATTRIBUTES_CAPABILITY.cast(this);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.multimap.asMap().forEach((name, modifiers) -> {
            NBTTagList tagList = new NBTTagList();
            modifiers.forEach(modifier -> tagList.appendTag(this.serializeModifier(modifier)));
            tag.setTag(name, tagList);
        });
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.clearBaublesAttributes();
        for(String name : nbt.getKeySet()) {
            NBTTagList tagList = nbt.getTagList(name, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < tagList.tagCount(); i++) {
                AttributeModifier modifier = this.deserializeModifier(tagList.getCompoundTagAt(i));
                this.multimap.put(name, modifier);
            }
        }
    }

    public NBTTagCompound serializeModifier(AttributeModifier modifier) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("uuid", modifier.getID().toString());
        tag.setString("name", modifier.getName());
        tag.setDouble("amount", modifier.getAmount());
        tag.setInteger("operation", modifier.getOperation());
        return tag;
    }

    public AttributeModifier deserializeModifier(NBTTagCompound tag) {
        return new AttributeModifier(
                UUID.fromString(tag.getString("uuid")),
                tag.getString("name"),
                tag.getDouble("amount"),
                tag.getInteger("operation")
        );
    }
}
