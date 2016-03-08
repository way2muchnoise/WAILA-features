package wailafeatures.feature;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.input.Keyboard;
import wailafeatures.config.Settings;
import wailafeatures.util.AuthorIdent;
import wailafeatures.util.LogHelper;
import wailafeatures.util.TranslationHelper;

import java.util.List;

public class AuthorFeature implements IFeature, IWailaDataProvider, IWailaEntityProvider
{
    @Override
    public void registerFeature(Side side)
    {
        LogHelper.debugInfo("Registering AuthorInfo");
        AuthorIdent.init();
        ModuleRegistrar.instance().registerTailProvider((IWailaDataProvider) this, Block.class);
        ModuleRegistrar.instance().registerTailProvider((IWailaEntityProvider) this, Entity.class);
    }

    private <T extends List<String>> T addModAuthors(ItemStack itemStack, T currenttip)
    {
        if (Settings.authorIdent && itemStack != null && itemStack.getItem() != null)
        {
            String authors = AuthorIdent.nameFromItem(itemStack.getItem());
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && authors != null && !authors.equals(""))
                currenttip.add("\u00a79\u00a7o" + TranslationHelper.translateToLocal("wailafeatures.author.madeBy") + " " + authors);
        }
        return currenttip;
    }

    private <T extends List<String>> T addModAuthors(Entity entity, T currenttip)
    {
        if (Settings.authorIdent && entity != null)
        {
            String authors = AuthorIdent.nameFromEntity(entity);
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && authors != null && !authors.equals(""))
                currenttip.add("\u00a79\u00a7o" + TranslationHelper.translateToLocal("wailafeatures.author.madeBy") + " " + authors);
        }
        return currenttip;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return addModAuthors(itemStack, currenttip);
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return tag;
    }


    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config)
    {
        return addModAuthors(entity, currenttip);
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world)
    {
        return tag;
    }
}
