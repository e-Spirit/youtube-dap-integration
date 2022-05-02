package com.espirit.se.modules.youtube.integration;

import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import com.espirit.moddev.components.annotations.ProjectAppComponent;
import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import com.espirit.ps.psci.genericconfiguration.Values;

/**
 * The FirstSpirit Youtube integration project app.
 * Can be added to a FirstSpirit project to enable and configure Youtube integration.
 */
@ProjectAppComponent(name = "YoutubeVideoProjectApp",
		displayName = "Project App: Youtube Video",
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
		if (GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, broker)) {
			Values values = GenericConfigPanel.values(broker, YoutubeIntegrationProjectApp.class);
			String apiKey = values.getString(YoutubeIntegrationProjectConfig.CONFIG_API_KEY);
			return apiKey != null && !apiKey.isEmpty();
		}
		return false;
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
