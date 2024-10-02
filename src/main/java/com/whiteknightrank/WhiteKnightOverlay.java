package com.whiteknightrank;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class WhiteKnightOverlay extends OverlayPanel
{
	private final WhiteKnightRankPlugin plugin;
	private final WhiteKnightRankConfig config;

	@Inject
	private WhiteKnightOverlay(WhiteKnightRankPlugin plugin, WhiteKnightRankConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		this.plugin = plugin;
		this.config = config;
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "White Knight overlay");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlay() || !plugin.shouldShowOverlay())
		{
			return null;
		}

		String rank = plugin.getKnightRank().name();

		final String kcString = String.format("KC: %d", plugin.getKc());
		final String rankString = String.format("Rank: %s", rank.substring(0, 1).toUpperCase() + rank.substring(1).toLowerCase());
		final int maxWidth = Math.max(graphics.getFontMetrics().stringWidth(kcString), graphics.getFontMetrics().stringWidth(rankString));

		panelComponent.getChildren().add(TitleComponent.builder().text(kcString).color(Color.WHITE).build());
		panelComponent.getChildren().add(TitleComponent.builder().text(rankString).color(Color.WHITE).build());

//		panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));

		return super.render(graphics);
	}
}
