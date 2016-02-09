package wailafeatures;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import wailafeatures.config.ConfigHandler;
import wailafeatures.feature.FeatureList;
import wailafeatures.proxy.CommonProxy;
import wailafeatures.reference.MetaData;
import wailafeatures.reference.Reference;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL, guiFactory = Reference.GUI_FACTORY, dependencies = "after:JEI@[2.23.0,);after:Waila")
public class WailaFeatures
{
    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = MetaData.init(metadata);
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
    }

    @Mod.EventHandler
    public void onServerLoad(FMLLoadCompleteEvent event)
    {
        FeatureList.registerFeatures(event.getSide());
    }

}
