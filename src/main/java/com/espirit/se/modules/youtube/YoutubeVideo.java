package com.espirit.se.modules.youtube;

/**
 * Youtube Video Data Object
 * This data object carries information about a single Youtube video and is used by the YoutubeVideoDataAccessPlugin.
 *
 * @see com.espirit.se.modules.youtube.dataaccess.YoutubeVideoDataAccessPlugin
 * @see com.espirit.se.modules.youtube.dataaccess.YoutubeVideoDataAccessSession
 * @see com.espirit.se.modules.youtube.dataaccess.YoutubeVideoDataStream
 */
public class YoutubeVideo {

	private final String _id;
	private final String _title;
	private final String _description;
	private final String _thumbnailUrl;
	private final String _posterUrl;

	/**
	 * Instantiates a new Youtube video data object.
	 *
	 * @param id           the ID that YouTube uses to uniquely identify the video
	 * @param title        the video's title
	 * @param description  the video's description
	 * @param thumbnailUrl the thumbnail url for this video
	 * @param posterUrl    the high quality image for this video
	 */
	YoutubeVideo(String id, String title, String description, String thumbnailUrl, String posterUrl) {
		_id = id;
		_title = title;
		_description = description;
		_thumbnailUrl = thumbnailUrl;
		_posterUrl = posterUrl;
	}

	/**
	 * Gets ID that YouTube uses to uniquely identify the video.
	 *
	 * @return the id
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Gets video's title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Gets video's description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return _description;
	}

	/**
	 * Gets thumbnail url for this video.
	 *
	 * @return the thumbnail url
	 */
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	/**
	 * Gets high quality image for this video.
	 *
	 * @return the poster url
	 */
	public String getPosterUrl() {
		return _posterUrl;
	}
}
