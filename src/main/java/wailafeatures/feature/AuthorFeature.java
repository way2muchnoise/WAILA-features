package wailafeatures.feature;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import wailafeatures.util.AuthorIdent;
import wailafeatures.util.LogHelper;
import wailafeatures.util.TranslationHelper;

import java.util.List;

public class AuthorFeature implements IFeature, IContainerTooltipHandler, IWailaDataProvider, IWailaEntityProvider
{
    @Override
    public void registerFeature(Side side)
    {
        LogHelper.debugInfo("Registering AuthorInfo");
        AuthorIdent.init();
        GuiContainerManager.addTooltipHandler(this);
        ModuleRegistrar.instance().registerTailProvider((IWailaDataProvider)this, Block.class);
        ModuleRegistrar.instance().registerTailProvider((IWailaEntityProvider)this, Entity.class);
    }

    private List<String> addModAuthors(ItemStack itemStack, List<String> currenttip)
    {
        if (itemStack != null && itemStack.getItem() != null)
        {
            String authors = AuthorIdent.nameFromObject(itemStack.getItem());
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && authors != null && !authors.equals(""))
                currenttip.add("\u00a79\u00a7o" + TranslationHelper.translateToLocal("wailafeatures.author.madeBy") + " " + authors);
        }
        return currenttip;
    }

    private List<String> addModAuthors(Entity entity, List<String> currenttip)
    {
        if (entity != null)
        {
            String authors = AuthorIdent.nameFromObject(entity);
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && authors != null && !authors.equals(""))
                currenttip.add("\u00a79\u00a7o" + TranslationHelper.translateToLocal("wailafeatures.author.madeBy") + " " + authors);
        }
        return currenttip;
    }

    @Override
    public List<String> handleTooltip(GuiContainer guiContainer, int i, int i1, List<String> currenttip)
    {
        return currenttip;
    }

    @Override
    public List<String> handleItemDisplayName(GuiContainer guiContainer, ItemStack itemStack, List<String> currenttip)
    {
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer guiContainer, ItemStack itemStack, int i, int i1, List<String> currenttip)
    {
        return addModAuthors(itemStack, currenttip);
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
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
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
