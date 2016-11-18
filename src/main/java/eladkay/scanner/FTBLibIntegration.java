package eladkay.scanner;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 15.11.2016.
 */
public enum FTBLibIntegration implements IFTBLibPlugin
{
    @FTBLibPlugin
    INSTANCE;

    public static FTBLibAPI API;

    @Override
    public void init(@Nonnull FTBLibAPI api)
    {
        API = api;
    }
}