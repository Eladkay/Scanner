package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter;
import eladkay.scanner.misc.PlaceObject;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@PacketRegister(Side.CLIENT)
public class PacketSyncAnimationQueue extends PacketBase {

	@Save
	private BlockPos pos;
	private ArrayList<PlaceObject> animationQueue;

	public PacketSyncAnimationQueue() {
	}

	public PacketSyncAnimationQueue(BlockPos pos, ArrayList<PlaceObject> animationQueue) {
		this.pos = pos;
		this.animationQueue = animationQueue;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound.hasKey("anim_queue")) {
			animationQueue.clear();
			NBTTagList list = compound.getTagList("anim_queue", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				PlaceObject object = new PlaceObject();
				object.deserializeNBT(list.getCompoundTagAt(i));
				animationQueue.add(object);
			}
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		if (animationQueue == null) return new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();

		ArrayList<PlaceObject> temp = new ArrayList<>(animationQueue);
		for (int i = 0; i < animationQueue.size(); i++) {
			list.appendTag(temp.get(i).serializeNBT());
		}
		nbt.setTag("anim_queue", list);
		return nbt;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		World world = Minecraft.getMinecraft().world;

		if (pos == null) return;

		if (world.isBlockLoaded(pos)) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile instanceof TileEntityTerrainScanner) {
				TileEntityTerrainScanner scanner = (TileEntityTerrainScanner) tile;
				scanner.animationQueue = animationQueue;
			}
		}
	}
}
