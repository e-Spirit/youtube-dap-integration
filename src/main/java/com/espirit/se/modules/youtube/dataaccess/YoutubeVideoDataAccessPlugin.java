package com.espirit.se.modules.youtube.dataaccess;


import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.report.JavaClientExecutableReportItem;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.client.plugin.report.ReportItem;
import de.espirit.firstspirit.ui.operations.RequestOperation;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

import com.espirit.moddev.components.annotations.PublicComponent;
import com.espirit.se.modules.youtube.YoutubeVideo;
import com.espirit.se.modules.youtube.integration.YoutubeIntegrationIcons;
import com.espirit.se.modules.youtube.integration.YoutubeIntegrationProjectApp;

import javax.swing.Icon;
import java.awt.Desktop;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

// TODO: Add better description
@PublicComponent(name = "YoutubeVideoDataAccessPlugin",
		displayName = "Youtube Video Data Access Plugin",
		description = "Youtube Video Data Access Plugin")
public class YoutubeVideoDataAccessPlugin implements DataAccessPlugin<YoutubeVideo> {

	private final DataAccessAspectMap _aspects = new DataAccessAspectMap();


	public void setUp(BaseContext context) {
		if (YoutubeIntegrationProjectApp.isInstalled(context)) {
			_aspects.put(Reporting.TYPE, new YouTubeVideoReportingAspect(context));
			_aspects.put(ReportItemsProviding.TYPE, new YoutubeVideoReportItemsProvidingAspect());
		}
	}


	public void tearDown() {
		// Nothing needs to be done here
	}


	@Override
	public DataAccessSessionBuilder<YoutubeVideo> createSessionBuilder() {
		return new YoutubeVideoDataAccessSession.Builder();
	}


	@Override
	public <A> A getAspect(DataAccessAspectType<A> aspectType) {
		return _aspects.get(aspectType);
	}


	@Override
	public Image<?> getIcon() {
		return null;
	}


	@Override
	public String getLabel() {
		return "YouTube";
	}


	public static class YouTubeVideoReportingAspect implements Reporting {

		private final BaseContext _context;


		private YouTubeVideoReportingAspect(BaseContext context) {
			_context = context;
		}


		@Override
		public Image<?> getReportIcon(boolean active) {
			if (_context.is(BaseContext.Env.WEBEDIT)) {
				return active ? YoutubeIntegrationIcons.getActive(_context) : YoutubeIntegrationIcons.getInactive(_context);
			} else {
				return active ? YoutubeIntegrationIcons.getInactive(_context) : YoutubeIntegrationIcons.getActive(_context);
			}
		}
	}


	public static class YoutubeVideoReportItemsProvidingAspect implements ReportItemsProviding<YoutubeVideo> {

		private final YoutubeVideoPreviewItem _clickItem;


		private YoutubeVideoReportItemsProvidingAspect() {
			_clickItem = new YoutubeVideoPreviewItem();
		}


		@Override
		public ReportItem<YoutubeVideo> getClickItem() {
			return _clickItem;
		}


		@Override
		public Collection<? extends ReportItem<YoutubeVideo>> getItems() {
			return Collections.emptyList();
		}


		private class YoutubeVideoPreviewItem implements JavaClientExecutableReportItem<YoutubeVideo>, WebeditExecutableReportItem<YoutubeVideo> {

			@Override
			public boolean isVisible(ReportContext<YoutubeVideo> context) {
				return true;
			}


			@Override
			public boolean isEnabled(ReportContext<YoutubeVideo> context) {
				return true;
			}


			@Override
			public String getLabel(ReportContext<YoutubeVideo> context) {
				return null;
			}


			@Override
			public String getIconPath(ReportContext<YoutubeVideo> context) {
				return null;
			}


			@Override
			public Icon getIcon(ReportContext<YoutubeVideo> context) {
				return null;
			}


			@Override
			public void execute(ReportContext<YoutubeVideo> context) {
				YoutubeVideo video = context.getObject();
				if (context.is(BaseContext.Env.WEBEDIT)) {
					ClientScriptOperation clientScript = context.requireSpecialist(OperationAgent.TYPE).getOperation(ClientScriptOperation.TYPE);
					String title = video.getTitle().replaceAll("'", "\\\\'");
					String script = String.format("openYoutubePreview('%s', '%s')", title, video.getId());
					clientScript.perform(script, false);
				} else {
					String url = "https://www.youtube.com/watch?v=" + video.getId();
					try {
						Desktop desktop = Desktop.getDesktop();
						desktop.browse(new URI(url));
					} catch (final Exception e) {
						RequestOperation message = context.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
						message.setTitle("YouTube");
						message.perform(url);
					}
				}
			}
		}
	}
}
