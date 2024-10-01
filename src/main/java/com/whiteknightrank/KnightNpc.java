package com.whiteknightrank;

import java.util.HashMap;
import java.util.Map;

public class KnightNpc
{
	// https://chisel.weirdgloop.org/moid/npc_name.html#/elite%20?black%20knight|elite%20dark/
	private static final Map<Integer, Integer> BLACK_KNIGHTS = new HashMap<>(
		Map.ofEntries(
			// Black Knight
			Map.entry(516, 1),
			Map.entry(517, 1),
			Map.entry(1545, 1),
			Map.entry(4331, 1),
			Map.entry(4959, 1),
			Map.entry(11952, 1),
			Map.entry(11953, 1),
			// Elite Dark Warrior
			Map.entry(13465, 6),
			Map.entry(13466, 6),
			// Elite Dark Ranger
			Map.entry(13467, 6),
			Map.entry(13468, 6),
			// Elite Dark Mage
			Map.entry(13469, 6),
			Map.entry(13470, 6),
			// Elite Black Knight
			Map.entry(13463, 12),
			Map.entry(13464, 12)
		)
	);

	private static final Map<Integer, Integer> WHITE_KNIGHTS = new HashMap<>(
		Map.ofEntries(
			Map.entry(1798, 1),
			Map.entry(1799, 1),
			Map.entry(1800, 1),
			Map.entry(1829, 1),
			Map.entry(4114, 1),
			Map.entry(11948, 1),
			Map.entry(11949, 1),
			Map.entry(11950, 1),
			Map.entry(11951, 1)
		)
	);

	/**
	 * Find the points acquired for killing a Black (or White!) Knight
	 *
	 * @param npcId
	 * @return int
	 */
	public static int getPoints(int npcId)
	{
		// Killing a white knight will decrease your rank
		if (WHITE_KNIGHTS.containsKey(npcId))
		{
			return -1;
		}

		return BLACK_KNIGHTS.getOrDefault(npcId, 0);
	}

	public static boolean isKnight(int npcId)
	{
		return BLACK_KNIGHTS.containsKey(npcId) || WHITE_KNIGHTS.containsKey(npcId);
	}
}
