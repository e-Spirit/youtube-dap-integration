package com.espirit.se.modules.youtube.integration;

import java.util.List;

public class YoutubeIntegrationConfig {

	private final String _apiKey;
	private final List<String> _channelIds;

	public YoutubeIntegrationConfig(final String apiKey, final List<String> channelIds) {
		_apiKey = apiKey;
		_channelIds = channelIds;
	}

	public String getApiKey() {
		return _apiKey;
	}

	public List<String> getChannelIds() {
		return _channelIds;
	}

	public boolean hasChannels() {
		return _channelIds != null && !_channelIds.isEmpty();
	}
}
