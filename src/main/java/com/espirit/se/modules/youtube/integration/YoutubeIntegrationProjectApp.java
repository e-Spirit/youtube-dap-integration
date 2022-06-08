package com.espirit.se.modules.youtube.integration;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import com.espirit.moddev.components.annotations.ProjectAppComponent;
import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import com.espirit.ps.psci.genericconfiguration.Values;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The FirstSpirit Youtube integration project app.
 * Can be added to a FirstSpirit project to enable and configure Youtube integration.
 */
@ProjectAppComponent(name = "YoutubeVideoProjectApp",
		displayName = "Youtube Video Project App",
		description = "Project application to configure the Youtube integration module.",
		configurable = YoutubeIntegrationProjectConfig.class)
public class YoutubeIntegrationProjectApp implements ProjectApp {

	/**
	 * Checks if this project app is installed in the project associated with
	 * the specified context and whether an API key is set.
	 *
	 * @param broker the project related broker
	 * @return true if the project app is installed and an API key is set, false otherwise
	 */
	public static boolean isInstalled(SpecialistsBroker broker) {
		YoutubeIntegrationConfig configuration = getConfiguration(broker);
		return configuration != null && Strings.notEmpty(configuration.getApiKey());
	}

	/**
	 * Checks if this project app is installed in the project associated with
	 * the specified context and whether an API key is set. If so, a new configuration object is created.
	 *
	 * @param broker the project related broker
	 * @return true if the project app is installed and an API key is set, false otherwise
	 */
	@Nullable
	public static YoutubeIntegrationConfig getConfiguration(SpecialistsBroker broker) {
		if (GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, broker)) {
			Values values = GenericConfigPanel.values(broker, YoutubeIntegrationProjectApp.class);
			String apiKey = values.getString(YoutubeIntegrationProjectConfig.API_KEY);
			String channelIds = values.getString(YoutubeIntegrationProjectConfig.CHANNEL_IDS);
			List<String> channelIdList = new ArrayList<>();
			if (Strings.notEmpty(channelIds)) {
				channelIdList = Arrays.stream(channelIds.split(",")).map(String::trim).collect(Collectors.toList());
			}
			return new YoutubeIntegrationConfig(apiKey, channelIdList);
		}
		return null;
	}

	@Override
	public void init(ProjectAppDescriptor descriptor, ProjectEnvironment environment) {
		// Nothing needs to be done here
	}

	@Override
	public void installed() {
		// Nothing needs to be done here
	}

	@Override
	public void uninstalling() {
		// Nothing needs to be done here
	}

	@Override
	public void updated(String oldVersionString) {
		// Nothing needs to be done here
	}
}
