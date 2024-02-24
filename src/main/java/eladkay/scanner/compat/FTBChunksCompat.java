package eladkay.scanner.compat;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.data.Protection;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.UUID;

public class FTBChunksCompat {

    public static boolean isFTBChunksLoaded() {
        return ModList.get().getModFileById("ftbchunks") != null;
    }

    public static boolean checkClaimed(BlockPos pos, World world, UUID placer, BlockState state) {
        if (!isFTBChunksLoaded()) return false;
        ClaimedChunk claimedChunk = FTBChunksAPI.getManager().getChunk(new ChunkDimPos(world, pos));
        if (claimedChunk == null) return false;
        if (placer.equals(new UUID(0, 0))) {
            return true; // If it is not placed by player it should not allow if it is in a claim
        }
        ServerPlayerEntity player = world.getServer().getPlayerList().getPlayer(placer);
        if (player == null) {
            return true; // Player is not online so the scanner can't build in a claim
        }
        if (FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, pos, Protection.EDIT_BLOCK)) {
            return true; // Player does not have the permission to touch that claim
        }
        return false;
    }

    public static int getMinimapTextureId() {
        if(!isFTBChunksLoaded()) return -1;
        return FTBChunksClient.minimapTextureId;
    }
}
