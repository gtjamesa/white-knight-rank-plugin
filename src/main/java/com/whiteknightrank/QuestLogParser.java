package com.whiteknightrank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;

@Slf4j
public class QuestLogParser
{
	public static final int KC_LOG_COMPONENT_ID = 7798814;
	public static final int KC_MASTER_LOG_COMPONENT_ID = 7798813;
	public static final String KC_LOG_TEXT_MATCH = "White Knight with a kill score of <col=\\d+>([\\d,]+)<col=\\d+>";
	public static final String QUEST_NAME = "Wanted!";

	@Inject
	private Client client;

	/**
	 * Parse the number of Black Knight kills from the widget text
	 *
	 * @return int
	 */
	public int getKc()
	{
		Widget widget = findLogWidget();

		if (widget == null || widget.getText() == null)
		{
			return 0;
		}

		// <col=000080>I am currently a <col=800000>NOVICE<col=000080> White Knight with a kill score of <col=800000>152<col=000080>.
		Pattern pattern = Pattern.compile(KC_LOG_TEXT_MATCH);
		Matcher matcher = pattern.matcher(widget.getText());
		boolean found = matcher.find();

		if (found)
		{
			String kc = matcher.group(1).replaceAll(",", "");
			return Integer.parseInt(kc);
		}

		log.debug("Failed to parse KC from widget text: {}", widget.getText());

		return 0;
	}

	public boolean isWantedQuest()
	{
		Widget widgetTitle = client.getWidget(ComponentID.DIARY_TITLE);

		return widgetTitle != null && widgetTitle.getText() != null && widgetTitle.getText().contains(QUEST_NAME);
	}

	private Widget findLogWidget()
	{
		int[] widgets = {KC_LOG_COMPONENT_ID, KC_MASTER_LOG_COMPONENT_ID};

		for (int widgetId : widgets)
		{
			Widget widget = client.getWidget(widgetId);

			if (widget != null && widget.getText() != null && !widget.getText().isEmpty())
			{
				return widget;
			}
		}

		return null;
	}
}
