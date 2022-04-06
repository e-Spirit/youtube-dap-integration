package com.espirit.se.modules.youtube.integration;


import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class YoutubeIntegrationIcons {

	private static final String JC_PATTERN = "/icons/%s.png";
	private static final String WE_PATTERN = "yt/icons/%s.svg";


	private YoutubeIntegrationIcons() {
		throw new IllegalStateException("Utility class");
	}


	private static Icon getIcon(String baseName) {
		return new ImageIcon(YoutubeIntegrationIcons.class.getResource(String.format(JC_PATTERN, baseName)));
	}


	private static Image<?> getImageIcon(BaseContext context, String baseName) {
		try {
			ImageAgent imageAgent = context.requireSpecialist(ImageAgent.TYPE);
			if (context.is(BaseContext.Env.WEBEDIT)) {
				return imageAgent.getImageFromUrl(String.format(WE_PATTERN, baseName));
			} else {
				return imageAgent.getImageFromIcon(getIcon(baseName));
			}
		} catch (Exception e) {
			context.logError(e.getMessage(), e);
		}
		return null;
	}


	public static Image<?> getActive(BaseContext context) {
		return getImageIcon(context, "youtube_active");
	}


	public static Image<?> getInactive(BaseContext context) {
		return getImageIcon(context, "youtube_inactive");
	}


	public static Image<?> getVideo(BaseContext context) {
		return getImageIcon(context, "camera");
	}
}
