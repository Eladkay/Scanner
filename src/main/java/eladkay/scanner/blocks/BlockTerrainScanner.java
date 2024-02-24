package eladkay.scanner.blocks;

import eladkay.scanner.tiles.TileEntityTerrainScanner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockTerrainScanner extends Block {
    public static BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BlockTerrainScanner() {
        super(Properties.of(Material.METAL).strength(5));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
        //setRegistryName(ScannerMod.MODID + ":terrain_scanner");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityTerrainScanner();
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        if (pLevel.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            if (pLevel.getBlockEntity(pPos) != null) {
                NetworkHooks.openGui((ServerPlayerEntity) pPlayer, (TileEntityTerrainScanner)pLevel.getBlockEntity(pPos), pPos);
            }
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void destroy(IWorld pLevel, BlockPos pPos, BlockState pState) {
        //We need to do this always in case the config has changed since the block as added.
        BlockPos start = pPos.east().below(pPos.getY());
        for (BlockPos p : BlockPos.Mutable.betweenClosed(start, start.offset(15, 255, 15))) {
            BlockState state = pLevel.getBlockState(p);
            if (state.getBlock().isAir(state, pLevel, p))
                pLevel.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        }
        super.destroy(pLevel, pPos, pState);
    }

    @Override
    public void wasExploded(World pLevel, BlockPos pPos, Explosion pExplosion) {
        //We need to do this always in case the config has changed since the block as added.
        BlockPos start = pPos.east().below(pPos.getY());
        for (BlockPos p : BlockPos.Mutable.betweenClosed(start, start.offset(15, 255, 15))) {
            BlockState state = pLevel.getBlockState(p);
            if (state.getBlock().isAir(state, pLevel, p))
                pLevel.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        }
        super.wasExploded(pLevel, pPos, pExplosion);
    }

    @Override
    public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide) {
            TileEntity te = pLevel.getBlockEntity(pPos);
            TileEntityTerrainScanner tets = ((TileEntityTerrainScanner) te);
            if (te == null) return;
            if (pPlacer instanceof PlayerEntity) {
                tets.placer = pPlacer.getUUID();
                PlayerEntity player = (PlayerEntity) pPlacer;
                //noinspection ConstantConditions
                if (player != null)
                    tets.placerName = player.getName();
                else
                    tets.placerName = new StringTextComponent("");
            } else {
                tets.placer = new UUID(0, 0);
                tets.placerName = new StringTextComponent("");
            }
            te.setChanged();
        }
    }


    /*@Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, BlockState state) {
        onBlockDestroyedByExplosion(worldIn, pos, null);
    }*/

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        TileEntity te = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        return te != null && ((TileEntityTerrainScanner) te).powered ? this.defaultBlockState().setValue(POWERED, true) : this.defaultBlockState().setValue(POWERED, false);
    }

}
