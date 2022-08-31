package com.mrbysco.drippedout.registry;

import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.block.SidewaysDripBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DripRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DrippedOut.MOD_ID);

	public static final RegistryObject<Block> SIDEWAYS_POINTED_DRIPSTONE = BLOCKS.register("sideways_pointed_dripstone", () ->
			new SidewaysDripBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_BROWN)
					.noOcclusion().sound(SoundType.POINTED_DRIPSTONE).strength(1.5F, 3.0F)));

}
