package com.espirit.se.modules.youtube.integration;

import de.espirit.firstspirit.agency.SpecialistsBroker;

import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import com.espirit.ps.psci.genericconfiguration.Values;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YoutubeIntegrationProjectAppTest {

	@Test
	void getConfiguration_NOT_INSTALLED() {
		SpecialistsBroker specialistsBrokerMock = mock(SpecialistsBroker.class);
		try (MockedStatic<GenericConfigPanel> genericConfigPanelMockedStatic = Mockito.mockStatic(GenericConfigPanel.class)) {
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, specialistsBrokerMock))
					.thenReturn(false);
			assertNull(YoutubeIntegrationProjectApp.getConfiguration(specialistsBrokerMock));
		}
	}

	@Test
	void getConfiguration() {
		SpecialistsBroker specialistsBrokerMock = mock(SpecialistsBroker.class);
		try (MockedStatic<GenericConfigPanel> genericConfigPanelMockedStatic = Mockito.mockStatic(GenericConfigPanel.class)) {
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, specialistsBrokerMock)).thenReturn(true);
			String apikey = "apikey";
			String channelId = "channelIds";
			Values valuesMock = mock(Values.class);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.API_KEY)).thenReturn(apikey);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.CHANNEL_IDS)).thenReturn(channelId);
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.values(specialistsBrokerMock, YoutubeIntegrationProjectApp.class)).thenReturn(valuesMock);
			YoutubeIntegrationConfig configuration = YoutubeIntegrationProjectApp.getConfiguration(specialistsBrokerMock);

			assertNotNull(configuration);
			assertEquals(apikey, configuration.getApiKey());
			assertNotNull(configuration.getChannelIds());
			assertFalse(configuration.getChannelIds().isEmpty());
			assertEquals(1, configuration.getChannelIds().size());
			assertTrue(configuration.getChannelIds().contains(channelId));
		}
	}

	@Test
	void getConfiguration_NO_CHANNELS() {
		SpecialistsBroker specialistsBrokerMock = mock(SpecialistsBroker.class);
		try (MockedStatic<GenericConfigPanel> genericConfigPanelMockedStatic = Mockito.mockStatic(GenericConfigPanel.class)) {
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, specialistsBrokerMock)).thenReturn(true);
			String apikey = "apikey";
			Values valuesMock = mock(Values.class);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.API_KEY)).thenReturn(apikey);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.CHANNEL_IDS)).thenReturn("");
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.values(specialistsBrokerMock, YoutubeIntegrationProjectApp.class)).thenReturn(valuesMock);
			YoutubeIntegrationConfig configuration = YoutubeIntegrationProjectApp.getConfiguration(specialistsBrokerMock);

			assertNotNull(configuration);
			assertNotNull(configuration.getChannelIds());
			assertTrue(configuration.getChannelIds().isEmpty());
		}
	}

	@Test
	void getConfiguration_MULTI_CHANNEL() {
		SpecialistsBroker specialistsBrokerMock = mock(SpecialistsBroker.class);
		try (MockedStatic<GenericConfigPanel> genericConfigPanelMockedStatic = Mockito.mockStatic(GenericConfigPanel.class)) {
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, specialistsBrokerMock)).thenReturn(true);
			String apikey = "apikey";
			String channelId = "Channel 1, Channel 2";
			Values valuesMock = mock(Values.class);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.API_KEY)).thenReturn(apikey);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.CHANNEL_IDS)).thenReturn(channelId);
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.values(specialistsBrokerMock, YoutubeIntegrationProjectApp.class)).thenReturn(valuesMock);
			YoutubeIntegrationConfig configuration = YoutubeIntegrationProjectApp.getConfiguration(specialistsBrokerMock);

			assertNotNull(configuration);
			assertNotNull(configuration.getChannelIds());
			assertFalse(configuration.getChannelIds().isEmpty());
			assertEquals(2, configuration.getChannelIds().size());
			assertTrue(configuration.getChannelIds().contains("Channel 1"));
			assertTrue(configuration.getChannelIds().contains("Channel 2"));
		}
	}

	@Test
	void getConfiguration_MULTI_CHANNEL_EMPTY() {
		SpecialistsBroker specialistsBrokerMock = mock(SpecialistsBroker.class);
		try (MockedStatic<GenericConfigPanel> genericConfigPanelMockedStatic = Mockito.mockStatic(GenericConfigPanel.class)) {
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.isInstalled(YoutubeIntegrationProjectApp.class, specialistsBrokerMock)).thenReturn(true);
			String apikey = "apikey";
			String channelId = "Channel 1,";
			Values valuesMock = mock(Values.class);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.API_KEY)).thenReturn(apikey);
			when(valuesMock.getString(YoutubeIntegrationProjectConfig.CHANNEL_IDS)).thenReturn(channelId);
			genericConfigPanelMockedStatic.when(() -> GenericConfigPanel.values(specialistsBrokerMock, YoutubeIntegrationProjectApp.class)).thenReturn(valuesMock);
			YoutubeIntegrationConfig configuration = YoutubeIntegrationProjectApp.getConfiguration(specialistsBrokerMock);

			assertNotNull(configuration);
			assertNotNull(configuration.getChannelIds());
			assertFalse(configuration.getChannelIds().isEmpty());
			assertEquals(1, configuration.getChannelIds().size());
			assertTrue(configuration.getChannelIds().contains("Channel 1"));
		}
	}
}