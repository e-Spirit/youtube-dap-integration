package com.espirit.se.modules.youtube.connector;

import com.espirit.se.modules.youtube.YoutubeVideo;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;

import java.io.IOException;
import java.util.List;

/**
 * Request to retrieve videos from youtube.
 */
public interface YoutubeVideoSearchRequest {

	/**
	 * Create youtube video object.
	 *
	 * @param searchResult the search result
	 * @return the youtube video
	 */
	static YoutubeVideo createYoutubeVideo(final SearchResult searchResult) {
		SearchResultSnippet searchResultSnippet = searchResult.getSnippet();
		ThumbnailDetails searchResultThumbnails = searchResultSnippet.getThumbnails();
		return new YoutubeVideo(searchResult.getId().getVideoId(),
								searchResultSnippet.getTitle(),
								searchResultSnippet.getDescription(),
								searchResultThumbnails.getDefault().getUrl(),
								searchResultThumbnails.getHigh().getUrl());
	}

	/**
	 * Gets total.
	 *
	 * @return the total
	 */
	int getTotal();

	/**
	 * Search videos.
	 *
	 * @param count the count
	 * @return the videos
	 * @throws IOException the io exception
	 */
	List<YoutubeVideo> searchVideos(final int count);

	/**
	 * Has next.
	 *
	 * @return the boolean
	 */
	boolean hasNext();
}
