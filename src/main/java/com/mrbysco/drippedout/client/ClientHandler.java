package com.mrbysco.drippedout.client;

import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(), RenderType.cutout());
	}
}
