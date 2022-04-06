package com.espirit.ps.custom.youtube;


public class YoutubeVideo {
	private final String _id;
	private final String _title;
	private final String _description;
	private final String _thumbnailUrl;
	private final String _posterUrl;

	YoutubeVideo(String id, String title, String description, String thumbnailUrl, String posterUrl) {
		_id = id;
		_title = title;
		_description = description;
		_thumbnailUrl = thumbnailUrl;
		_posterUrl = posterUrl;
	}

	public String getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public String getDescription() {
		return _description;
	}

	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	public String getPosterUrl() {
		return _posterUrl;
	}
}
