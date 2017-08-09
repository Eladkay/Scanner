package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter;
import eladkay.scanner.misc.PlaceObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@PacketRegister(Side.CLIENT)
public class PacketSyncAnimationQueue extends PacketBase {

	@Save
	private BlockPos pos;
	@Save
	private long worldTime;
	@Save
	private BlockPos placePos;
	private IBlockState fakeState;
	@Nullable
	private NBTTagCompound fakeTE;

	public PacketSyncAnimationQueue() {
	}

	public PacketSyncAnimationQueue(BlockPos pos, BlockPos placePos, IBlockState fakeState, @Nullable TileEntity fakeTE, long worldTime) {
		this.pos = pos;
		this.placePos = placePos;
		this.fakeState = fakeState;
		if (fakeTE != null) this.fakeTE = fakeTE.writeToNBT(new NBTTagCompound());
		this.worldTime = worldTime;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound.hasKey("fake_state")) {
			fakeState = NBTUtil.readBlockState(compound.getCompoundTag("fake_state"));
		}
		if (compound.hasKey("fake_tile")) {
			fakeTE = compound.getCompoundTag("fake_tile");
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (fakeState != null) {
			NBTTagCompound stateNBT = new NBTTagCompound();
			NBTUtil.writeBlockState(stateNBT, fakeState);
			nbt.setTag("fake_state", stateNBT);
		}

		if (fakeTE != null) nbt.setTag("fake_tile", fakeTE);
		return nbt;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		World world = Minecraft.getMinecraft().world;

		if (pos == null) return;

		if (world.isBlockLoaded(pos)) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile instanceof TileEntityTerrainScanner && fakeState != null) {
				TileEntityTerrainScanner scanner = (TileEntityTerrainScanner) tile;
				scanner.animationQueue.add(new PlaceObject(world, fakeState, placePos, fakeTE, worldTime));
				scanner.markDirty();
			}
		}
	}
}
