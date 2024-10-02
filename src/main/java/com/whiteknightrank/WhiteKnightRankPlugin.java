package com.whiteknightrank;

import com.google.inject.Provides;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
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
	private WhiteKnightOverlay overlay;

	@Inject
	public ConfigManager configManager;

	@Inject
	private QuestLogParser questLogParser;

	@Getter
	private int kc = 0;

	@Getter
	private KnightRank knightRank = KnightRank.NOVICE;

	@Getter
	private long lastKillTime;

	@Getter
	private boolean knightNearby;

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

		log.info("KC from parser: {}", kc);
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
	{
		final NPC npc = npcLootReceived.getNpc();

		if (!KnightNpc.isKnight(npc.getId()))
		{
			return;
		}

		int points = KnightNpc.getPoints(npc.getId());
		kc += points;
		knightRank = KnightRank.valueOfKc(kc);
		lastKillTime = System.currentTimeMillis() / 1000L;
		saveKc();
		log.debug("Killed: {} (ID: {} / P: {} / C: {}) / KC: {} / {}", npc.getName(), npc.getId(), points, npc.getCombatLevel(), kc, knightRank.name());
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();

		if (!KnightNpc.isKnight(npc.getId()))
		{
			return;
		}

		knightNearby = true;
		log.debug("Spawned: {} (ID: {} / C: {})", npc.getName(), npc.getId(), npc.getCombatLevel());
	}

	@Schedule(period = 590, unit = ChronoUnit.SECONDS, asynchronous = true)
	public void knightNearbyTask()
	{
		// Reset the flag every 9m50s, and it should be reactivated if a knight is nearby
		knightNearby = false;
	}

	/**
	 * Check if the overlay should be shown
	 * The conditions are that a knight is nearby or has been killed in the last 10 minutes
	 *
	 * @return boolean
	 */
	public boolean shouldShowOverlay()
	{
		long currentTime = System.currentTimeMillis() / 1000L;
		return knightNearby || lastKillTime >= currentTime - 600;
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
			log.info("Loaded KC: {} / {}", loadedKc, knightRank.name());
		}
	}

	@Provides
	WhiteKnightRankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WhiteKnightRankConfig.class);
	}
}
