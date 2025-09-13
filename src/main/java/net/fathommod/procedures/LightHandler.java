package net.fathommod.procedures;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = FathommodMod.MOD_ID, value = Dist.CLIENT)
public class LightHandler {
    public static final ArrayList<BlockPos> TRINKET_PLACED = new ArrayList<>();
    public static boolean IS_ALLOWED_TO_ILLUMINATE = false;

    @SubscribeEvent
    public static void execute(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();
        if (IS_ALLOWED_TO_ILLUMINATE && placeLight(world, playerPos))
            clearSurroundingBlocks(world, playerPos, 14, false);
        else clearSurroundingBlocks(world, playerPos, 17, true);
    }

    private static boolean placeLight(Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
            world.setBlock(pos, FathommodModBlocks.BUDGET_LIGHT.get().defaultBlockState(), 1);
            TRINKET_PLACED.add(pos);
            return true;
        } else if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
            world.setBlock(pos, FathommodModBlocks.BUDGET_LIGHT.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), 1);
            TRINKET_PLACED.add(pos);
            return true;
        } else if (world.getBlockState(pos.above(1)).getBlock() == Blocks.AIR) {
            world.setBlock(pos.above(1), FathommodModBlocks.BUDGET_LIGHT.get().defaultBlockState(), 1);
            TRINKET_PLACED.add(pos.above(1));
            return true;
        } else if (world.getBlockState(pos.above(1)).getBlock() == Blocks.WATER) {
            world.setBlock(pos.above(1), FathommodModBlocks.BUDGET_LIGHT.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), 1);
            TRINKET_PLACED.add(pos.above(1));
            return true;
        } else return world.getBlockState(pos).getBlock() == FathommodModBlocks.BUDGET_LIGHT.get() || world.getBlockState(pos.above(1)).getBlock() == FathommodModBlocks.BUDGET_LIGHT.get();
    }

    private static void clearSurroundingBlocks(Level world, BlockPos centerPos, int radius, boolean bypass) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    if (((!pos.equals(centerPos) && !pos.equals(centerPos.above(1))) || bypass) && world.getBlockState(pos).getBlock() == FathommodModBlocks.BUDGET_LIGHT.get() && TRINKET_PLACED.contains(pos)) {
                        if (world.getBlockState(pos).getValue(BlockStateProperties.WATERLOGGED))
                            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                        else
                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        TRINKET_PLACED.remove(pos);
                    }
                }
            }
        }
    }
}

