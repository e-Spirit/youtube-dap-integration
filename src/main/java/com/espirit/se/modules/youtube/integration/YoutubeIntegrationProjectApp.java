package com.espirit.se.modules.youtube.integration;


import com.espirit.moddev.components.annotations.ProjectAppComponent;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

@ProjectAppComponent(name="YouTube-DAP-Integration", configurable = YoutubeIntegrationProjectConfig.class)
public class YoutubeIntegrationProjectApp implements ProjectApp {

	@Override
	public void init(ProjectAppDescriptor descriptor, ProjectEnvironment environment) {
	}

	@Override
	public void installed() {
	}

	@Override
	public void uninstalling() {
	}

	@Override
	public void updated(String oldVersionString) {
	}

	public static boolean isInstalled(SpecialistsBroker broker) {
		if (YoutubeIntegrationProjectConfig.isInstalled(YoutubeIntegrationProjectApp.class, broker)) {
			Values values = YoutubeIntegrationProjectConfig.values(broker, YoutubeIntegrationProjectApp.class);
			String apiKey = values.getString(YoutubeIntegrationProjectConfig.CONFIG_API_KEY);
			return apiKey != null && !apiKey.isEmpty();
		}
		return false;
	}
}