package com.whiteknightrank;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum KnightRank
{
	MASTER(1300),
	ADEPT(800),
	NOBLE(500),
	PAGE(300),
	PEON(200),
	NOVICE(100),
	NO_RANK(0);

	private final int kc;

	private static final Map<Integer, KnightRank> BY_KC = new HashMap<>();

	static
	{
		for (KnightRank e : values())
		{
			BY_KC.put(e.kc, e);
		}
	}

	KnightRank(int kc)
	{
		this.kc = kc;
	}

	public String prettyName()
	{
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}

	public static KnightRank valueOfKc(int kc)
	{
		for (KnightRank e : values())
		{
			if (kc >= e.kc)
			{
				return e;
			}
		}

		return null;
	}
}
