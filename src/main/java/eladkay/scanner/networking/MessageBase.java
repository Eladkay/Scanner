package eladkay.scanner.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class MessageBase<T> {

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    public abstract void fromBuffer(PacketBuffer buf);

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    public abstract void toBuffer(PacketBuffer buf);

    public T onMessage(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
            handleServerSide((T)this, ctx.get());
        else
            handleClientSide((T)this, Minecraft.getInstance().player);

        ctx.get().setPacketHandled(true);
        return (T)this;
    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param player  the player reference
     */
    public abstract void handleClientSide(T message, PlayerEntity player);

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     *
     * @param message the packet
     * @param context  the context reference
     */
    public abstract void handleServerSide(T message, NetworkEvent.Context context);
}
