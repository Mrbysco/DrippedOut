package com.mrbysco.drippedout.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DrippedDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(new DripLoots(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(new Language(generator));
			generator.addProvider(new BlockModels(generator, helper));
			generator.addProvider(new BlockStates(generator, helper));
		}
	}

	private static class DripLoots extends LootTableProvider {
		public DripLoots(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(
					Pair.of(MonsterBlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class MonsterBlockTables extends BlockLoot {

			@Override
			protected void addTables() {
				this.dropOther(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(), Items.POINTED_DRIPSTONE);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) DripRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}

	private static class Language extends LanguageProvider {
		public Language(DataGenerator gen) {
			super(gen, DrippedOut.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE, "Sideways Pointed Dripstone");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, DrippedOut.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			makeSidewaysDripstone(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get());
		}

		private void makeSidewaysDripstone(Block block) {
			ModelFile clusterBlock = models().getExistingFile(modLoc("block/" + block.getRegistryName().getPath()));
			getVariantBuilder(block)
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
		public BlockModels(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, DrippedOut.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			makeSidewaysDripstone(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get());
		}

		private void makeSidewaysDripstone(Block block) {
			ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
			withExistingParent(location.getPath(), modLoc("block/sideways_pointed"))
					.texture("cross", mcLoc("block/pointed_dripstone_up_tip"));
		}
	}
}
