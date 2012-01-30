package com.coversal.plugin.wol;

import com.coversal.ucl.plugin.PluginAnnouncer;

public class WolAnnouncer extends PluginAnnouncer {

	public WolAnnouncer() {
		defineProfile(Wol.PROFILE_NAME, Wol.class);
	}

}
