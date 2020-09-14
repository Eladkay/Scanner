package eladkay.scanner.terrain;

import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.feed_the_beast.ftbutilities.data.ClaimedChunks.isActive;

public class TerrainScannerUtils {

    public static boolean checkClaimed(BlockPos pos, World world, UUID placer, IBlockState state) {
        if (!ScannerMod.ftbu) //If ftbu is not present, it's surely not claimed
            return false;
        if (pos.getY() > 1) //If it's claimed, should stop at y=1
            return false;
        if (placer == null) //Old scanners, keep these working
            return false;
        int dim = world.provider.getDimension();
        if (placer.equals(new UUID(0,0))) //New scanners without a real player, only builds if the chunk is not claimed
            return ClaimedChunks.instance.getChunk(new BlockDimPos(pos.getX(), 1, pos.getZ(), dim).toChunkPos()) != null;
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(placer);
        if (player == null) //Placer offline, only builds if the chunk is not claimed
            return ClaimedChunks.instance.getChunk(new BlockDimPos(pos.getX(), 1, pos.getZ(), dim).toChunkPos()) != null;
        return blockBlockEditing(player, pos, state, dim, world);
    }

    public static boolean blockBlockEditing(EntityPlayer player, BlockPos pos, @Nullable IBlockState state, int dim, World world) {
        if (isActive() && world != null && player instanceof EntityPlayerMP) {
            if (state == null) {
                state = world.getBlockState(pos);
            }
            ClaimedChunk chunk = ClaimedChunks.instance.getChunk(new ChunkDimPos(pos, dim));
            return chunk != null && !FTBUtilitiesPermissions.hasBlockEditingPermission(player, state.getBlock()) && !chunk.getTeam().hasStatus(ClaimedChunks.instance.universe.getPlayer(player), chunk.getData().getEditBlocksStatus());
        } else {
            return false;
        }
    }
}
