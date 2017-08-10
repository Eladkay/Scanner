package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpCircle;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.RandUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@TileRegister("dimensionalCore")
public class TileDimensionalCore extends TileMod implements ITickable {

	@Override
	public void update() {
		if (!world.isRemote) return;

		EnumDimensions dimension = getWorld().getBlockState(getPos()).getValue(BlockDimensionalCore.TYPE);

		Color color;
		switch (dimension) {
			case OVERWORLD:
				color = Color.GREEN;
				break;
			case END:
				color = Color.MAGENTA;
				break;
			case NETHER:
				color = Color.RED;
				break;
			case NONE:
			default:
				color = Color.WHITE;
				break;
		}

		double angle = getWorld().getTotalWorldTime() / 10.0;
		double x = MathHelper.cos((float) angle);
		double y = MathHelper.sin((float) angle);

		ParticleBuilder builder = new ParticleBuilder(10);
		builder.setRender(new ResourceLocation(ScannerMod.MODID, "particles/sparkle_blurred"));
		builder.setCollision(true);
		builder.disableRandom();
		builder.disableMotionCalculation();

		if (dimension == EnumDimensions.OVERWORLD) {
			ParticleSpawner.spawn(builder, getWorld(), new InterpCircle(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(x, x, y), 1.5f), 20, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setScale(0.5f);
				particleBuilder.setColor(color);
				particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
				particleBuilder.setLifetime(RandUtil.nextInt(5, 10));
			});
		} else if (dimension == EnumDimensions.NETHER) {
			ParticleSpawner.spawn(builder, getWorld(), new InterpCircle(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(y, x, y), 1.5f), 20, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setScale(0.5f);
				particleBuilder.setColor(color);
				particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
				particleBuilder.setLifetime(RandUtil.nextInt(10, 20));
			});
		} else if (dimension == EnumDimensions.END) {
			ParticleSpawner.spawn(builder, getWorld(), new InterpCircle(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(y, y, x), 1.5f), 20, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setScale(0.5f);
				particleBuilder.setColor(color);
				particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
				particleBuilder.setLifetime(RandUtil.nextInt(10, 30));
			});
		} else {
			ParticleSpawner.spawn(builder, getWorld(), new InterpCircle(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(x, 1, y), 1.5f), 20, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setScale(0.5f);
				particleBuilder.setColor(color);
				particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
				particleBuilder.setLifetime(RandUtil.nextInt(10, 20));
			});
		}
	}
}
