package com.github.synth.main.content.blocks

import com.github.synth.lib.api.multiblock.*
import com.github.synth.lib.api.multiblock.MultiBlockState.*
import com.github.synth.lib.api.util.dsl.*
import net.minecraft.core.*
import net.minecraft.core.Direction.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraft.world.entity.player.*
import net.minecraft.world.item.context.*
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.*
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.*
import net.minecraft.world.phys.shapes.*

class CaseBlock : Block(Properties.of(Material.HEAVY_METAL)) {

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        return defaultBlockState().setValue(DirectionalBlock.FACING, pContext.clickedFace.opposite).setValue(MultiBlockState.State, Unformed)
    }

    override fun createBlockStateDefinition(pBuilder: Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING).add(MultiBlockState.State)
    }

    /**
     * Form the multi-block
     */
    override fun use(pState: BlockState, level: Level, pPos: BlockPos, pPlayer: Player, pHand: InteractionHand, pHit: BlockHitResult): InteractionResult {
//        if (level.isClientSide) return InteractionResult.sidedSuccess(true)
//        level.setBlockAndUpdate(pPos, pState.setValue(State, pState.getValue(State).next))
//        return InteractionResult.sidedSuccess(false)
        return super.use(pState, level, pPos, pPlayer, pHand, pHit)

    }

    //Formed sides
    private val up = Shapes.empty()
        .join(Shapes.box(0.0, 0.5625, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR)
    private val down = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 1.0, 0.4375, 1.0), BooleanOp.OR)
    private val east = Shapes.empty()
        .join(Shapes.box(0.5625, 0.0, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR)
    private val west = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 0.4375, 1.0, 1.0), BooleanOp.OR)
    private val north = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.4375), BooleanOp.OR)
    private val south = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.5625, 1.0, 1.0, 1.0), BooleanOp.OR)

    //Small 3x3 square only (for corners)
    private val seast = Shapes.empty()
        .join(Shapes.box(0.5, 0.0, 0.5, 1.0, 0.5, 1.0), BooleanOp.OR)

    private val snorth = Shapes.empty()
        .join(Shapes.box(0.5, 0.0, 0.0, 1.0, 0.5, 0.5), BooleanOp.OR)

    private val ssouth = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.5, 0.5, 0.5, 1.0), BooleanOp.OR)

    private val swest = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 0.5, 0.5, 0.5), BooleanOp.OR)

    //Edges only shapes
    private val ceast = Shapes.empty()
        .join(Shapes.box(0.5, 0.0, 0.5, 1.0, 1.0, 1.0), BooleanOp.OR)
    private val cnorth = Shapes.empty()
        .join(Shapes.box(0.5, 0.0, 0.0, 1.0, 1.0, 0.5), BooleanOp.OR)
    private val csouth = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.5, 0.5, 1.0, 1.0), BooleanOp.OR)
    private val cwest = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 0.5, 1.0, 0.5), BooleanOp.OR)


    private val deast = Shapes.empty()
        .join(Shapes.box(0.5625, 0.0, 0.0, 1.0, 0.4375, 1.0), BooleanOp.OR)
    private val dnorth = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 1.0, 0.4375, 0.4375), BooleanOp.OR)
    private val dsouth = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.5625, 1.0, 0.4375, 1.0), BooleanOp.OR)
    private val dwest = Shapes.empty()
        .join(Shapes.box(0.0, 0.0, 0.0, 0.4375, 0.4375, 1.0), BooleanOp.OR)


    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun getShape(
        pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pContext: CollisionContext
    ): VoxelShape {
        return when (pState.getValue(MultiBlockState.State)) {

            Unformed -> Shapes.block()
            Side -> when (pState.getValue(DirectionalBlock.FACING)) {
                DOWN -> down
                UP -> up
                NORTH -> north
                SOUTH -> south
                WEST -> west
                EAST -> east
            }
            VEdge -> when (pState.getValue(DirectionalBlock.FACING)) {
                NORTH -> cnorth
                SOUTH -> csouth
                WEST -> cwest
                EAST -> ceast
                else -> Shapes.block()
            }
            HEdge -> when (pState.getValue(DirectionalBlock.FACING)) {
                NORTH -> dnorth
                SOUTH -> dsouth
                WEST -> dwest
                EAST -> deast
                else -> Shapes.block()
            }
            Corner -> when (pState.getValue(DirectionalBlock.FACING)) {
                NORTH -> snorth
                SOUTH -> ssouth
                WEST -> swest
                EAST -> seast
                else -> Shapes.block()
            }
            else -> Shapes.block()
        }
    }


}