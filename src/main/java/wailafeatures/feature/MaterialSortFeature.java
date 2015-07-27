package wailafeatures.feature;

import codechicken.nei.SearchField;
import codechicken.nei.api.API;
import codechicken.nei.api.ItemFilter;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import wailafeatures.config.Settings;
import wailafeatures.util.LogHelper;

public class MaterialSortFeature implements IFeature, SearchField.ISearchProvider
{
    @Override
    public void registerFeature(Side side)
    {
        LogHelper.debugInfo("Registering MaterialFilter");
        API.addSearchProvider(this);
    }

    @Override
    public boolean isPrimary()
    {
        return false;
    }

    @Override
    public ItemFilter getFilter(String searchText)
    {
        if (Settings.materialPrefix)
            if (searchText.startsWith("@material:"))
                searchText = searchText.substring(10);
            else
                return null;
        return new MaterialFilter(searchText);
    }

    public class MaterialFilter implements ItemFilter
    {
        private MaterialEnum material;
        
        public MaterialFilter(String searchText)
        {
            try
            {
                this.material = MaterialEnum.valueOf(searchText);
            } catch (IllegalArgumentException ignored) { }
        }

        @Override
        public boolean matches(ItemStack item)
        {
            if (this.material == null) return true;
            if (item.getItem() instanceof ItemBlock)
                return Block.getBlockFromItem(item.getItem()).getMaterial() == material.material;
            return false;
        }
    }
    
    public enum MaterialEnum
    {
        air(Material.air),
        grass(Material.anvil),
        ground(Material.ground),
        wood(Material.wood),
        rock(Material.rock),
        iron(Material.iron),
        anvil(Material.anvil),
        water(Material.water),
        lava(Material.lava),
        leaves(Material.leaves),
        plants(Material.plants),
        vine(Material.vine),
        sponge(Material.sponge),
        cloth(Material.cloth),
        fire(Material.fire),
        sand(Material.sand),
        circuits(Material.circuits),
        carpet(Material.carpet),
        glass(Material.glass),
        redstoneLight(Material.redstoneLight),
        tnt(Material.tnt),
        coral(Material.coral),
        ice(Material.ice),
        packedIce(Material.packedIce),
        snow(Material.snow),
        craftedSnow(Material.craftedSnow),
        cactus(Material.cactus),
        clay(Material.clay),
        gourd(Material.gourd),
        dragonEgg(Material.dragonEgg),
        portal(Material.portal),
        cake(Material.cake),
        web(Material.web),
        piston(Material.piston);

        private Material material;
        MaterialEnum(Material material)
        {
            this.material = material;
        }
    }
}
