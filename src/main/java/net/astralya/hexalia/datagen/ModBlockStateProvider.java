package net.astralya.hexalia.datagen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.RabbageCropBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

    private String blockName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "block/" + path);
    }

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, HexaliaMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        blockWithItem(ModBlocks.INFUSED_DIRT);
        blockWithItem(ModBlocks.SALT_BLOCK);
        blockWithItem(ModBlocks.CELESTIAL_CRYSTAL_BLOCK);
        blockWithItem(ModBlocks.COTTONWOOD_PLANKS);
        blockWithItem(ModBlocks.WILLOW_PLANKS);

        waterItemBlock(ModBlocks.SIREN_KELP.get());

        simpleCrossBlock(ModBlocks.NAUTILITE.get());
        simpleCrossBlock(ModBlocks.WITCHWEED.get());
        simpleCrossBlock(ModBlocks.WILD_MANDRAKE.get());
        simpleCrossBlock(ModBlocks.WILD_SUNFIRE_TOMATO.get());
        simpleCrossBlock(ModBlocks.COTTONWOOD_CATKIN.get());

        blockItem(ModBlocks.COTTONWOOD_LOG);
        blockItem(ModBlocks.COTTONWOOD_WOOD);
        blockItem(ModBlocks.STRIPPED_COTTONWOOD_LOG);
        blockItem(ModBlocks.STRIPPED_COTTONWOOD_WOOD);
        blockItem(ModBlocks.WILLOW_LOG);
        blockItem(ModBlocks.WILLOW_WOOD);
        blockItem(ModBlocks.STRIPPED_WILLOW_LOG);
        blockItem(ModBlocks.STRIPPED_WILLOW_WOOD);

        flowerWithPotBlock(ModBlocks.SPIRIT_BLOOM.get(), ModBlocks.POTTED_SPIRIT_BLOOM.get());
        flowerWithPotBlock(ModBlocks.DREAMSHROOM.get(), ModBlocks.POTTED_DREAMSHROOM.get());
        flowerWithPotBlock(ModBlocks.GHOST_FERN.get(), ModBlocks.POTTED_GHOST_FERN.get());
        flowerWithPotBlock(ModBlocks.CELESTIAL_BLOOM.get(), ModBlocks.POTTED_CELESTIAL_BLOOM.get());
        flowerWithPotBlock(ModBlocks.WITHERED_CELESTIAL_BLOOM.get(), ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get());
        flowerWithPotBlock(ModBlocks.MORPHORA.get(), ModBlocks.POTTED_MORPHORA.get());
        flowerWithPotBlock(ModBlocks.GRIMSHADE.get(), ModBlocks.POTTED_GRIMSHADE.get());
        flowerWithPotBlock(ModBlocks.WINDSONG.get(), ModBlocks.POTTED_WINDSONG.get());
        flowerWithPotBlock(ModBlocks.ASTRYLIS.get(), ModBlocks.POTTED_ASTRYLIS.get());
        flowerWithPotBlock(ModBlocks.LOURDES.get(), ModBlocks.POTTED_LOURDES.get());
        flowerWithPotBlock(ModBlocks.AEGIFLORA.get(), ModBlocks.POTTED_AEGIFLORA.get());
        flowerWithPotBlock(ModBlocks.WITHERED_AEGIFLORA.get(), ModBlocks.POTTED_WITHERED_AEGIFLORA.get());
        flowerWithPotBlock(ModBlocks.BEGONIA.get(), ModBlocks.POTTED_BEGONIA.get());
        flowerWithPotBlock(ModBlocks.LAVENDER.get(), ModBlocks.POTTED_LAVENDER.get());
        flowerWithPotBlock(ModBlocks.DAHLIA.get(), ModBlocks.POTTED_DAHLIA.get());
        flowerWithPotBlock(ModBlocks.NIGHTSHADE_BUSH.get(), ModBlocks.POTTED_NIGHTSHADE_BUSH.get());
        flowerWithPotBlock(ModBlocks.WILLOW_SAPLING.get(), ModBlocks.POTTED_WILLOW_SAPLING.get());
        flowerWithPotBlock(ModBlocks.COTTONWOOD_SAPLING.get(), ModBlocks.POTTED_COTTONWOOD_SAPLING.get());

        stageBlock(ModBlocks.RABBAGE_CROP.get(), RabbageCropBlock.AGE);

        leavesBlock(ModBlocks.COTTONWOOD_LEAVES);
        leavesBlock(ModBlocks.WILLOW_LEAVES);

        logBlock(((RotatedPillarBlock) ModBlocks.COTTONWOOD_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.COTTONWOOD_WOOD.get()), blockTexture(ModBlocks.COTTONWOOD_LOG.get()), blockTexture(ModBlocks.COTTONWOOD_LOG.get()));
        logBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_COTTONWOOD_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_COTTONWOOD_WOOD.get()), blockTexture(ModBlocks.STRIPPED_COTTONWOOD_LOG.get()), blockTexture(ModBlocks.STRIPPED_COTTONWOOD_LOG.get()));
        
        logBlock(((RotatedPillarBlock) ModBlocks.WILLOW_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.WILLOW_WOOD.get()), blockTexture(ModBlocks.WILLOW_LOG.get()), blockTexture(ModBlocks.WILLOW_LOG.get()));
        logBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_WOOD.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()));

        stairsBlock(((StairBlock) ModBlocks.COTTONWOOD_STAIRS.get()), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        stairsBlock(((StairBlock) ModBlocks.WILLOW_STAIRS.get()), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        slabBlock(((SlabBlock) ModBlocks.COTTONWOOD_SLAB.get()), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.WILLOW_SLAB.get()), blockTexture(ModBlocks.WILLOW_PLANKS.get()), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        buttonBlock(((ButtonBlock) ModBlocks.COTTONWOOD_BUTTON.get()), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.WILLOW_BUTTON.get()), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        pressurePlateBlock(((PressurePlateBlock) ModBlocks.COTTONWOOD_PRESSURE_PLATE.get()), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.WILLOW_PRESSURE_PLATE.get()), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        fenceBlock((FenceBlock) ModBlocks.COTTONWOOD_FENCE.get(), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        fenceBlock((FenceBlock) ModBlocks.WILLOW_FENCE.get(), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        fenceGateBlock((FenceGateBlock) ModBlocks.COTTONWOOD_FENCE_GATE.get(), blockTexture(ModBlocks.COTTONWOOD_PLANKS.get()));
        fenceGateBlock((FenceGateBlock) ModBlocks.WILLOW_FENCE_GATE.get(), blockTexture(ModBlocks.WILLOW_PLANKS.get()));

        doorBlockWithRenderType(((DoorBlock) ModBlocks.COTTONWOOD_DOOR.get()), modLoc("block/cottonwood_door_bottom"), modLoc("block/cottonwood_door_top"), "cutout");
        doorBlockWithRenderType(((DoorBlock) ModBlocks.WILLOW_DOOR.get()), modLoc("block/willow_door_bottom"), modLoc("block/willow_door_top"), "cutout");

        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.COTTONWOOD_TRAPDOOR.get()), modLoc("block/cottonwood_trapdoor"), true, "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.WILLOW_TRAPDOOR.get()), modLoc("block/willow_trapdoor"), true, "cutout");
    }

    public void flowerWithPotBlock(Block flower, Block flowerPot) {
        String flowerName = blockName(flower);
        String flowerPotName = blockName(flowerPot);

        ModelFile flowerModel = models().cross(flowerName, resourceBlock(flowerName)).renderType("cutout");

        this.simpleBlock(flower, flowerModel);

        itemModels().getBuilder(flowerName)
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", resourceBlock(flowerName));

        this.simpleBlock(flowerPot,
                models().withExistingParent(flowerPotName, mcLoc("block/flower_pot_cross"))
                        .texture("plant", resourceBlock(flowerName))
                        .renderType("cutout"));
    }

    public void waterItemBlock (Block block) {
        String name = blockName(block);
        ModelFile model = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/cross")))
                .texture("cross", resourceBlock(name))
                .renderType("cutout");
        this.simpleBlock(block, model);
    }

    public void simpleCrossBlock(Block block) {
        String name = blockName(block);

        ModelFile model = models().cross(name, resourceBlock(name)).renderType("cutout");

        this.simpleBlock(block, model);

        itemModels().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", resourceBlock(name));
    }


    public void tallPlantBlock(Block block) {
        String name = blockName(block);
        String bottomName = name + "_bottom";
        String topName = name + "_top";

        ModelFile bottomModel = models().cross(bottomName, resourceBlock(bottomName)).renderType("cutout");
        ModelFile topModel = models().cross(topName, resourceBlock(topName)).renderType("cutout");

        getVariantBuilder(block)
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                .modelForState().modelFile(bottomModel).addModel()
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                .modelForState().modelFile(topModel).addModel();

        itemModels().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", resourceBlock(topName));
    }

    private void leavesBlock(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(),
                models().singleTexture(deferredBlock.getId().getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(deferredBlock.get())).renderType("cutout"));
    }


    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("hexalia:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("hexalia:block/" + deferredBlock.getId().getPath() + appendix));
    }

    public void stageBlock(Block block, IntegerProperty ageProperty, Property<?>... ignored) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    int ageSuffix = state.getValue(ageProperty);
                    String stageName = blockName(block) + "_stage" + ageSuffix;
                    return ConfiguredModel.builder()
                            .modelFile(models().cross(stageName, resourceBlock(stageName)).renderType("cutout")).build();
                }, ignored);
    }

}
