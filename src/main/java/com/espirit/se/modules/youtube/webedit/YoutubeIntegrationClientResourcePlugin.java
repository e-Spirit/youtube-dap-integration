package com.espirit.se.modules.youtube.webedit;


import com.espirit.moddev.components.annotations.PublicComponent;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.webedit.plugin.ClientResourcePlugin;

import java.util.Collections;
import java.util.List;

@PublicComponent(name="YoutubeClientResourcePlugin")
public class YoutubeIntegrationClientResourcePlugin implements ClientResourcePlugin {

	@Override
	public void setUp(BaseContext context) {
	}

	@Override
	public void tearDown() {
	}

	@Override
	public List<String> getScriptUrls() {
		return Collections.singletonList("yt/youtube.js");
	}

	@Override
	public List<String> getStylesheetUrls() {
		return Collections.singletonList("yt/youtube.css");
	}
}
