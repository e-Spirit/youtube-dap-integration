package com.espirit.se.modules.youtube.dataaccess;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.ParameterMap;
import de.espirit.firstspirit.client.plugin.report.ParameterSelect;
import de.espirit.firstspirit.client.plugin.report.ParameterText;

import com.espirit.se.modules.youtube.YoutubeVideo;
import com.espirit.se.modules.youtube.YoutubeVideos;
import com.google.api.services.youtube.model.Channel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The FirstSpirit Youtube video data stream.
 */
public class YoutubeVideoDataStream implements DataStream<YoutubeVideo> {

	private final YoutubeVideos _youtubeVideos;
	private int _countAll;
	private int _total;
	private Iterator<YoutubeVideo> _data = null;
	private Iterator<Channel> _channels = null;

	private YoutubeVideoDataStream(BaseContext context, YoutubeVideos youtubeVideos, @Nullable String query, @Nullable String channel) {
		_countAll = 0;
		_total = -1;
		if (youtubeVideos == null) {
			_youtubeVideos = YoutubeVideos.createInstance(context);
		} else {
			_youtubeVideos = youtubeVideos;
		}
		_data = _youtubeVideos.find(query, channel);
		_channels = _youtubeVideos.getChannels().iterator();
	}

	@Override
	public void close() {
		_data = null;
	}

	@Override
	public List<YoutubeVideo> getNext(int count) {
		List<YoutubeVideo> videoList = new ArrayList<>();

		int i = 0;
		if (_data != null) {
			while (_data.hasNext() && i < count) {
				YoutubeVideo video = _data.next();
				if (video != null && !videoList.contains(video)) {
					videoList.add(video);
					i++;
				}
			}
		}

		_countAll += i;

		if (i != count) {
			_total = _countAll;
		}

		return videoList;
	}

	@Override
	public int getTotal() {
		return _total;
	}

	@Override
	public boolean hasNext() {
		return _data != null && _data.hasNext();
	}

	/**
	 * The FirstSpirit Youtube video data stream builder.
	 */
	public static class Builder implements DataStreamBuilder<YoutubeVideo> {

		private final BaseContext _context;
		private final FilterableAspect _filterableAspect;
		private final StreamBuilderAspectMap _aspects;
		private final YoutubeVideos _youtubeVideos;

		/**
		 * Instantiates a new Builder.
		 *
		 * @param context the context
		 */
		Builder(BaseContext context) {

			List<ParameterSelect.SelectItem> selectItems = new ArrayList<>();

			_youtubeVideos = YoutubeVideos.createInstance(context);

			if (_youtubeVideos != null) {

				if (_youtubeVideos.getChannels() != null && !_youtubeVideos.getChannels().isEmpty()) {
					ParameterSelect.SelectItem selectItemAll = Parameter.Factory.createSelectItem("All Channels", "all");
					selectItems.add(selectItemAll);

					Iterator<Channel> channels = _youtubeVideos.getChannels().iterator();

					while (channels.hasNext()) {
						Channel channel = channels.next();
						ParameterSelect.SelectItem selectItem = Parameter.Factory.createSelectItem(channel.getSnippet().getTitle(), channel.getId());
						selectItems.add(selectItem);
					}
				}
			}

			_context = context;

			_filterableAspect = new FilterableAspect(selectItems);

			_aspects = new StreamBuilderAspectMap();
			_aspects.put(Filterable.TYPE, _filterableAspect);
		}

		@Override
		public DataStream<YoutubeVideo> createDataStream() {
			return new YoutubeVideoDataStream(_context, _youtubeVideos, _filterableAspect.getQuery(), _filterableAspect.getChannel());
		}

		@Override
		public <A> A getAspect(StreamBuilderAspectType<A> aspectType) {
			return _aspects.get(aspectType);
		}
	}

	/**
	 * Aspect to provide filters for a data stream.
	 */
	public static class FilterableAspect implements Filterable {

		private ParameterText _query = null;
		private ParameterSelect _channel = null;

		private ParameterMap _filter = null;

		private FilterableAspect(List<ParameterSelect.SelectItem> selectItems) {
			if (selectItems != null && !selectItems.isEmpty()) {
				_channel = Parameter.Factory.createSelect("channelFilterSelect", selectItems, "all");
			}
			_query = Parameter.Factory.createText("query", "", "");
		}

		@Override
		public List<Parameter<?>> getDefinedParameters() {
			List<Parameter<?>> pList = new ArrayList<>();
			pList.add(_query);
			if (_channel != null) {
				pList.add(_channel);
			}

			return pList;
		}

		@Override
		public void setFilter(ParameterMap filter) {
			_filter = filter;
		}

		/**
		 * Gets the query string.
		 *
		 * @return the query string
		 */
		@Nullable
		String getQuery() {
			String query = _filter.get(_query);
			return !Strings.isEmpty(query) ? query : null;
		}

		/**
		 * Gets the selected channel.
		 *
		 * @return the channel id
		 */
		@Nullable
		String getChannel() {
			String channel = null;
			if (_channel != null) {
				channel = _filter.get(_channel);
			} else {
				return null;
			}
			return !Strings.isEmpty(channel) ? channel : null;
		}
	}
}
