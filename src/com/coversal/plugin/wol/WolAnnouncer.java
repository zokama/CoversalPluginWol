package com.coversal.plugin.wol;

import com.coversal.ucl.plugin.ProfileAnnouncer;

public class WolAnnouncer extends ProfileAnnouncer {

	public WolAnnouncer() {
		defineProfile(Wol.PROFILE_NAME, Wol.class);
	}

}
