package eladkay.scanner.compat;

public class Waila {
    public static void onWailaCall(/*IWailaRegistrar registrar*/) {
        /*registrar.registerBodyProvider(new Scanner(), BlockTerrainScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockTerrainScanner.class);

        registrar.registerBodyProvider(new Scanner(), BlockBiomeScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockBiomeScanner.class);*/
    }
//
//    public static class Scanner implements IWailaDataProvider {
//
//        @Override
//        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return accessor.getStack();
//        }
//
//        @Override
//        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return currenttip;
//        }
//
//        @Override
//        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            TileEntity tileEntity = accessor.getTileEntity();
//            if (!(tileEntity instanceof BaseTE)) return currenttip;
//            if (((BaseTE) tileEntity).container == null) return currenttip;
//            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ()));
//            int energy = ((BaseTE) tileEntity).getEnergyStored(accessor.getSide());
//            int max = ((BaseTE) tileEntity).getMaxEnergyStored(accessor.getSide());
//            /*int energy = accessor.getNBTData().getInteger("energy");
//            int max = accessor.getNBTData().getInteger("max");*/
//            currenttip.add("Energy: " + energy + "/" + max);
//            return currenttip;
//        }
//
//        @Override
//        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return currenttip;
//        }
//
//        @Override
//        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
//            BaseTE scanner = (BaseTE) te;
//            tag.setInteger("energy", scanner.getEnergyStored(null));
//            tag.setInteger("max", scanner.getMaxEnergyStored(null));
//            return tag;
//        }
//
//    }
}