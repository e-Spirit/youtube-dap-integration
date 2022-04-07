package com.espirit.se.modules.youtube.webedit;


import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.webedit.plugin.ClientResourcePlugin;

import com.espirit.moddev.components.annotations.PublicComponent;

import java.util.Collections;
import java.util.List;


/**
 * The FirstSpirit Youtube integration client resource plugin.
 * Plug-in providing resource urls to be loaded in webedit. In this case, the styling and logic for the Youtube player.
 */
// TODO: Naming, find better description
@PublicComponent(name = "YoutubeClientResourcePlugin",
		displayName = "Youtube Client Resource Plugin",
		description = "Provides the required css and javascript files.")
public class YoutubeIntegrationClientResourcePlugin implements ClientResourcePlugin {

	@Override
	public void setUp(BaseContext context) {
		// Nothing needs to be done here
	}


	@Override
	public void tearDown() {
		// Nothing needs to be done here
	}


	/**
	 * Provides the absolute javascript url to be loaded in the ContentCreator.
	 * See /src/main/fsm-resources/web/youtube.js
	 *
	 * @return
	 */
	@Override
	public List<String> getScriptUrls() {
		return Collections.singletonList("yt/youtube.js");
	}

	/**
	 * Provides the stylesheet/css url url to be loaded in the ContentCreator.
	 * See /src/main/fsm-resources/web/youtube.css
	 *
	 * @return
	 */
	@Override
	public List<String> getStylesheetUrls() {
		return Collections.singletonList("yt/youtube.css");
	}
}
