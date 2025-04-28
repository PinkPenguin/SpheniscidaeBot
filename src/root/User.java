package root;

public class User {

	private String userName = "ERROR!";
//	private String badgeInfo;
	private String badges;
//	private String clientNonce;
//	private String color;
	private String displayName;
//	private String emoteSets;
//	private String flags;
//	private String id;
	private String mod;
//	private String roomID;
//	private String subscriber;
//	private String tmi_sent_ts;
//	private String turbo;
//	private String userID;
//	private String userType;

	private boolean isBroadcaster = false;
	private boolean isMod = false;
	private boolean isSubscriber = false;

//	public User(String name) {
//		this.userName = name;
//	}

	// TODO: just a temporary constructor until I figure out what to do with the
	// advanced user tags
	public User(String userTag) {
		String[] splitTag = userTag.substring(1).split(";");

		for (String s : splitTag) {
			String[] spl = s.split("=");
			switch (spl[0]) {
			case "badges":
				if (s.contains("broadcaster")) {
					isBroadcaster = true;
					isMod = true;
					isSubscriber = true;
				} else if (s.contains("subscriber")) {
					isSubscriber = true;
				}
				break;

			case "display-name":
				displayName = spl[1];
				break;

			case "subscriber":
				isSubscriber = true;
				break;

			case "mod":
				if (spl[1].equals("1")) {
					isMod = true;
				}
				break;
			}
		}
//		/*Contains sub length information. TODO: for when i need it if ever*/
////		badgeInfo = splitTag[0];
//		badges = splitTag[1];
//		if (badges.contains("broadcaster")) {
//			isBroadcaster = true;
//			isMod = true;
////			isSubscriber = true;
//		}
//		else if (badges.contains("subscriber")) {
//			isSubscriber = true;
//		}
////		clientNonce = splitTag[2];
////		color = splitTag[3];
//		displayName = splitTag[4].split("=")[1];
////		emoteSets = splitTag[5];
////		flags = splitTag[6];
////		id = splitTag[7];
//		mod = splitTag[8];
//		if(mod.endsWith("1")) {
//			isMod = true;
//		}
////		roomID = splitTag[9];
////		subscriber = splitTag[10];
////		tmi_sent_ts = splitTag[11];
////		turbo = splitTag[12];
////		userID = splitTag[13];
////		userType = splitTag[14];
	}

	public String getName() {
		return displayName;
	}

	public boolean isBroadcaster() {
		return isBroadcaster;
	}

	public boolean isSubscriber() {
		return isSubscriber;
	}

	public boolean isMod() {
		return isMod;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof User))
			return false;

		User u = (User) o;

		return userName.equals(u.userName);
	}
}
