package wailafeatures.util;

import net.minecraft.util.StatCollector;

public class TranslationHelper
{
    public static String translateToLocal(String key)
    {
        return StatCollector.translateToLocal(key);
    }
}
