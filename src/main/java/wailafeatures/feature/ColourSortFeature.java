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
import wailafeatures.config.Settings;
import wailafeatures.reference.Colours;
import wailafeatures.util.LogHelper;
import wailafeatures.util.Tuple;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ColourSortFeature implements IFeature, SearchField.ISearchProvider
{
    private Map<Colour, List<ItemStack>> colourMap;
    private List<ItemStack> checkedItems;
    private Side side;

    @Override
    public void registerFeature(Side side)
    {
        LogHelper.debugInfo("Registering ColourFilter");
        API.addSearchProvider(this);
        this.side = side;
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
        return Settings.debugMode;
    }

    @Override
    public ItemFilter getFilter(String searchText)
    {
        if (!Settings.colourSearch) return null;
        if (Settings.colourPrefix)
        {
            if (searchText.startsWith("@colour:"))
                searchText = searchText.substring(8);
            else
                return null;
        }
        return Settings.fuzzyColourMode ? new FuzzyColourFilter(searchText) : new ColourFilter(searchText);
    }

    public void checkItem(ItemStack itemStack)
    {
        Colour colour = null;
        if (itemStack.getItem() instanceof ItemBlock)
            colour = calcColour(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
        else
            colour = calcColour(itemStack, itemStack.getItem(), itemStack.getItemDamage());
        if (colour != null)
        {
            List<ItemStack> stacks = this.colourMap.get(colour);
            if (stacks == null)
                stacks = new LinkedList<ItemStack>();
            stacks.add(itemStack);
            LogHelper.debugInfo("Mapped " + itemStack.getDisplayName() + " as " + colour.name());
            this.colourMap.put(colour, stacks);
        }
        this.checkedItems.add(itemStack);
    }

    private Colour calcColour(Block block, int damage)
    {
        Set<String> iconNames = new LinkedHashSet<String>();
        // Get all block sides
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

            if (name.contains(":"))
            {
                String[] split = name.split(":");
                resourceLocation = new ResourceLocation(split[0] + ":textures/blocks/" + split[1] + ".png");
            } else
                resourceLocation = new ResourceLocation("textures/blocks/" + name + ".png");

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

    private Colour calcColour(ItemStack itemStack, Item item, int damage)
    {
        ResourceLocation resourceLocation;
        List<Integer> colours = new LinkedList<Integer>();

        /*
        Get all item Icons based on the render pass
        when a pass has no icon skip it
         */
        for (int i = 0; item.getRenderPasses(damage) > i; i++)
        {
            IIcon icon = item.getIconFromDamage(damage);
            if (icon == null) continue;

            // On the client side check the render colour of the itemstack
            if (ColourSortFeature.this.side == Side.CLIENT)
            {
                int colour = item.getColorFromItemStack(itemStack, i);
                // When the render colour is not the default take that as could and don't do pixel check
                if (colour != 16777215)
                {
                    colours.add(colour);
                    break;
                }
            }

            String name = icon.getIconName();

            if (name.contains(":"))
            {
                String[] split = name.split(":");
                resourceLocation = new ResourceLocation(split[0] + ":textures/items/" + split[1] + ".png");
            } else
                resourceLocation = new ResourceLocation("textures/items/" + name + ".png");

            try
            {
                BufferedImage bufferedImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());

                colours.add(getIntColour(bufferedImage));
            } catch (IOException ignore) {}
        }

        if (colours.size() == 0) return Colour.black;
        return Colour.find(colours.size() > 1 ? Colours.blend(colours.toArray(new Integer[colours.size()])) : colours.get(0));
    }

    private int getIntColour(BufferedImage image)
    {
        Map<Integer, Integer> colourCount = new HashMap<Integer, Integer>();

        /*
        Gather a map where the keys are the colours of pixels
        and the value is the amount of times they occur
         */
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

        /*
        Remove the transparent colour
        This is needed for items since they have lots of transparent pixels
         */
        colourCount.remove(0);

        List<Tuple<Integer, Integer>> coloursFinal = new LinkedList<Tuple<Integer, Integer>>();

        /*
        Create a list of Tuples where the first value of the tuple is the colour and the second is the occurrence
        When 2 int colours are close together mix the key value with the tuple's first object
        And add the occurrences to the counter in the second object of the tuple
         */
        for (Map.Entry<Integer, Integer> entry : colourCount.entrySet())
        {
            if (coloursFinal.size() == 0)
            {
                Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(entry);
                coloursFinal.add(tuple);
            }
            else
            {
                Tuple<Integer, Integer> tuple = null;
                for (Iterator<Tuple<Integer, Integer>> itr = coloursFinal.iterator(); itr.hasNext(); )
                {
                    tuple = itr.next();
                    double d = Math.sqrt(Math.pow((Colours.getRed(tuple.getFirst()) - Colours.getRed(entry.getKey())), 2) + Math.pow((Colours.getGreen(tuple.getFirst()) - Colours.getGreen(entry.getKey())), 2) + Math.pow((Colours.getBlue(tuple.getFirst()) - Colours.getBlue(entry.getKey())), 2));
                    if (d < 25/255F)
                    {
                        itr.remove();
                        tuple.setFirst(Colours.blend(tuple.getFirst(), entry.getKey()));
                        tuple.setSecond(tuple.getSecond() + entry.getValue());
                        break;
                    }
                    tuple = null;
                }
                if (tuple == null)
                    tuple = new Tuple<Integer, Integer>(entry);
                coloursFinal.add(tuple);
            }
        }

        /*
        Find the most dominant colour
         */
        int dom = 0;
        int max = 0;
        for (Tuple<Integer, Integer> entry : coloursFinal)
        {
            if (entry.getSecond() > max)
            {
                dom = entry.getFirst();
                max = entry.getSecond();
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

    public class FuzzyColourFilter extends ColourFilter
    {
        private FuzzyColour fuzzyColour;

        public FuzzyColourFilter(String searchText)
        {
            super(searchText);
            this.fuzzyColour = FuzzyColour.find(searchText);
        }

        @Override
        public boolean matches(ItemStack itemStack)
        {
            boolean parent = super.matches(itemStack);
            if (fuzzyColour == null) return parent;
            if (!ColourSortFeature.this.checkedItems.contains(itemStack))
                ColourSortFeature.this.checkItem(itemStack);
            List<ItemStack> list = new LinkedList<ItemStack>();
            for (Colour colour : fuzzyColour.colours)
                if (ColourSortFeature.this.colourMap.containsKey(colour))
                    list.addAll(ColourSortFeature.this.colourMap.get(colour));
            return list.contains(itemStack);
        }
    }

    public enum FuzzyColour
    {
        green(Colour.green, Colour.lime),
        gray(Colour.gray, Colour.lightGray),
        red(Colour.red, Colour.maroon),
        purple(Colour.purple, Colour.magenta),
        blue(Colour.blue, Colour.navy, Colour.teal);

        public Colour[] colours;

        FuzzyColour(Colour... colours)
        {
            this.colours = colours;
        }

        public static FuzzyColour find(String value)
        {
            for (FuzzyColour colour : values())
            {
                if (colour.name().equalsIgnoreCase(value))
                    return colour;
            }
            return null;
        }
    }

    public enum Colour
    {
        white(new int[]{255, 255, 255}),
        black(new int[]{0, 0, 0}),
        green(new int[]{0, 128, 0}),
        lime(new int[]{0, 255, 0}),
        red(new int[]{255, 0, 0}),
        maroon(new int[]{128, 0, 0}),
        navy(new int[]{0, 0, 128}),
        blue(new int[]{0, 0, 255}),
        magenta(new int[]{255, 0, 255}),
        teal(new int[]{0, 128, 128}),
        orange(new int[]{255, 140, 0}),
        yellow(new int[]{255, 255, 0}),
        purple(new int[]{128, 0, 128}),
        cyan(new int[]{0, 255, 255}),
        pink(new int[]{255, 192, 203}),
        brown(new int[]{139 ,69, 19}),
        gray(new int[]{128, 128, 128}),
        lightGray(new int[]{192, 192, 192});

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
            int i = 0;
            while (true)
            {
                for (Colour colour : values())
                {
                    double d = Math.sqrt(Math.pow((Colours.getRed(intColour) - colour.rgb[0] / 255F), 2) + Math.pow((Colours.getGreen(intColour) - colour.rgb[1] / 255F), 2) + Math.pow((Colours.getBlue(intColour) - colour.rgb[2] / 255F), 2));
                    if (d < i / 255F)
                    {
                        return colour;
                    }
                }
                i+=1;
            }
        }
    }
}
