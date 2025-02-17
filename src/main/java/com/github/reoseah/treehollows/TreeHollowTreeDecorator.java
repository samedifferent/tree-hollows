package com.github.reoseah.treehollows;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class TreeHollowTreeDecorator extends TreeDecorator {
    public static final Codec<TreeHollowTreeDecorator> CODEC = RecordCodecBuilder.create( //
            instance -> instance.group( //
                            Registry.BLOCK.getCodec().fieldOf("block").forGetter(config -> config.block), //
                            Codec.floatRange(0.0f, 1.0f).fieldOf("world_generation_chance").forGetter(config -> config.worldGenChance), //
                            Codec.floatRange(0.0f, 1.0f).fieldOf("growth_chance").forGetter(config -> config.worldGenChance)) //
                    .apply(instance, TreeHollowTreeDecorator::new));

    protected final Block block;
    protected final float worldGenChance;
    protected final float growthChance;

    public TreeHollowTreeDecorator(Block block, float worldGenChance, float growthChance) {
        this.block = block;
        this.worldGenChance = worldGenChance;
        this.growthChance = growthChance;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeHollows.TREE_DECORATOR_TYPE;
    }

    @Override
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        boolean isWorldGen = world instanceof ChunkRegion;
        if (random.nextFloat() <= (isWorldGen ? this.worldGenChance : this.growthChance)) {
            int height = 2 + random.nextInt(1);
            if (logPositions.size() <= height) {
                return;
            }
            // log positions are sorted by Y coordinate
            BlockPos pos = logPositions.get(height);
            Direction facing = Direction.fromHorizontal(random.nextInt(4));

            replacer.accept(pos, this.block.getDefaultState().with(TreeHollowBlock.FACING, facing));

            if (isWorldGen) {
                LootableContainerBlockEntity.setLootTable((ChunkRegion) world, random, pos, TreeHollows.LOOT_TABLE_ID);
            }
        }
    }
}
