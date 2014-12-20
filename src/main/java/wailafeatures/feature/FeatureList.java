package wailafeatures.feature;

public enum FeatureList
{
    author(new AuthorFeature());

    private IFeature feature;
    private FeatureList(IFeature feature)
    {
        this.feature = feature;
    }

    public void register()
    {
        this.feature.registerFeature();
    }

    public static void registerFeatures()
    {
        for (FeatureList feature : FeatureList.values())
            feature.register();
    }
}
