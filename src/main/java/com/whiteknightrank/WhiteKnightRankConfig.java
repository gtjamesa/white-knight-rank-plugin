package com.whiteknightrank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("whiteknightrank")
public interface WhiteKnightRankConfig extends Config
{
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
