package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import eladkay.scanner.misc.RandUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;

@TileRegister("dimensionalCore")
public class TileDimensionalCore extends TileMod implements ITickable {

	@Override
	public void update() {
		if (!world.isRemote) return;
		System.out.println("aaaa");
		for (int i = 0; i < 3; i++) {
			Vec3d vec = new Vec3d(getPos()).addVector(0.5, 0.5, 0.5).addVector(RandUtil.nextDouble(-0.25, 0.25), RandUtil.nextDouble(-0.25, 0.25), RandUtil.nextDouble(-0.25, 0.25));
			world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, vec.xCoord, vec.yCoord, vec.zCoord, 0, 1, 0);
		}
	}
}
