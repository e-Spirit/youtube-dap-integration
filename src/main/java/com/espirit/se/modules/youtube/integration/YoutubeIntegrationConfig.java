package com.espirit.se.modules.youtube.integration;

import java.util.List;

/**
 * Class to outsource configurations
 */
public class YoutubeIntegrationConfig {

	private final String _apiKey;
	private final List<String> _channelIds;

	/**
	 * Initialize Configuration
	 *
	 * @param apiKey
	 * @param channelIds
	 */
	public YoutubeIntegrationConfig(final String apiKey, final List<String> channelIds) {
		_apiKey = apiKey;
		_channelIds = channelIds;
	}

	/**
	 *
	 * @return Google API key
	 */
	public String getApiKey() {
		return _apiKey;
	}

	/**
	 *
	 * @return list of YouTube channel ids
	 */
	public List<String> getChannelIds() {
		return _channelIds;
	}
}
