package com.github.synth.main.content.tiles

import com.github.synth.lib.api.content.tiles.*
import com.github.synth.lib.api.multiblock.*
import com.github.synth.lib.api.multiblock.MultiBlockState.*
import com.github.synth.lib.api.multiblock.StructureBlockVariant.*
import com.github.synth.lib.api.multiblock.StructureBlockVariant.Corner
import com.github.synth.lib.api.multiblock.StructureBlockVariant.Side
import com.github.synth.lib.api.util.*
import com.github.synth.lib.api.util.dsl.*
import com.github.synth.main.*
import com.github.synth.main.Synth.Blocks.Case
import com.github.synth.main.Synth.Blocks.Panel
import com.github.synth.main.content.blocks.*
import net.minecraft.core.*
import net.minecraft.core.Direction.*
import net.minecraft.nbt.*
import net.minecraft.world.*
import net.minecraft.world.InteractionResult.*
import net.minecraft.world.entity.player.*
import net.minecraft.world.item.*
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.*
import net.minecraft.world.level.material.*

/**
 * The "brain" of the reactor. Is responsible for updating the states of the casings, finding input/outputs/and sparks
 */
class ControlTile(blockPos: BlockPos, blockState: BlockState) : BaseTile<ControlTile>(Synth.Tiles.Control, blockPos, blockState),
    IMaster<ControlTile> {
    /**Keep track of our structure**/
    private var structure: Opt<StructureStore> = Opt.nil()

    /**Keep track of tick to do logic at fixed rate**/
    private var tick = 0

    /**
     * Called right before the first tick, add initialization logic here if it requires the world to be present
     */
    override fun onInit() = world.ifPresent {
        updateSlaves(structure)
    }

    /**
     * Delegate ticking of the server here
     */
    override fun onTick(level: Level) {
        if (level.isClientSide) return
        if (tick >= 20) {
            if (structure.isPresent && !isValid(structure)) {
                unform(structure)
//                info { "Unformed structure because it was invalid: $structure" }
                structure = Opt.nil()
            }
            tick = 0
        }
        tick++
    }

    /**
     * Attempt to form our multi block here, if valid we store the multiblock
     */
    override fun onUse(level: Level, player: Player, itemUsed: ItemStack, direction: Direction): InteractionResult {
        if (level.isClientSide) return SUCCESS
        if (player.isShiftKeyDown) {
            unform(structure)
            structure = Opt.nil()
            return CONSUME
        }
        if (this.structure.isPresent) return PASS
        this.structure = form(direction.opposite)
        structure.ifPresent {
//            info { "Formed structure: $structure" }
        }
        return CONSUME
    }

    /**
     * Called upon destroying the block, we want to update our multiblock in this case to destroy it
     */
    override fun onBreak(level: Level, player: Player, willHarvest: Boolean, fluid: FluidState) = unform(structure)

    /**
     * Required to check to see if a block at the given position and type is valid
     */
    override fun isValidVariant(blockPos: BlockPos, blockState: BlockState, type: StructureBlockVariant, structure: Opt<StructureStore>): Boolean =
        when (type) {
            Side -> blockState.`is`(Synth.Blocks.Panel) || blockState.`is`(Synth.Blocks.Control)                     || blockState.`is`(Synth.Blocks.Case)
//                    || blockState.`is`(Synth.Blocks.Input) || blockState.`is`(Synth.Blocks.Output)
            VerticalEdge -> blockState.`is`(Synth.Blocks.Panel)
            HorizontalEdge -> blockState.`is`(Synth.Blocks.Panel)
            Inner -> {
//                if (blockState.`is`(Synth.Blocks.Spark)) {
//                    val up = blockPos.offset(0, 1, 0)
//                    if (!world) false
//                    else {
//                        val upstate = world().getBlockState(up)
//                        upstate.`is`(Synth.Blocks.Panel)
//                    }
//                } else
                    blockState.isAir
//                            || blockState.`is`(Synth.Blocks.Fuel) || blockState.`is`(Synth.Blocks.Chamber)
            }
            Corner -> blockState.`is`(Synth.Blocks.Panel)
        }

    /**
     * Used to update the state of a given block. By default, it just returns the current state,
     * use this in the override version to just get the default state and update it
     */
    override fun setFormedAt(blockPos: BlockPos, direction: Direction, variant: StructureBlockVariant, store: StructureStore): BlockState {
        var state = super.setFormedAt(blockPos, direction, variant, store)
        when (state.block) {
//            is ChamberBlock -> {
//                if (!world) return state
//                val be = (world mapNil { it.getBlockEntity(blockPos) }) ?: return state
//                if (be !is ChamberTile) return state
//                be.powerUp()
//            }
            is PanelBlock -> {
                state =
                    if (blockPos.y == store.max.y && blockPos.x != store.max.x && blockPos.x != store.min.x && blockPos.z != store.min.z && blockPos.z != store.max.z) state.setValue(
                        DirectionalBlock.FACING,
                        DOWN
                    )
                    else state.setValue(DirectionalBlock.FACING, direction)
                if (blockPos.y > store.min.y) { //don't set state for bottom panel
                    state = state.setValue(MultiBlockState.State, variant.multiBlockState)
                }
            }
            is CaseBlock -> state = state.setValue(DirectionalBlock.FACING, direction).setValue(MultiBlockState.State, Formed )
            is ControlBlock -> state = state.setValue(HorizontalDirectionalBlock.FACING, direction.horizontal.opposite).setValue(MultiBlockState.State, Formed)
//            is OutputBlock -> state = state.setValue(HorizontalDirectionalBlock.FACING, direction.horizontal.opposite).setValue(OutputBlock.Formed, true)
//            is InputBlock -> state = state.setValue(HorizontalDirectionalBlock.FACING, direction.horizontal.opposite).setValue(InputBlock.Formed, true)
        }
        return state
    }

    /**
     * Should return the unformed state for the given block. It will also remove the master connection from the slaves
     */
    override fun setUnformedAt(blockPos: BlockPos, direction: Direction, variant: StructureBlockVariant, store: StructureStore): BlockState {
        val state = super.setUnformedAt(blockPos, direction, variant, store)
        when (state.block) {
//            is ChamberBlock -> {
//                if (!world) return state
//                val be = (world mapNil { it.getBlockEntity(blockPos) }) ?: return state
//                if (be !is ChamberTile) return state
//                be.powerDown()
//            }
//            is SparkBlock -> {
//                if (!world) return state
//                return world().getBlockState(blockPos)
//            }
        }
        return state
    }

    /**
     * Gets and casts all of the slaves of the given type
     */
    inline operator fun <reified R : BlockEntity> get(store: Opt<StructureStore>): Set<R> {
        return getSlaves(store).filterIsInstance<R>().toSet()
    }


    /**
     * Save our structure
     */
    override fun onSave(tag: CompoundTag) {
        structure.ifPresent {
            tag.put("structure", it.serializeNBT())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onLoad(tag: CompoundTag) {
        if (tag.contains("structure")) {
            val structureTag = tag.getCompound("structure")
            this.structure = Opt.of(StructureStore(structureTag))
        }
    }


}