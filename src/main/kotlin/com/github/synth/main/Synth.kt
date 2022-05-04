package com.github.synth.main

import com.github.synth.lib.api.registry.*
import com.github.synth.lib.api.util.dsl.*
import com.github.synth.main.content.blocks.*
import com.github.synth.main.content.tiles.*
import com.github.synth.main.listeners.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.fml.common.*
import net.minecraftforge.registries.*
import thedarkcolour.kotlinforforge.forge.*

@Mod(Synth.ModId)
object Synth : IRegistry {
    const val ModId: String = "synth"

    /**
     * ========================Blocks registry========================
     */
    object Blocks : Registry<Block>(ForgeRegistries.BLOCKS) {
        val Panel by register("panel") { PanelBlock() }
        val Case by register("case") { CaseBlock() }
        val Control by register("control") { ControlBlock() }
    }

    object Tiles : Registry<BlockEntityType<*>>(ForgeRegistries.BLOCK_ENTITIES) {
        val Control by register("control") { tileTypeOf(Blocks.Control) { ControlTile(it.first, it.second) } }
    }
    /**
     * ========================Items registry========================
     */
    object Items : Registry<Item>(ForgeRegistries.ITEMS) {
        val Control by register("control") {
            object : BlockItem(Blocks.Control, Properties().fireResistant().tab(CreativeTab).stacksTo(1)) {}
        }
        val Panel by register("panel") {
            object : BlockItem(Blocks.Panel, Properties().fireResistant().tab(CreativeTab).stacksTo(64)) {}
        }
        val Case by register("case") {
            object : BlockItem(Blocks.Case, Properties().fireResistant().tab(CreativeTab).stacksTo(64)) {}
        }
        val CreativeTab: CreativeModeTab = object : CreativeModeTab("Synth") {
            override fun makeIcon(): ItemStack = ItemStack(Control)
        }
    }

    init {
        registerAll(ModId, MOD_BUS, FORGE_BUS)
        Common.register(ModId, MOD_BUS, FORGE_BUS)
        runWhenOn(Dist.CLIENT) { Client.register(ModId, MOD_BUS, FORGE_BUS) }
        runWhenOn(Dist.DEDICATED_SERVER) { Server.register(ModId, MOD_BUS, FORGE_BUS) }
    }


}