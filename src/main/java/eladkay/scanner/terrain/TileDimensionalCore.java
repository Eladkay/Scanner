package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.RandUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@TileRegister("dimensionalCore")
public class TileDimensionalCore extends TileMod implements ITickable {

	@Override
	public void update() {
		if (!world.isRemote) return;

		ParticleBuilder builder = new ParticleBuilder(10);
		builder.setRender(new ResourceLocation(ScannerMod.MODID, "particles/sparkle_blurred"));
		builder.setCollision(true);
		ParticleSpawner.spawn(builder, getWorld(), new StaticInterp<>(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5)), 5, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setScale(RandUtil.nextFloat());
			particleBuilder.setMotion(new Vec3d(RandUtil.nextDouble(-0.03, 0.03), RandUtil.nextDouble(-0.03, 0.03), RandUtil.nextDouble(-0.03, 0.03)));
			particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
			particleBuilder.setLifetime(RandUtil.nextInt(20, 30));
			particleBuilder.setPositionOffset(new Vec3d(RandUtil.nextDouble(-0.25, 0.25), RandUtil.nextDouble(-0.25, 0.25), RandUtil.nextDouble(-0.25, 0.25)));
			particleBuilder.setColorFunction(new InterpColorHSV(Color.CYAN, Color.BLUE));
		});
	}
}
