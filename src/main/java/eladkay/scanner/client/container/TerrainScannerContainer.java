package eladkay.scanner.client.container;

import eladkay.scanner.init.ModContainerTypes;
import eladkay.scanner.tiles.TileEntityTerrainScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;

public class TerrainScannerContainer extends Container {

    public final TileEntityTerrainScanner scanner;

    public TerrainScannerContainer(int pContainerId, PlayerInventory playerInventory, PacketBuffer data) {
        this(pContainerId, playerInventory, (TileEntityTerrainScanner) playerInventory.player.level.getBlockEntity(data.readBlockPos()));
    }

    public TerrainScannerContainer(int pContainerId, PlayerInventory playerInventory, TileEntityTerrainScanner scanner) {
        super(ModContainerTypes.TERRAIN_SCANNER_MENU.get(), pContainerId);
        this.scanner = scanner;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return true;
    }
}
