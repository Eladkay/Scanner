package eladkay.scanner.client.container;

import eladkay.scanner.init.ModContainerTypes;
import eladkay.scanner.tiles.TileEntityScannerQueue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;

public class ScannerQueueContainer extends Container {

    public final TileEntityScannerQueue scanner;

    public ScannerQueueContainer(int pContainerId, PlayerInventory playerInventory, PacketBuffer data) {
        this(pContainerId, playerInventory, (TileEntityScannerQueue) playerInventory.player.level.getBlockEntity(data.readBlockPos()));
    }

    public ScannerQueueContainer(int pContainerId, PlayerInventory playerInventory, TileEntityScannerQueue scanner) {
        super(ModContainerTypes.SCANNER_QUEUE_MENU.get(), pContainerId);
        this.scanner = scanner;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return true;
    }
}
