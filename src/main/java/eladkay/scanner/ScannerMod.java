package eladkay.scanner;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.compat.MineTweaker;
import eladkay.scanner.misc.NetworkHelper;
import eladkay.scanner.proxy.CommonProxy;
import eladkay.scanner.terrain.*;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

@Mod(modid = ScannerMod.MODID, name = "Scanner", version = ScannerMod.VERSION)
public class ScannerMod {
    public static final String MODID = "scanner";
    private static final boolean TESTING = false;

    @SidedProxy(serverSide = "eladkay.scanner.proxy.CommonProxy", clientSide = "eladkay.scanner.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static DimensionType dimOverWorld;
    public static DimensionType dimNether;
    public static DimensionType dimEnd;
    public static BlockTerrainScanner terrainScanner;
    public static BlockScannerQueue scannerQueue;
    public static BlockBiomeScanner biomeScannerBasic;
    public static BlockBiomeScanner biomeScannerAdv;
    public static BlockBiomeScanner biomeScannerElite;
    public static BlockBiomeScanner biomeScannerUltimate;
    public static BlockDimensionalCore dimensionalCore;
    @Mod.Instance(MODID)
    public static ScannerMod instance;
    public static CreativeTabs tab;
    public static BlockAirey air;
    public static final String VERSION = "1.4.1";

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        instance = this;
        tab = new CreativeTabs(MODID) {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(terrainScanner);
            }
        };
        GameRegistry.register(air = new BlockAirey());

        //Terrain Scanner and accessories
        GameRegistry.register(terrainScanner = new BlockTerrainScanner());
        GameRegistry.register(new ItemBlock(terrainScanner) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("The ultimate terrain reconstruction tool.");
                tooltip.add("Its GUI is fairly self-explanatory in my opinion.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":terrainScanner").setCreativeTab(tab));
        GameRegistry.registerTileEntity(TileEntityTerrainScanner.class, "terrainScanner");

        GameRegistry.register(scannerQueue = new BlockScannerQueue());
        GameRegistry.register(new ItemBlock(scannerQueue) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides a buffer of chunks and allows you to queue up chunks for scanning.");
                tooltip.add("Place next to Terrain Scanner.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":scannerQueue").setCreativeTab(tab));
        GameRegistry.registerTileEntity(TileEntityScannerQueue.class, "q");

        GameRegistry.register(dimensionalCore = new BlockDimensionalCore());
        GameRegistry.register(new ItemBlock(dimensionalCore) {

            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                if (stack.getItemDamage() == EnumDimensions.NONE.ordinal()) tooltip.add("Crafting component.");
                else {
                    tooltip.add("Allows you to build chunks from other dimensions. ");
                    tooltip.add("Place next to Terrain Scanner.");
                }
                super.addInformation(stack, playerIn, tooltip, advanced);
            }

            @Override
            public String getUnlocalizedName(ItemStack stack) {
                return super.getUnlocalizedName(stack) + "_" + EnumDimensions.values()[stack.getMetadata()].getName();
            }

            @Override
            public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();

                if (!block.isReplaceable(worldIn, pos)) {
                    pos = pos.offset(facing);
                }

                if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, null, stack)) {
                    int i = stack.getMetadata();
                    IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn, stack);

                    if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
                        SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                        worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        --stack.stackSize;
                    }

                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        }.setHasSubtypes(true).setRegistryName(MODID + ":dimensionalCore").setCreativeTab(tab));


        //Biome Scanner Tiers

        GameRegistry.registerTileEntity(TileEntityBiomeScanner.class, "biomeScanner");

        GameRegistry.register((biomeScannerBasic = (BlockBiomeScanner) new BlockBiomeScanner(0).setRegistryName(ScannerMod.MODID + ":biomeScannerBasic")));
        GameRegistry.register(new ItemBlock(biomeScannerBasic) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides info about biomes far away.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":biomeScannerBasic").setCreativeTab(tab));

        GameRegistry.register((biomeScannerAdv = (BlockBiomeScanner) new BlockBiomeScanner(1).setRegistryName(ScannerMod.MODID + ":biomeScannerAdv")));
        GameRegistry.register(new ItemBlock(biomeScannerAdv) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides info about biomes far away.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":biomeScannerAdv").setCreativeTab(tab));

        GameRegistry.register((biomeScannerElite = (BlockBiomeScanner) new BlockBiomeScanner(2).setRegistryName(ScannerMod.MODID + ":biomeScannerElite")));
        GameRegistry.register(new ItemBlock(biomeScannerElite) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides info about biomes far away.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":biomeScannerElite").setCreativeTab(tab));

        GameRegistry.register((biomeScannerUltimate = (BlockBiomeScanner) new BlockBiomeScanner(3).setRegistryName(ScannerMod.MODID + ":biomeScannerUltimate")));
        GameRegistry.register(new ItemBlock(biomeScannerUltimate) {
            @Override
            public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add("Provides info about biomes far away.");
                super.addInformation(stack, playerIn, tooltip, advanced);
            }
        }.setRegistryName(MODID + ":biomeScannerUltimate").setCreativeTab(tab));


        if (TESTING) {
            Item item = new Item() {
                @Override
                public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                    playerIn.sendMessage(new TextComponentString(String.valueOf(FMLCommonHandler.instance().getMinecraftServerInstance())));
                    return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
                }
            }.setRegistryName("scanner:testytest").setUnlocalizedName("scanner:testytest").setCreativeTab(tab);
            GameRegistry.register(item);
        }
        FMLInterModComms.sendMessage("Waila", "register", "eladkay.scanner.compat.Waila.onWailaCall");
        MineTweaker.init();
        proxy.init();
        Config.initConfig(event.getSuggestedConfigurationFile());
        dimOverWorld = DimensionType.register("fakeoverworld", "", Config.dimid, WorldProviderOverworld.class, true);
        DimensionManager.registerDimension(Config.dimid, dimOverWorld);
        dimNether = DimensionType.register("fakenether", "", Config.dimid + 1, WorldProviderNether.class, true);
        DimensionManager.registerDimension(Config.dimid + 1, dimNether);
        dimEnd = DimensionType.register("fakeend", "", Config.dimid + 2, WorldProviderEnd.class, true);
        DimensionManager.registerDimension(Config.dimid + 2, dimEnd);
        NetworkHelper.init();
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStartingEvent event) {
        event.registerServerCommand(new SpeedTickCommand());
        event.registerServerCommand(new TpToDim99Command());
    }

    public static class WorldProviderOverworld extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dimOverWorld;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderOverworld(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), TileEntityTerrainScanner.PRESET);
        }
    }

    public static class WorldProviderNether extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dimNether;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderHell(world, world.getWorldInfo().isMapFeaturesEnabled(), world.getSeed());
        }
    }

    public static class WorldProviderEnd extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dimEnd;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderEnd(world, world.getWorldInfo().isMapFeaturesEnabled(), world.getSeed());
        }
    }

    public static class SpeedTickCommand extends CommandBase {


        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)");
        }

        @Override
        public String getName() {
            return "speedts";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/speedts";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            TileEntity te = server.worldServerForDimension(getCommandSenderAsPlayer(sender).dimension).getTileEntity(getCommandSenderAsPlayer(sender).getPosition().down());
            if (te instanceof ITickable) for (int i = 0; i < 100000; i++) ((ITickable) te).update();

        }
    }

    public static class TpToDim99Command extends CommandBase {


        @Override
        public String getName() {
            return "goto";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/goto";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 1) return;
            if ("homepls".equals(args[0]))
                getCommandSenderAsPlayer(sender).changeDimension(0);
            else if (args[0].contains("offwego"))
                getCommandSenderAsPlayer(sender).changeDimension(Integer.parseInt(args[0].replace("offwego", "")));

        }
    }
}


