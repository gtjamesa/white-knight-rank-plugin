package com.whiteknightrank;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(name = "White Knight Rank", description = "Track your White Knight rank and Black Knight kills.", tags = {"white", "knight", "black", "whiteknight", "blackknight"})
public class WhiteKnightRankPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "whiteknightrank";
	public static final String CONFIG_KC_KEY = "kc";

	@Inject
	private Client client;

	@Inject
	private WhiteKnightRankConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	@Inject
	public ConfigManager configManager;

	@Inject
	private WhiteKnightOverlay overlay;

	@Inject
	private QuestLogParser questLogParser;

	@Getter
	private int kc = 0;

	@Getter
	private KnightRank knightRank = KnightRank.NOVICE;

	@Getter
	private long lastKillTime;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadKc();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != WidgetID.DIARY_QUEST_GROUP_ID || !questLogParser.isWantedQuest())
		{
			return;
		}

		// Opening the "Wanted!" quest log will fetch the kc and persist to disk
		kc = questLogParser.getKc();
		knightRank = KnightRank.valueOfKc(kc);
		saveKc();

		log.debug("KC from quest log: {}", kc);
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
	{
		final NPC npc = npcLootReceived.getNpc();

		if (KnightNpc.isKnight(npc.getId()))
		{
			trackKill(npc);
		}
	}

	private void trackKill(NPC npc)
	{
		int points = KnightNpc.getPoints(npc.getId());
		KnightRank oldRank = knightRank;

		kc += points;
		knightRank = KnightRank.valueOfKc(kc);
		lastKillTime = System.currentTimeMillis() / 1000L;

		if (config.showNotificationOnRank() && oldRank != knightRank)
		{
			notifier.notify("Congratulations! You are now a White Knight " + knightRank.prettyName() + "!");
		}

		saveKc();
		log.debug("Killed: {} (ID: {} / P: {}) / KC: {} / {}", npc.getName(), npc.getId(), points, kc, knightRank.prettyName());
	}

	/**
	 * Check if the overlay should be shown
	 * The conditions are that a knight is nearby or has been killed in the last 10 minutes
	 *
	 * @return boolean
	 */
	public boolean recentlyKilledKnight()
	{
		long currentTime = System.currentTimeMillis() / 1000L;
		return lastKillTime >= currentTime - 600;
	}

	private void saveKc()
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KC_KEY, kc);
	}

	private void loadKc()
	{
		// if we already have KC loaded, don't overwrite it
		if (kc > 0)
		{
			return;
		}

		String loadedKc = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KC_KEY);

		if (loadedKc != null)
		{
			kc = Integer.parseInt(loadedKc);
			knightRank = KnightRank.valueOfKc(kc);
			assert knightRank != null;
			log.debug("Loaded KC: {} / {}", loadedKc, knightRank.prettyName());
		}
	}

	@Provides
	WhiteKnightRankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WhiteKnightRankConfig.class);
	}
}
