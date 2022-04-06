package com.espirit.ps.custom.youtube.executable;


import com.espirit.moddev.components.annotations.PublicComponent;
import com.espirit.ps.custom.youtube.YoutubeVideo;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.forms.FormField;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.CommodityContainer;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.TransferType;

import java.io.Writer;
import java.util.List;
import java.util.Map;

@PublicComponent(name="DropYoutubeVideo")
public class YoutubeVideoDropExecutable implements Executable {
	private final static String PARAM_ID = "id";
	private final static String PARAM_TITLE = "title";
	private final static String PARAM_DESCRIPTION = "description";


	@Override
	public Object execute(final Map<String, Object> parameter) {
		BaseContext context = (BaseContext) parameter.get("context");
		if (parameter.containsKey("drop") && parameter.containsKey("dropdata")) {
			TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
			TransferType<YoutubeVideo> videoTransferType = transferAgent.getRawValueType(YoutubeVideo.class);
			CommodityContainer dropdata = (CommodityContainer) parameter.get("dropdata");
			List<YoutubeVideo> videos = dropdata.get(videoTransferType);

			if (!videos.isEmpty()) {
				processVideo(videos.get(0), parameter);
			}
		}
		return null;
	}

	private void processVideo(final YoutubeVideo video, final Map<String, Object> paramMap) {
		if (video != null) {
			if (paramMap.containsKey(PARAM_ID)) {
				((FormField<?>) paramMap.get(PARAM_ID)).set(video.getId());
			}
			if (paramMap.containsKey(PARAM_TITLE)) {
				((FormField<?>) paramMap.get(PARAM_TITLE)).set(video.getTitle());
			}
			if (paramMap.containsKey(PARAM_DESCRIPTION)) {
				((FormField<?>) paramMap.get(PARAM_DESCRIPTION)).set(video.getDescription());
			}
		}
	}

	@Override
	public Object execute(final Map<String, Object> parameter, final Writer out, final Writer err) {
		return execute(parameter);
	}
}
