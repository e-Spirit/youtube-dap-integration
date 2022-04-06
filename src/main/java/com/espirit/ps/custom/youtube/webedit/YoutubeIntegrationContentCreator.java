package com.espirit.ps.custom.youtube.webedit;

import com.espirit.moddev.components.annotations.WebAppComponent;
import com.espirit.moddev.components.annotations.WebResource;
import de.espirit.firstspirit.module.WebApp;
import de.espirit.firstspirit.module.WebEnvironment;
import de.espirit.firstspirit.module.descriptor.WebAppDescriptor;

@WebAppComponent(name = "YouTube-DAP-Integration WebApp", webXml = "web/web.xml",
        webResources = {
        @WebResource(name = "webfiles", version = "", path = "web/", targetPath = "/yt"),
                @WebResource(name = "icons", version = "", path = "icons/", targetPath = "/yt/icons"),


        }
        )
public class YoutubeIntegrationContentCreator implements WebApp {
    @Override
    public void createWar() {

    }

    @Override
    public void init(WebAppDescriptor webAppDescriptor, WebEnvironment webEnvironment) {

    }

    @Override
    public void installed() {

    }

    @Override
    public void uninstalling() {

    }

    @Override
    public void updated(String s) {

    }
}
