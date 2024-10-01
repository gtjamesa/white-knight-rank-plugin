package com.whiteknightrank;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WhiteKnightRankPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WhiteKnightRankPlugin.class);
		RuneLite.main(args);
	}
}