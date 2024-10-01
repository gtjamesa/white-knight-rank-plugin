package com.whiteknightrank;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "White Knight Rank", description = "Track your White Knight rank and Black Knight kills.", tags = {"white", "knight", "black", "whiteknight", "blackknight"})
public class WhiteKnightRankPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private WhiteKnightRankConfig config;

	@Inject
	private QuestLogParser questLogParser;

	private int kc = 0;
	private KnightRank knightRank = KnightRank.NOVICE;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != WidgetID.DIARY_QUEST_GROUP_ID || !questLogParser.isWantedQuest())
		{
			return;
		}

		kc = questLogParser.getKc();
		knightRank = KnightRank.valueOfKc(kc);

		log.info("KC from parser: {}", kc);
	}

	@Provides
	WhiteKnightRankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WhiteKnightRankConfig.class);
	}
}
