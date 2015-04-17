package wailafeatures.util;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class AuthorIdent
{
    private static Map<String, String> authorMap = new HashMap<String, String>();

    public static void init()
    {
        for (ModContainer mod : Loader.instance().getModList()){
            authorMap.put(mod.getSource().getName(), mod.getMetadata().getAuthorList());
        }

        authorMap.put("1.7.10.jar", "Mojang");
        authorMap.put("1.8.jar", "Mojang");
        authorMap.put("forgeSrc", "Mojang");
        authorMap.put("Forge", "Mojang");
    }

    public static String nameFromObject(Object obj)
    {
        String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        try
        {
            objPath = URLDecoder.decode(objPath, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        String authorName = "<Unknown>";
        for (String s : authorMap.keySet())
        {
            if (objPath.contains(s))
            {
                authorName = authorMap.get(s);
                break;
            }
        }

        return authorName;
    }
}
