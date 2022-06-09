package com.espirit.se.modules.youtube.connector;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class YoutubeConnectorRequestTest {

	public static final String APIKEY = "apikey";
	public static final String CHANNEL_1 = "Channel 1";

	static YouTube _youTubeMock;

	@BeforeAll
	static void beforeAll() throws IOException {
		_youTubeMock = mock(YouTube.class);
		YouTube.Search searchMock = mock(YouTube.Search.class);
		when(_youTubeMock.search()).thenReturn(searchMock);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchMock.list(any())).thenReturn(searchListMock);

		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyString())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
	}

	@Test
	public void getSearchRequest_NO_CHANNEL() {
		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.emptyList(), APIKEY);

		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_ONE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.singletonList(channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_SEARCH_SINGLE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.singletonList(channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_SEARCH_IN_ONE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_SEARCH_IN_UNKNOWN_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn("Unknown channel");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_SEARCH_IN_ALL_CHANNELS() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "all");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeMultiChannelVideoSearchRequest);
	}

	@Test
	public void getSearchRequest_SEARCH_ALL_CHANNELS() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeMultiChannelVideoSearchRequest);
	}

}