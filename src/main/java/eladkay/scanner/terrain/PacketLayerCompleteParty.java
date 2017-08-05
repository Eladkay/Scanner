package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.RandUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;
import java.util.function.BiConsumer;

@PacketRegister(Side.CLIENT)
public class PacketLayerCompleteParty extends PacketBase {

	@Save
	private BlockPos start;
	@Save
	private BlockPos end;
	@Save
	private int y;

	public PacketLayerCompleteParty() {
	}

	public PacketLayerCompleteParty(BlockPos start, BlockPos end, int y) {
		this.start = start;
		this.end = end;
		this.y = y;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		for (int i = start.getX(); i < end.getX(); i++) {
			for (int j = start.getZ(); j < end.getZ(); j++) {

				Vec3d pos = new Vec3d(i + 0.5, y, j + 0.5);

				ParticleBuilder builder = new ParticleBuilder(10);
				builder.setRenderNormalLayer(new ResourceLocation(ScannerMod.MODID, "particles/sparkle_blurred"));
				builder.setCollision(true);
				builder.setScale(RandUtil.nextFloat(0.5f, 2));
				builder.enableMotionCalculation();
				builder.setAcceleration(new Vec3d(0, -0.02, 0));

				ParticleSpawner.spawn(builder, Minecraft.getMinecraft().world, new StaticInterp<>(pos), 3, 0, (aFloat, particleBuilder) -> {
					particleBuilder.setMotion(new Vec3d(RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(0.1, 0.3), RandUtil.nextDouble(-0.5, 0.5)));
					particleBuilder.setAlphaFunction(new InterpFadeInOut(0, 1));
					particleBuilder.setLifetime(RandUtil.nextInt(40, 60));
					particleBuilder.setPositionOffset(new Vec3d(RandUtil.nextDouble(-0.2, 0.2), 0, RandUtil.nextDouble(-0.2, 0.2)));

					particleBuilder.setColor(Color.CYAN);
				});
			}
		}
	}
}
