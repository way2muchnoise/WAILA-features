package wailafeatures.config;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
        if (event.modID.equalsIgnoreCase(Reference.ID))
            loadConfig();
    }

    private static void loadConfig()
    {
        Settings.debugMode = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.debug.title"), Configuration.CATEGORY_GENERAL, false, TranslationHelper.translateToLocal("wailafeatures.config.debug.description"));
        Settings.fuzzyColourMode = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.fuzzyColour.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.fuzzyColour.description"));
        Settings.colourPrefix = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.colourPrefix.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.colourPrefix.description"));
        Settings.materialPrefix = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.materialPrefix.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.materialPrefix.description"));
        Settings.authorIdent = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.authorIdent.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.authorIdent.description"));
        Settings.colourSearch = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.colourSearch.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.colourSearch.description"));
        Settings.materialSearch = config.getBoolean(TranslationHelper.translateToLocal("wailafeatures.config.materialSearch.title"), Configuration.CATEGORY_GENERAL, true, TranslationHelper.translateToLocal("wailafeatures.config.materialSearch.description"));
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
