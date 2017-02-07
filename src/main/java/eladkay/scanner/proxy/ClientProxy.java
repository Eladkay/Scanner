package eladkay.scanner.proxy;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.terrain.GuiScannerQueue;
import eladkay.scanner.terrain.GuiTerrainScanner;
import eladkay.scanner.terrain.TileEntityScannerQueue;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ClientProxy extends CommonProxy {
    private static final String IP = "http://eladkay.pw/scanner/ScannerCallback.php";
    private static boolean sentCallback = false;

    @Override
    public void init() {
        super.init();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.terrainScanner), 0, new ModelResourceLocation("scanner:terrainScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.scannerQueue), 0, new ModelResourceLocation("scanner:scannerQueue", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerBasic), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerAdv), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerElite), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.biomeScannerUltimate), 0, new ModelResourceLocation("scanner:biomeScanner", "inventory"));

       /* if(Config.showOutline)
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerrainScanner.class, new TileEntitySpecialRendererTerrainScanner());*/
        MinecraftForge.EVENT_BUS.register(this);

    }

    /**
     * There's definitely a better way to do this, but who am I to judge
     *
     * @param event The event
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (sentCallback || Minecraft.getMinecraft().player == null) return;
        try {
            if (Minecraft.getMinecraft().player.getName().matches("(?:Player\\d{1,3})")) return;
            new Thread(() -> {
                try {
                    sendGet("ScannerMod", IP + "?username=" + Minecraft.getMinecraft().player.getName() + "&timestamp=" + new Date().toString().replace(" ", "") + "&version=" + ScannerMod.VERSION);
                } catch (Exception e) {
                    sentCallback = true;
                    e.printStackTrace();
                }
            }).start();
            //System.out.println(IP + "?username=" + Minecraft.getMinecraft().player.getName() + "&timestamp=" + new Date().toString().replace(" ", "") + "&version=" + ScannerMod.VERSION);
            sentCallback = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendGet(String userAgent, String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", userAgent);
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    @Override
    public void openGuiBiomeScanner(TileEntityBiomeScanner tileEntity) {
        new GuiBiomeScanner(tileEntity).openGui();
    }

    @Override
    public void openGuiTerrainScanner(TileEntityTerrainScanner tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerrainScanner(tileEntity));
    }

    @Override
    @Nullable
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public void openGuiScannerQueue(TileEntityScannerQueue tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScannerQueue(tileEntity));
    }
}
