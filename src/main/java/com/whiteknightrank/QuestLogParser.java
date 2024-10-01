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
	public static final String KC_LOG_TEXT_MATCH = "White Knight with a kill score of <col=\\d+>(\\d+)<col=\\d+>";
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
		Widget widget = client.getWidget(KC_LOG_COMPONENT_ID);

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
			String kc = matcher.group(1);
			return Integer.parseInt(kc);
		}

		return 0;
	}

	public boolean isWantedQuest()
	{
		Widget widgetTitle = client.getWidget(ComponentID.DIARY_TITLE);

		return widgetTitle != null && widgetTitle.getText() != null && widgetTitle.getText().contains(QUEST_NAME);
	}
}
