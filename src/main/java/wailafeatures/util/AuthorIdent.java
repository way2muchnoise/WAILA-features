package wailafeatures.util;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class AuthorIdent
{
    private static Map<String, String> authorMap = new HashMap<>();

    public static void init()
    {
        for (ModContainer mod : Loader.instance().getModList())
            if (mod!= null && mod.getModId() != null && mod.getMetadata() != null)
                authorMap.put(mod.getModId().toLowerCase(), mod.getMetadata().getAuthorList());
        authorMap.put("minecraft", "Mojang");
    }

    public static String nameFromItem(Item item)
    {
        String author = authorMap.get(item.getRegistryName().getResourceDomain().toLowerCase());
        return author == null ? "<Unknown>" : author;
    }

    public static String nameFromEntity(Entity entity)
    {
        String author = "Mojang";
        EntityRegistry.EntityRegistration reg = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
        if (reg != null)
            author = authorMap.get(reg.getContainer().getModId());
        return author;
    }

}
