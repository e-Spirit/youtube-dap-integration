package com.espirit.se.modules.youtube.connector;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;

import com.espirit.se.modules.youtube.YoutubeVideo;
import com.espirit.se.modules.youtube.integration.YoutubeIntegrationConfig;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Youtube base class supports core features, such as searching and retrieving videos.
 */
public class YoutubeConnector {

	private static final String APP_NAME = "FirstSpirit YouTube Integration";
	private static final Class<?> LOGGER = YoutubeConnector.class;
	private final YouTube _youtube;
	private final List<Channel> _channels;
	private final String _apiKey;

	/**
	 * Instantiates a new Youtube connector.
	 *
	 * @param youtube  the youtube
	 * @param channels the channels
	 * @param apiKey   the api key
	 */
	public YoutubeConnector(final YouTube youtube, final List<Channel> channels, final String apiKey) {
		_youtube = youtube;
		_channels = channels;
		_apiKey = apiKey;
	}

	/**
	 * Gets search request.
	 *
	 * @param query   the query
	 * @param channel the channel
	 * @return the search request
	 */
	public YoutubeVideoSearchRequest getSearchRequest(@Nullable String query, @Nullable String channel) {
		YoutubeVideoSearchRequest request = null;
		try {
			if (_channels.isEmpty()) {
				// no channel configured
				request = YoutubeStandardVideoSearchRequest.createInstance(_apiKey, _youtube, query, null);
			} else if (_channels.size() == 1) {
				// one channel configured
				request = YoutubeStandardVideoSearchRequest.createInstance(_apiKey, _youtube, query, _channels.get(0));
			} else if (Strings.isEmpty(channel) || "all".equals(channel)) {
				// more than one channel configured but none or all channels selected
				request = YoutubeMultiChannelVideoSearchRequest.createInstance(_apiKey, _youtube, query, _channels);
			} else {
				// more than one channel configured and one channel selected
				Optional<Channel> queryChannel = getQueryChannel(channel);
				if (queryChannel.isPresent()) {
					// selected channel found in configured channel
					request = YoutubeStandardVideoSearchRequest.createInstance(_apiKey, _youtube, query, queryChannel.get());
				} else {
					// no matching channel found
					// Fallback: search without channel
					request = YoutubeStandardVideoSearchRequest.createInstance(_apiKey, _youtube, query, null);
				}
			}
		} catch (IOException e) {
			Logging.logError("Unable to create Request", e, LOGGER);
		}
		return request;
	}

	/**
	 * Provides a list of videos for the specified IDs.
	 *
	 * @param videoIds list of video ids
	 * @return a list of youtube videos or an empty list
	 */
	public List<YoutubeVideo> getVideo(Collection<String> videoIds) {
		List<YoutubeVideo> videos = new ArrayList<>();
		if (!videoIds.isEmpty()) {
			try {
				VideoListResponse response = _youtube.videos()
						.list("snippet")
						.setKey(_apiKey)
						.setId(Strings.implode(videoIds, ", "))
						.setMaxResults((long) videoIds.size())
						.setFields("items(id,snippet(description,thumbnails/default/url,thumbnails/high/url,title))")
						.execute();
				for (Video video : response.getItems()) {
					VideoSnippet snippet = video.getSnippet();
					ThumbnailDetails thumbnails = snippet.getThumbnails();
					videos.add(new YoutubeVideo(video.getId(), snippet.getTitle(), snippet.getDescription(), thumbnails.getDefault().getUrl(), thumbnails.getHigh().getUrl()));
				}
			} catch (GoogleJsonResponseException e) {
				Logging.logError("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e, LOGGER);
			} catch (IOException e) {
				Logging.logError("IOException when retrieving a Youtube video.", e, LOGGER);
			}
		}
		return videos;
	}

	/**
	 * Gets channels.
	 *
	 * @return the channels
	 */
	public List<Channel> getChannels() {
		return _channels;
	}

	private Optional<Channel> getQueryChannel(final String channelId) {
		for (final Channel channel : _channels) {
			if (channel.getId().equals(channelId)) {
				return Optional.of(channel);
			}
		}
		return Optional.empty();
	}

	/**
	 * Builder to create a new YouTubeConnector instance.
	 */
	public static class Builder {

		private String _apiKey;
		private List<String> _channelIds;

		/**
		 * Set the apikey and channelids based on the YoutubeIntegrationConfig
		 *
		 * @param youtubeIntegrationConfig the youtube integration config
		 * @return Builder builder
		 */
		public Builder config(YoutubeIntegrationConfig youtubeIntegrationConfig) {
			_apiKey = youtubeIntegrationConfig.getApiKey();
			_channelIds = youtubeIntegrationConfig.getChannelIds();
			return this;
		}

		/**
		 * Apikey builder.
		 *
		 * @param apiKey the api key
		 * @return the builder
		 */
		public Builder apikey(String apiKey) {
			_apiKey = apiKey;
			return this;
		}

		/**
		 * Channels builder.
		 *
		 * @param channelIds the channel ids
		 * @return the builder
		 */
		public Builder channels(List<String> channelIds) {
			_channelIds = channelIds;
			return this;
		}

		/**
		 * Build youtube connector.
		 *
		 * @return the youtube connector
		 */
		public YoutubeConnector build() {
			if (Strings.notEmpty(_apiKey)) {
				YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
				}).setApplicationName(APP_NAME).build();
				List<Channel> youtubeChannels = new ArrayList<>();
				if (_channelIds != null && !_channelIds.isEmpty()) {
					try {
						youtubeChannels = getYoutubeChannels(youtube);
					} catch (IOException e) {
						Logging.logError("Youtube channel retrieval error", e, LOGGER);
					}
				}
				return new YoutubeConnector(youtube, youtubeChannels, _apiKey);
			}
			return null;
		}

		/**
		 * Helper method to verify the specified api key and channel ids.
		 *
		 * @throws IOException the io exception
		 */
		public void checkSettings() throws IOException {
			if (Strings.isEmpty(_apiKey)) {
				throw new IllegalArgumentException("YoutTube API KEY is missing");
			}
			YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
			}).setApplicationName(APP_NAME).build();
			youtube.i18nLanguages()
					.list("snippet")
					.setKey(_apiKey)
					.setFields("etag")
					.execute();
			if (_channelIds == null || _channelIds.isEmpty()) {
				Logging.logInfo("No channels configured", LOGGER);
			} else {
				getYoutubeChannels(youtube);
			}
		}

		/**
		 * Gets YouTube channels.
		 *
		 * @param youtube the youtube
		 * @return the youtube channels
		 * @throws IOException the io exception
		 */
		List<Channel> getYoutubeChannels(@NotNull final YouTube youtube) throws IOException {
			List<Channel> result = new ArrayList<>();
			for (final String channelId : _channelIds) {
				ChannelListResponse channels = youtube.channels()
						.list("snippet")
						.setKey(_apiKey)
						.setFields("items(id,snippet(title,description))")
						.setId(channelId)
						.execute();
				List<Channel> responseChannelList = channels.getItems();
				if (responseChannelList.size() == 1) {
					Channel channel = responseChannelList.get(0);
					if (!channelId.equals(channel.getId())) {
						throw new IllegalArgumentException("Incomplete ChannelId, try: " + channel.getId());
					} else {
						result.add(channel);
					}
				} else {
					throw new IllegalArgumentException("Unknown ChannelId");
				}
			}
			return result;
		}

	}
}
