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
import com.espirit.se.modules.youtube.connector.YoutubeConnector;
import com.espirit.se.modules.youtube.connector.YoutubeVideoSearchRequest;
import com.espirit.se.modules.youtube.integration.YoutubeIntegrationConfig;
import com.espirit.se.modules.youtube.integration.YoutubeIntegrationProjectApp;
import com.google.api.services.youtube.model.Channel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The FirstSpirit Youtube video data stream.
 * Please see FirstSpirit API for more information.
 */
public class YoutubeVideoDataStream implements DataStream<YoutubeVideo> {

	private final YoutubeVideoSearchRequest _youtubeVideoSearchRequest;

	public YoutubeVideoDataStream(final YoutubeVideoSearchRequest youtubeVideoSearchRequest) {
		_youtubeVideoSearchRequest = youtubeVideoSearchRequest;
	}

	@Override
	public void close() {
		// Nothing
	}

	@Override
	public List<YoutubeVideo> getNext(int count) {
		if (!_youtubeVideoSearchRequest.hasNext()) {
			return Collections.emptyList();
		}
		return _youtubeVideoSearchRequest.searchVideos(count);
	}

	@Override
	public int getTotal() {
		return _youtubeVideoSearchRequest.getTotal();
	}

	@Override
	public boolean hasNext() {
		return _youtubeVideoSearchRequest.hasNext();
	}

	/**
	 * The FirstSpirit Youtube video data stream builder.
	 * Please see FirstSpirit API for more information.
	 */
	public static class Builder implements DataStreamBuilder<YoutubeVideo> {

		private final FilterableAspect _filterableAspect;
		private final StreamBuilderAspectMap _aspects;
		private final YoutubeConnector _youtubeConnector;

		/**
		 * Instantiates a new Builder.
		 *
		 * @param context the context
		 */
		Builder(BaseContext context) {
			YoutubeIntegrationConfig configuration = YoutubeIntegrationProjectApp.getConfiguration(context);
			_youtubeConnector = YoutubeConnector.createInstance(configuration);
			_aspects = new StreamBuilderAspectMap();

			List<ParameterSelect.SelectItem> selectItems = new ArrayList<>();
			if (_youtubeConnector != null) {
				List<Channel> youtubeChannels = _youtubeConnector.getChannels();
				if (youtubeChannels != null && !youtubeChannels.isEmpty()) {
					ParameterSelect.SelectItem selectItemAll = Parameter.Factory.createSelectItem("All Channels", "all");
					selectItems.add(selectItemAll);

					for (final Channel youtubeChannel : youtubeChannels) {
						ParameterSelect.SelectItem selectItem = Parameter.Factory.createSelectItem(youtubeChannel.getSnippet().getTitle(), youtubeChannel.getId());
						selectItems.add(selectItem);
					}
				}
			}
			_filterableAspect = new FilterableAspect(selectItems);
			_aspects.put(Filterable.TYPE, _filterableAspect);
		}

		@Override
		public DataStream<YoutubeVideo> createDataStream() {
			return new YoutubeVideoDataStream(_youtubeConnector.getSearchRequest(_filterableAspect.getQuery(), _filterableAspect.getChannel()));
		}

		@Override
		public <A> A getAspect(StreamBuilderAspectType<A> aspectType) {
			return _aspects.get(aspectType);
		}
	}

	/**
	 * Aspect to provide filters for a data stream.
	 * Please see FirstSpirit API for more information.
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
			}
			return !Strings.isEmpty(channel) ? channel : null;
		}
	}
}
