package com.espirit.ps.custom.youtube;


import com.espirit.ps.custom.youtube.integration.YoutubeIntegrationProjectApp;
import com.espirit.ps.custom.youtube.integration.YoutubeIntegrationProjectConfig;
import com.espirit.ps.psci.genericconfiguration.Values;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;


public class YoutubeVideos {
	private static final String APP_NAME = "FirstSpirit YouTube Integration";
	private final YouTube _youtube;
	private final String _apiKey;
	private final List<Channel> _channels = new ArrayList<Channel>();

	private static final Class<?> LOGGER = YoutubeVideos.class;

	private static int apiCounter = 0;

	private YoutubeVideos(String apiKey, @Nullable String channelIds) throws Exception {

		apiCounter = 0;
		_apiKey = apiKey;
		//_channelIds = channelIds.split(",");
		_youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {}).setApplicationName(APP_NAME).build();

		if (channelIds != null && !channelIds.isEmpty()) {
			try {
				Iterator<String> channelIterator = Arrays.stream(channelIds.split(",")).iterator();
				while (channelIterator.hasNext()) {
					String channelId = channelIterator.next();
					ChannelListResponse channels = _youtube.channels()
							.list("snippet")
							.setKey(apiKey)
							//.setFields("items/id")
							.setFields("items(id,snippet(title,description))")
							.setId(channelId)
							.execute();
					apiCounter++;
					Logging.logInfo("API Counter: " + apiCounter, LOGGER);


					List<Channel> channelList = channels.getItems();
					int channelListSize = channelList.size();

					if (channelListSize == 1) {
						Channel channel = channelList.get(0);
						_channels.add(channel);
					}
				}

			} catch (GoogleJsonResponseException e) {
				throw new Exception("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e);
			}
		}
	}

	@Nullable
	public static YoutubeVideos createInstance(SpecialistsBroker broker) {

		if (YoutubeIntegrationProjectConfig.isInstalled(YoutubeIntegrationProjectApp.class, broker)) {

			Values values = YoutubeIntegrationProjectConfig.values(broker, YoutubeIntegrationProjectApp.class);
			String apiKey = values.getString(YoutubeIntegrationProjectConfig.CONFIG_API_KEY);
			if (apiKey != null && !apiKey.isEmpty()) {

				String channelIds = values.getString(YoutubeIntegrationProjectConfig.CONFIG_CHANNEL_IDS);
				try {
					Logging.logInfo("Creating new instance of Youtube videos.", LOGGER);
					return new YoutubeVideos(apiKey, channelIds == null || channelIds.isEmpty() ? null : channelIds);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString();
					Logging.logError("Error while creating instance of Youtube Connector: " + sStackTrace, LOGGER);
				}
			}
		}
		return null;
	}


	public static void checkSettings(String apiKey, @Nullable String channelIds) throws Exception {
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {}).setApplicationName(APP_NAME).build();
		try {
			youtube.i18nLanguages()
					.list("snippet")
					.setKey(apiKey)
					.setFields("etag")
					.execute();
			apiCounter++;
			Logging.logInfo("API Counter: " + apiCounter, LOGGER);
		} catch (GoogleJsonResponseException e) {
			throw new Exception("Invalid API Key!", e);
		}

		if (channelIds != null && !channelIds.isEmpty()) {
			try {
				Iterator<String> channelIterator = Arrays.stream(channelIds.split(",")).iterator();
				while (channelIterator.hasNext()) {
					String channelId = channelIterator.next();
					ChannelListResponse channels = youtube.channels()
							.list("id")
							.setKey(apiKey)
							.setFields("items/id")
							//.setFields("items(id,snippet(title,description))")
							.setId(channelId)
							.execute();
					apiCounter++;
					Logging.logInfo("API Counter: " + apiCounter, LOGGER);
					if (channels.getItems().size() == 1) {
						Channel channel = channels.getItems().get(0);
						if (!channelId.equals(channel.getId())) {
							throw new Exception("Incomplete ChannelId, try: " + channel.getId());
						}
					} else {
						throw new Exception("Unknown ChannelId");
					}
				}
			} catch (GoogleJsonResponseException e) {
				throw new Exception("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e);
			}
		}
	}


	public Iterator<YoutubeVideo> find(@Nullable String query, @Nullable String channel) {
		//if (query != null || channel != null) {
			try {

				if (query == null) query = "";

				Iterator<Channel> channelIterator = null;
				if (channel == null || channel.isEmpty() || channel.equals("all")) {
					if (!_channels.isEmpty()) {
						channelIterator = _channels.iterator();
					}
				}

				List<YoutubeVideo> results = new ArrayList<YoutubeVideo>();

				if (channelIterator == null) {
					YouTube.Search.List ysl;
					if (channel == null || channel.isEmpty()) {
						ysl = _youtube.search()
							.list("snippet")
							.setKey(_apiKey)
							.setMaxResults(25l)
							.setQ(query)
							.setType("video")
							.setFields("items(id/videoId,snippet(description,thumbnails/default/url,thumbnails/high/url,title)),nextPageToken");
					} else {
						ysl = _youtube.search()
							.list("snippet")
							.setKey(_apiKey)
							.setMaxResults(25l)
							.setChannelId(channel)
							.setQ(query)
							.setType("video")
							.setFields("items(id/videoId,snippet(description,thumbnails/default/url,thumbnails/high/url,title)),nextPageToken");
					}
					Iterator<YoutubeVideo> it = new SearchResults(ysl);
					while (it.hasNext()) {
						results.add(it.next());
					}
				} else {
					while (channelIterator.hasNext()) {
						String channelId = channelIterator.next().getId();
						YouTube.Search.List ysl = _youtube.search()
								.list("snippet")
								.setKey(_apiKey)
								.setMaxResults(25l)
								.setChannelId(channelId)
								.setQ(query)
								.setType("video")
								.setFields("items(id/videoId,snippet(description,thumbnails/default/url,thumbnails/high/url,title)),nextPageToken");
						Iterator<YoutubeVideo> it = new SearchResults(ysl);
						while (it.hasNext()) {
							results.add(it.next());
						}
					}
				}

				return results.iterator();

			} catch (GoogleJsonResponseException e) {
				Logging.logError("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e, getClass());
			} catch (Exception e) {
				Logging.logError(e.getMessage(), e, getClass());
			}
		//}
		return Collections.emptyIterator();
	}

	public List<Channel> getChannels() {

		return _channels;

	}

	/*public static Iterator<YoutubeVideo> find(SpecialistsBroker broker, @Nullable String query, @Nullable String channel) {
		YoutubeVideos videos = createInstance(broker);
		return videos != null ? videos.find(query, channel) : Collections.emptyIterator();
	}*/


	private List<YoutubeVideo> get(Collection<String> videoIds) {
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
				apiCounter++;
				Logging.logInfo("API Counter: " + apiCounter, LOGGER);
				for (com.google.api.services.youtube.model.Video video : response.getItems()) {
					VideoSnippet snippet = video.getSnippet();
					ThumbnailDetails thumbnails = snippet.getThumbnails();
					videos.add(new YoutubeVideo(video.getId(), snippet.getTitle(), snippet.getDescription(), thumbnails.getDefault().getUrl(), thumbnails.getHigh().getUrl()));
				}
			} catch (GoogleJsonResponseException e) {
				Logging.logError("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e, getClass());
			} catch (Exception e) {
				Logging.logError(e.getMessage(), e, getClass());
			}
		}
		return videos;
	}

	public static List<YoutubeVideo> get(SpecialistsBroker broker, Collection<String> videoIds) {
		YoutubeVideos videos = createInstance(broker);
		return videos != null ? videos.get(videoIds) : Collections.emptyList();
	}


	@Nullable
	private YoutubeVideo get(String videoId) {
		List<YoutubeVideo> videos = get(Collections.singletonList(videoId));
		return videos.isEmpty() ? null : videos.get(0);
	}

	@Nullable
	public static YoutubeVideo get(SpecialistsBroker broker, String videoId) {
		YoutubeVideos videos = createInstance(broker);
		return videos != null ? videos.get(videoId) : null;
	}


	private static class SearchResults implements Iterator<YoutubeVideo> {
		private final YouTube.Search.List _ysl;
		private Iterator<SearchResult> _items;
		private boolean _hasNextPage = true;
		private YoutubeVideo _nextVideo;

		private SearchResults(YouTube.Search.List ysl) throws IOException {
			_ysl = ysl;
			nextPage();
			nextVideo();
		}

		private void nextPage() throws IOException {
			SearchListResponse response = _ysl.execute();
			apiCounter++;
			Logging.logInfo("API Counter: " + apiCounter, LOGGER);
			_items = response.getItems().iterator();

			String token = response.getNextPageToken();
			if (token != null) {
				_ysl.setPageToken(response.getNextPageToken());
			} else {
				_hasNextPage = false;
			}
		}

		private YoutubeVideo nextVideo() throws IOException {
			YoutubeVideo result = _nextVideo;
			_nextVideo = null;

			if (!_items.hasNext()) {
				if (_hasNextPage) {
					nextPage();
					if (!_items.hasNext()) {
						return result;
					}
				} else {
					return result;
				}
			}

			SearchResult video = _items.next();
			SearchResultSnippet snippet = video.getSnippet();
			ThumbnailDetails thumbnails = snippet.getThumbnails();
			_nextVideo = new YoutubeVideo(video.getId().getVideoId(), snippet.getTitle(), snippet.getDescription(), thumbnails.getDefault().getUrl(), thumbnails.getHigh().getUrl());

			return result;
		}

		@Override
		public boolean hasNext() {
			return _nextVideo != null;
		}

		@Override
		public YoutubeVideo next() {
			try {
				return nextVideo();
			} catch (Exception e) {
				Logging.logError(e.getMessage(), e, getClass());
				return null;
			}
		}
	}
}
