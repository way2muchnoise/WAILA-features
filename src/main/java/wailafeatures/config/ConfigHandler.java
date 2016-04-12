package wailafeatures.config;

import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import wailafeatures.reference.Reference;
import wailafeatures.util.TranslationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler
{
    public static Configuration config;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Reference.ID))
            loadConfig();
    }

    private static void loadConfig()
    {
        Settings.debugMode = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.debug.title"), Configuration.CATEGORY_GENERAL, false, TranslationHelper.translateToLocal("wailafeatures.config.debug.description"));
        Settings.authorIdent = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.authorIdent.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.authorIdent.description"));
        if (config.hasChanged())
            config.save();
    }

    @SuppressWarnings("unchecked")
    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
        return list;
    }
}
