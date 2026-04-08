package com.expandedevents;

import com.expandedevents.capabilities.baubles.CapabilityBaublesAttributes;
import com.expandedevents.handlers.BaublesEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ExpandedEvents.MOD_ID,
        name = ExpandedEvents.MOD_NAME,
        version = ExpandedEvents.MOD_VERSION,
        acceptedMinecraftVersions = ExpandedEvents.MC_VERSION,
        dependencies = ExpandedEvents.DEPENDENCIES
)
public class ExpandedEvents {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String MC_VERSION = "[1.12.2]";
    public static final String DEPENDENCIES = "required-after:mixinbooter@[10.5,)";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final boolean isBaublesLoaded = Loader.isModLoaded("baubles");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(isBaublesLoaded) {
            MinecraftForge.EVENT_BUS.register(new BaublesEventHandler());
            CapabilityBaublesAttributes.register();
        }
    }
}
