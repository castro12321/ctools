package castro.ctools.modules.groups;

public enum GroupType
{
	HIDDEN  (0),
	REGULAR (1),
	TEAM    (2),
	STAFF   (3);
	
	public final int priority;
	
	private GroupType(int priority)
	{
		this.priority = priority;
	}
	
	
	public static GroupType get(int priority)
	{
		switch(priority)
		{
		case 0: return HIDDEN;
		case 1: return REGULAR;
		case 2: return TEAM;
		case 3: return STAFF;
		}
		return null;
	}
}