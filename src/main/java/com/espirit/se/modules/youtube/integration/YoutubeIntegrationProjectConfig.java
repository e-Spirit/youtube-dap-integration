package com.espirit.se.modules.youtube.integration;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.templatestore.SectionTemplate;
import de.espirit.firstspirit.access.store.templatestore.SectionTemplates;
import de.espirit.firstspirit.access.store.templatestore.TemplateStoreRoot;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.module.ProjectEnvironment;

import com.espirit.ps.psci.genericconfiguration.ExecuteAction;
import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import com.espirit.se.modules.youtube.YoutubeVideos;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipFile;


public class YoutubeIntegrationProjectConfig extends GenericConfigPanel<ProjectEnvironment> {

	public final static String CONFIG_API_KEY = "api_key";
	public final static String CONFIG_CHANNEL_IDS = "channel_ids";


	@Override
	protected void configure() {
		ConfigGuiBuilder builder = builder().title("YouTube Integration Configuration");
		builder.text("Google API Key", CONFIG_API_KEY, "");
		builder.text("Channel IDs (optional)", CONFIG_CHANNEL_IDS, "");

		CheckSettingsAction checkSettingsAction = new CheckSettingsAction(this);
		builder.button("Check Settings", "check_button", checkSettingsAction, null);

		ImportSampleSectionAction importSampleSectionAction = new ImportSampleSectionAction(getEnvironment().getBroker());
		builder.button("Import Sample Template", "import_button", importSampleSectionAction, null);
	}


	private static class CheckSettingsAction implements ExecuteAction {

		private final YoutubeIntegrationProjectConfig _config;


		CheckSettingsAction(YoutubeIntegrationProjectConfig config) {
			_config = config;
		}


		@Override
		public void perform() {
			String apiKey = _config.getFormValue(CONFIG_API_KEY);
			if (apiKey != null && !apiKey.isEmpty()) {
				String channelIds = _config.getFormValue(CONFIG_CHANNEL_IDS);
				try {
					YoutubeVideos.checkSettings(apiKey, channelIds);
					JOptionPane.showMessageDialog(null, "Connection successful!");
				} catch (Exception e) {
					Logging.logError(e.getMessage(), e, getClass());
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		}
	}


	private static class ImportSampleSectionAction implements ExecuteAction {

		private final SpecialistsBroker _broker;


		ImportSampleSectionAction(SpecialistsBroker broker) {
			_broker = broker;
		}


		@Override
		public void perform() {
			ZipFile zipFile = null;
			try {
				File tempFile = File.createTempFile("export_youtube_video", ".zip");
				tempFile.deleteOnExit();

				Files.copy(getClass().getResourceAsStream("/files/export_youtube_video.zip"), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				zipFile = new ZipFile(tempFile);

				TemplateStoreRoot templateStore = (TemplateStoreRoot) _broker.requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.TEMPLATESTORE);
				SectionTemplates sectionTemplates = templateStore.getSectionTemplates();
				StoreElement element = sectionTemplates.importStoreElement(zipFile, null);

				if (element instanceof SectionTemplate) {
					SectionTemplate sectionTemplate = (SectionTemplate) element;
					sectionTemplate.setLock(true);
					sectionTemplate.save();
					sectionTemplate.setLock(false);
					String message = String.format("Sample SectionTemplate successfully imported! (uid = %s)", sectionTemplate.getUid());
					JOptionPane.showMessageDialog(null, message);
				}

			} catch (Exception e) {
				Logging.logError(e.getMessage(), e, getClass());
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
						Logging.logError("Could not close zip file.", e, YoutubeIntegrationProjectConfig.class);
					}
				}
			}
		}
	}
}
