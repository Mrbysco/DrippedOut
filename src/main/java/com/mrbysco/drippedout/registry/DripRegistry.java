package com.mrbysco.drippedout.registry;

import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.block.SidewaysDripBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DripRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DrippedOut.MOD_ID);

	public static final DeferredBlock<SidewaysDripBlock> SIDEWAYS_POINTED_DRIPSTONE = BLOCKS.register("sideways_pointed_dripstone", () ->
			new SidewaysDripBlock(BlockBehaviour.Properties.of().setId(blockKey("sideways_pointed_dripstone")).mapColor(MapColor.TERRACOTTA_BROWN)
					.noOcclusion().sound(SoundType.POINTED_DRIPSTONE).strength(1.5F, 3.0F)));


	private static ResourceKey<Block> blockKey(String path) {
		return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DrippedOut.MOD_ID, path));
	}
}
