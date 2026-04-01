package com.mrbysco.drippedout.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SidewaysDripBlock extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<Direction> TIP_DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape NORTH_SHAPE = Block.box(3.0D, 3.0D, 4.0D, 13.0D, 13.0D, 16.0D);
	private static final VoxelShape EAST_SHAPE = Block.box(0.0D, 3.0D, 3.0D, 12.0D, 13.0D, 13.0D);
	private static final VoxelShape SOUTH_SHAPE = Block.box(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 12.0D);
	private static final VoxelShape WEST_SHAPE = Block.box(4.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D);

	public SidewaysDripBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(TIP_DIRECTION, Direction.NORTH)
				.setValue(WATERLOGGED, Boolean.FALSE)
		);
	}

	@Override
	public void onProjectileHit(@NotNull Level level, @NotNull BlockState state, BlockHitResult hitResult,
	                            @NotNull Projectile projectile) {
		BlockPos blockpos = hitResult.getBlockPos();
		if (level instanceof ServerLevel serverlevel && projectile.mayInteract(serverlevel, blockpos) &&
				projectile instanceof ThrownTrident && projectile.getDeltaMovement().length() > 0.6D) {
			level.destroyBlock(blockpos, true);
		}
	}

	@Override
	protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                            @NotNull Entity entity, @NotNull InsideBlockEffectApplier applier, boolean intersects) {
		if (level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			if ((livingEntity.xOld != livingEntity.getX() || livingEntity.zOld != livingEntity.getZ())) {
				double d0 = Math.abs(livingEntity.getX() - livingEntity.xOld);
				double d1 = Math.abs(livingEntity.getZ() - livingEntity.zOld);
				if (d0 >= (double) 0.003F || d1 >= (double) 0.003F) {
					livingEntity.hurtServer(serverLevel, livingEntity.damageSources().stalagmite(), 1.0F);
				}
			}
		}
		super.entityInside(state, level, pos, entity, applier, intersects);
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
	                           @NotNull CollisionContext context) {
		switch (state.getValue(TIP_DIRECTION)) {
			case EAST -> {
				return EAST_SHAPE;
			}
			case SOUTH -> {
				return SOUTH_SHAPE;
			}
			case WEST -> {
				return WEST_SHAPE;
			}
			default -> {
				return NORTH_SHAPE;
			}
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(blockpos);
		return this.defaultBlockState()
				.setValue(TIP_DIRECTION, context.getClickedFace())
				.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
		Direction direction = state.getValue(TIP_DIRECTION);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = levelReader.getBlockState(blockpos);
		return blockstate.isFaceSturdy(levelReader, blockpos, direction);
	}

	@NotNull
	@Override
	protected BlockState updateShape(BlockState state, @NotNull LevelReader level,
	                                 @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos pos,
	                                 Direction direction, @NotNull BlockPos neighborPos,
	                                 @NotNull BlockState neighborState, @NotNull RandomSource random) {
		return direction.getOpposite() == state.getValue(TIP_DIRECTION) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
	}

	@NotNull
	@Override
	public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state,
	                                   boolean includeData, @NotNull Player player) {
		return new ItemStack(Items.POINTED_DRIPSTONE);
	}

	@NotNull
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@NotNull
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(TIP_DIRECTION, rot.rotate(state.getValue(TIP_DIRECTION)));
	}

	@SuppressWarnings("deprecation")
	@NotNull
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(TIP_DIRECTION)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(TIP_DIRECTION, WATERLOGGED);
	}

	public static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
		return false;
	}
}
