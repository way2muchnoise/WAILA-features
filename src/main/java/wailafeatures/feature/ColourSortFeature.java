package wailafeatures.feature;

import codechicken.nei.SearchField;
import codechicken.nei.api.API;
import codechicken.nei.api.ItemFilter;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import wailafeatures.reference.Colours;
import wailafeatures.util.LogHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ColourSortFeature implements IFeature, SearchField.ISearchProvider
{
    private Map<Colour, List<ItemStack>> colourMap;
    private List<ItemStack> checkedItems;

    @Override
    public void registerFeature(Side side)
    {
        LogHelper.debugInfo("Registering ColourFilter");
        API.addSearchProvider(this);
        if (side == Side.CLIENT)
            ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new TextureReloadListener());
    }

    public class TextureReloadListener implements IResourceManagerReloadListener
    {
        @Override
        public void onResourceManagerReload(IResourceManager rm)
        {
            ColourSortFeature.this.colourMap.clear();
            ColourSortFeature.this.checkedItems.clear();
        }
    }

    public ColourSortFeature()
    {
        this.colourMap = new HashMap<Colour, List<ItemStack>>();
        this.checkedItems = new LinkedList<ItemStack>();
    }

    @Override
    public boolean isPrimary()
    {
        return true;
    }

    @Override
    public ItemFilter getFilter(String searchText)
    {
        return new ColourFilter(searchText);
    }

    public void checkItem(ItemStack itemStack)
    {
        Colour colour = null;
        if (itemStack.getItem() instanceof ItemBlock)
            colour = calcColour(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
        else
            colour = calcColour(itemStack.getItem(), itemStack.getItemDamage());
        if (colour != null)
        {
            List<ItemStack> stacks = this.colourMap.get(colour);
            if (stacks == null)
                stacks = new LinkedList<ItemStack>();
            stacks.add(itemStack);
            LogHelper.info("Mapped " + itemStack.getDisplayName() + " as " + colour.name());
            this.colourMap.put(colour, stacks);
        }
        this.checkedItems.add(itemStack);
    }

    private Colour calcColour(Block block, int damage)
    {
        Set<String> iconNames = new LinkedHashSet<String>();
        for (int i = 0; i < 6; i++)
        {
            IIcon icon = block.getIcon(i, damage);
            if (icon == null) continue;
            iconNames.add(icon.getIconName());
        }

        List<Integer> colours = new LinkedList<Integer>();

        for (String name : iconNames)
        {
            ResourceLocation resourceLocation;

            if (name.contains(":")) {
                String[] split = name.split(":");
                resourceLocation = new ResourceLocation(split[0] + ":textures/blocks/" + split[1] + ".png");
            } else {
                resourceLocation = new ResourceLocation("textures/blocks/" + name + ".png");
            }

            try
            {
                BufferedImage bufferedImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());

                if (bufferedImage == null)
                    continue;

                colours.add(getIntColour(bufferedImage));
            } catch (IOException ignore) {}
        }

        if (colours.size() == 0) return Colour.black;
        return Colour.find(colours.size() > 1 ? Colours.blend(colours.toArray(new Integer[colours.size()])) : colours.get(0));
    }

    private Colour calcColour(Item item, int damage)
    {
        IIcon icon = item.getIconFromDamage(damage);
        if (icon == null)
            return null;

        String name = icon.getIconName();
        ResourceLocation resourceLocation;
        int colour = 0;

        if (name.contains(":")) {
            String[] split = name.split(":");
            resourceLocation = new ResourceLocation(split[0] + ":textures/items/" + split[1] + ".png");
        } else {
            resourceLocation = new ResourceLocation("textures/items/" + name + ".png");
        }

        try
        {
            BufferedImage bufferedImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());

            colour = getIntColour(bufferedImage);
        } catch (IOException ignore) {}

        return Colour.find(colour);
    }

    private int getIntColour(BufferedImage image)
    {
        Map<Integer, Integer> colourCount = new HashMap<Integer, Integer>();

        for (int w = 0; w < image.getWidth(); w++)
        {
            for (int h = 0; h < image.getHeight(); h++)
            {
                int colour = image.getRGB(w, h);
                if (colourCount.containsKey(colour))
                    colourCount.put(colour, colourCount.get(colour)+1);
                else
                    colourCount.put(colour, 1);
            }
        }

        int dom = 0;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : colourCount.entrySet())
        {
            if (entry.getKey() != 0 && entry.getValue() > max)
            {
                dom = entry.getKey();
                max = entry.getValue();
            }
        }

        return dom;
    }

    public class ColourFilter implements ItemFilter
    {
        private Colour colour;

        public ColourFilter(String searchText)
        {
            this.colour = Colour.find(searchText);
        }

        @Override
        public boolean matches(ItemStack itemStack)
        {
            if (colour == null) return true;
            if (!ColourSortFeature.this.checkedItems.contains(itemStack))
                ColourSortFeature.this.checkItem(itemStack);
            List<ItemStack> list = ColourSortFeature.this.colourMap.get(colour);
            return list != null && list.contains(itemStack);
        }
    }

    public enum Colour
    {
        white(new int[]{255, 255, 255}),
        pink(new int[]{255, 192, 203}),
        brown(new int[]{139 ,69, 19}),
        orange(new int[]{255, 165, 0}),
        magenta(new int[]{255, 0, 255}),
        purple(new int[]{128, 0, 128}),
        gray(new int[]{128, 128, 128}),
        lightGray(new int[]{192, 192, 192}),
        green(new int[]{0, 128, 0}),
        lime(new int[]{0, 255, 0}),
        red(new int[]{255, 0, 0}),
        blue(new int[]{0, 0, 255}),
        yellow(new int[]{255, 255, 0}),
        black(new int[]{0, 0, 0});

        private Set<String> aliases;
        private int[] rgb;

        Colour(int[] rgb, String... aliases)
        {
            this.aliases = new LinkedHashSet<String>();
            for (String alias : aliases)
                this.aliases.add(alias.toLowerCase());
            this.rgb = rgb;
        }

        public static Colour find(String value)
        {
            for (Colour colour : values())
            {
                if (colour.name().equalsIgnoreCase(value) || colour.aliases.contains(value.toLowerCase()))
                    return colour;
            }
            return null;
        }

        public static Colour find(int intColour)
        {
            for (int i = 0; i < 50; i+=2)
            {
                for (Colour colour : values())
                {
                    double d = Math.sqrt(Math.pow((Colours.getRed(intColour) - colour.rgb[0] / 255F), 2) + Math.pow((Colours.getGreen(intColour) - colour.rgb[1] / 255F), 2) + Math.pow((Colours.getBlue(intColour) - colour.rgb[1] / 255F), 2));
                    if (d < i / 255F)
                    {
                        return colour;
                    }
                }
            }
            return null;
        }
    }
}
