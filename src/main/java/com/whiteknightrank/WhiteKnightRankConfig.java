package com.whiteknightrank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("whiteknightrank")
public interface WhiteKnightRankConfig extends Config
{
	@ConfigItem(keyName = "showInfobox", name = "Show Infobox", description = "Show an infobox with your current KC and rank")
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(keyName = "showOverlay", name = "Show Overlay", description = "Show an overlay when actively killing knights")
	default boolean showOverlay()
	{
		return true;
	}

//	@ConfigItem(keyName = "showNotificationOnRank", name = "Notify on rank", description = "Receive a notification when you rank up")
//	default boolean showNotificationOnRank()
//	{
//		return true;
//	}
}
