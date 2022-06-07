package com.espirit.se.modules.youtube.integration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class YoutubeIntegrationConfig {

	private String _apiKey;
	private List<String> _channelIds;

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
