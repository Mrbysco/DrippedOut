package com.mrbysco.drippedout.datagen;

import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DrippedDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new DripLoots(packOutput));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new Language(packOutput));
			generator.addProvider(true, new BlockModels(packOutput, helper));
			generator.addProvider(true, new BlockStates(packOutput, helper));
		}
	}

	private static class DripLoots extends LootTableProvider {
		public DripLoots(PackOutput packOutput) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(MonsterBlockTables::new, LootContextParamSets.BLOCK)
			));
		}

		public static class MonsterBlockTables extends BlockLootSubProvider {

			protected MonsterBlockTables() {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			protected void generate() {
				this.dropOther(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(), Items.POINTED_DRIPSTONE);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) DripRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> table.validate(validationContext));
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, DrippedOut.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE, "Sideways Pointed Dripstone");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, DrippedOut.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			makeSidewaysDripstone(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE);
		}

		private void makeSidewaysDripstone(RegistryObject<Block> block) {
			ModelFile clusterBlock = models().getExistingFile(modLoc("block/" + block.getId().getPath()));
			getVariantBuilder(block.get())
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
					.modelForState().modelFile(clusterBlock).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
					.modelForState().modelFile(clusterBlock).rotationY(90).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
					.modelForState().modelFile(clusterBlock).rotationY(180).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
					.modelForState().modelFile(clusterBlock).rotationY(270).addModel();
		}
	}

	private static class BlockModels extends BlockModelProvider {
		public BlockModels(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, DrippedOut.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			makeSidewaysDripstone(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE);
		}

		private void makeSidewaysDripstone(RegistryObject<Block> block) {
			withExistingParent(block.getId().getPath(), modLoc("block/sideways_pointed"))
					.texture("cross", mcLoc("block/pointed_dripstone_up_tip")).renderType("cutout");
		}
	}
}
