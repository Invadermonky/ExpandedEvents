package com.expandedevents.capabilities;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public interface IBaublesAttributesHandler {
    Multimap<String, AttributeModifier> getBaublesAttributes();

    void addBaublesAttributes(Multimap<String, AttributeModifier> multimap);

    void clearBaublesAttributes();
}
