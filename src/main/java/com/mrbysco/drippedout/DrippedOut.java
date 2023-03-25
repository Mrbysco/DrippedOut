package com.mrbysco.drippedout;

import com.mojang.logging.LogUtils;
import com.mrbysco.drippedout.handler.PlacementHandler;
import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DrippedOut.MOD_ID)
public class DrippedOut {
	public static final String MOD_ID = "drippedout";
	public static final Logger LOGGER = LogUtils.getLogger();

	public DrippedOut() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		DripRegistry.BLOCKS.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new PlacementHandler());
	}
}
