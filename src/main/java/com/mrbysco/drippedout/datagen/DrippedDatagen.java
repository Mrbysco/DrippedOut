package com.mrbysco.drippedout.datagen;

import com.mrbysco.drippedout.DrippedOut;
import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DrippedDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new DripLoots(packOutput, lookupProvider));

		generator.addProvider(true, new Language(packOutput));
		generator.addProvider(true, new Models(packOutput));
	}

	private static class DripLoots extends LootTableProvider {
		public DripLoots(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(DrilBlockLoot::new, LootContextParamSets.BLOCK)
			), completableFuture);
		}

		public static class DrilBlockLoot extends BlockLootSubProvider {

			protected DrilBlockLoot(HolderLookup.Provider lookupProvider) {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
			}

			@Override
			protected void generate() {
				this.dropOther(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(), Items.POINTED_DRIPSTONE);
			}

			@NotNull
			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) DripRegistry.BLOCKS.getEntries().stream().map((block) -> (Block) block.get())::iterator;
			}
		}

		@Override
		protected void validate(WritableRegistry<LootTable> writableregistry,
		                        @NotNull ValidationContext validationcontext,
		                        ProblemReporter.@NotNull Collector problemreporter$collector) {
			writableregistry.forEach((lootTable) -> lootTable.validate(validationcontext));
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

	private static class Models extends ModelProvider {
		private static final ModelTemplate POINTED_DRIPSTONE = ModelTemplates.create("drippedout:sideways_pointed", TextureSlot.CROSS);

		public Models(PackOutput packOutput) {
			super(packOutput, DrippedOut.MOD_ID);
		}

		@Override
		protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
			TextureMapping texturemapping = TextureMapping.cross(
					Identifier.withDefaultNamespace("block/pointed_dripstone_up_tip")
			);
			Identifier resourcelocation = POINTED_DRIPSTONE.extend().renderType("cutout").build()
					.create(DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(), texturemapping, blockModels.modelOutput);
			blockModels.blockStateOutput
					.accept(MultiVariantGenerator.dispatch(
									DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get(),
									BlockModelGenerators.plainVariant(resourcelocation))
							.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
					);
		}
	}
}
