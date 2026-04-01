package com.mrbysco.drippedout.registry;

import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.block.SidewaysDripBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DripRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DrippedOut.MOD_ID);

	public static final DeferredBlock<SidewaysDripBlock> SIDEWAYS_POINTED_DRIPSTONE = BLOCKS.registerBlock("sideways_pointed_dripstone", SidewaysDripBlock::new,
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.noOcclusion()
					.sound(SoundType.POINTED_DRIPSTONE)
					.strength(1.5F, 3.0F)
					.pushReaction(PushReaction.DESTROY)
					.isRedstoneConductor(SidewaysDripBlock::never)
	);
}
