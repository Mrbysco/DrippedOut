package com.mrbysco.drippedout;

import com.mojang.logging.LogUtils;
import com.mrbysco.drippedout.handler.PlacementHandler;
import com.mrbysco.drippedout.registry.DripRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(DrippedOut.MOD_ID)
public class DrippedOut {
	public static final String MOD_ID = "drippedout";
	public static final Logger LOGGER = LogUtils.getLogger();

	public DrippedOut(IEventBus eventBus) {
		DripRegistry.BLOCKS.register(eventBus);

		NeoForge.EVENT_BUS.register(new PlacementHandler());
	}
}
