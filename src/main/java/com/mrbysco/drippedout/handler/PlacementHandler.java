package com.mrbysco.drippedout.handler;

import com.mrbysco.drippedout.registry.DripRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlacementHandler {

	@SubscribeEvent
	public void onBlockPlacement(RightClickBlock placeEvent) {
		final Level level = placeEvent.getWorld();
		final Player player = placeEvent.getPlayer();
		final BlockPos relativePos = placeEvent.getPos().relative(placeEvent.getFace());
		ItemStack stack = placeEvent.getItemStack();
		if (stack.is(Items.POINTED_DRIPSTONE) && Plane.HORIZONTAL.test(placeEvent.getFace())) {
			final Block sidewaysBlock = DripRegistry.SIDEWAYS_POINTED_DRIPSTONE.get();
			BlockState state = sidewaysBlock.getStateForPlacement(
					new BlockPlaceContext(player, placeEvent.getHand(), new ItemStack(sidewaysBlock), placeEvent.getHitVec()));
			if (state.canSurvive(level, relativePos) && level.setBlockAndUpdate(relativePos, state)) {
				level.gameEvent(player, GameEvent.BLOCK_PLACE, relativePos);
				SoundType soundtype = state.getSoundType(level, relativePos, player);
				level.playSound(player, relativePos, state.getSoundType(level, relativePos, player).getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
				placeEvent.setCancellationResult(InteractionResult.SUCCESS);
				placeEvent.setCanceled(true);
			}
		}
	}
}
