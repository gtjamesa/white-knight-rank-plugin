package com.whiteknightrank;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "White Knight Rank", description = "Track your White Knight rank and Black Knight kills.", tags = {"white", "knight", "black", "whiteknight", "blackknight"})
public class WhiteKnightRankPlugin extends Plugin
{
	public static final int KC_LOG_COMPONENT_ID = 7798814;
	public static final String KC_LOG_TEXT_MATCH = "White Knight with a kill score of <col=\\d+>(\\d+)<col=\\d+>";

	@Inject
	private Client client;

	@Inject
	private WhiteKnightRankConfig config;

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

	@Provides
	WhiteKnightRankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WhiteKnightRankConfig.class);
	}
}
