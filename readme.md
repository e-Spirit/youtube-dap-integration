# YouTube-DAP-Integration
**Status: _stable release_**

This module integrates access to YouTube into FirstSpirit. A report is created which lists 
videos that could be searched and filtered by channels. Content could be integrated using drag &
drop.

The implementation serves as an example on how to create, build and install modules for third party
integrations.



## Requirements
* Java 11
* FirstSpirit 5.2.200303+
  * _tested with 5.2.220512_ 

## Building the module
Please be aware that you will need access to our artifactory to compile and build the module 
as you won't be able to get all required dependencies otherwise. Please see [FirstSpirit ODFS](https://docs.e-spirit.com/odfs/plug-developmen/implementation/index.html) for general
information on how to setup and build FirstSpirit modules. 

### Setting up work environment
As a developer you need to set up your environment. This has only to be done once since the 
configuration is shared for all repositories. Therefore you will have to set artifactory credentials.

#### Setting artifactory credentials to access Module
Dependencies specified in your module will be downloaded from our artifactory which acts as
a Maven repository. For this to work you need to specify the credentials in your personal, not
module-local gradle.properties (see specification of [Gradle Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties)).
The file is located in `$HOME/.gradle/gradle.properties` and should contain at least these lines:
```
artifactory_hosting_username=CLOUD_USERNAME
artifactory_hosting_password=CLOUD_ENCRYPTED_PASSWORD
```
`CLOUD_USERNAME` is usually your complete e-mail address. The encrypted password can be
retrieved through a simple [artifactory REST call](https://artifactory.e-spirit.hosting/artifactory/api/security/encryptedPassword).
(If prompted for a username and password, please use your cloud credentials.)

### Executing Gradle task
To actually build the module navigate to your project directory and execute
```
./gradlew build
```
This will use the gradle version that comes with this module. If you want to use a locally installed version of gradle
for any reason you can simply call
```
gradle build
```
Afterwards the module binary can be found in `build/fsm/YouTube-DAP-Integration-version.fsm`.

## Installation
1. Install the Module (YouTube-DAP-Integration-_version_.fsm)
2. In general add the Project App "_Youtube Video Project App_" to enable the report for any project 
3. To enable the report in **ContentCreator**, add (and deploy) the web component "_Youtube Video Web App_" in _ContentCreator_ web application (project-local or global*) 

_Even if the component is installed globally, the report is only available if the Project App is installed for the project_. 


## Configuration 
### Project App
* **Google API Key**, generated via [https://console.developers.google.com/](https://console.developers.google.com/), it must be an application key and include the "_YouTube Data API_"  
* **Channel ID (optional)**, paste an ID from a YouTube Channel URL (www.youtube.com/channel/**UC17RtCQNOtLpzY866GUsYvQ**)
  * Using a Channel ID limits the report to only display videos from this specific channel
  * Only if a Channel ID is set, the report displays any video when no search query is defined
* **Import Sample Template**, click here to automatically create a full featured Sample Section Template in this project 


### Templating
In your templates you could use a `FS_INDEX` to select a video from a list or a `FS_BUTTON` to drop a video on it, or a combination of both.

#### Using FS_INDEX
Add this to your Form source:
```xml
<FS_INDEX name="st_video" height="1" useLanguages="yes" viewMode="details">
  <LANGINFOS>
    <LANGINFO lang="*" label="Youtube Video"/>
  </LANGINFOS>
  <SOURCE name="YouTube-DAP-Integration/YoutubeVideoDataAccessPlugin"/>
</FS_INDEX>
```

*Tip:* If you only want to allow the selection of single video add this rule:
```xml
<RULE>
    <WITH>
        <PROPERTY name="EMPTY" source="st_video"/>
    </WITH>
    <DO>
        <PROPERTY name="ADD" source="st_video"/>
    </DO>
</RULE>
```

##### Output:
~~~
$-- usage on single video-item FS_INDEX --$
$CMS_SET(set_video, if(!st_video.empty, st_video.values.first))$
$CMS_IF(!set_video.empty)$
    <h1>$CMS_VALUE(set_video.title)$</h1>
    <iframe width="640" height="360"
     src="http://www.youtube.com/embed/$CMS_VALUE(set_video.id)$" frameborder="0"/>
$CMS_END_IF$
~~~

##### YoutubeVideo Interface
```java
package com.espirit.se.modules.youtube;

public interface YoutubeVideo {
    String getId();
    String getTitle();
    String getDescription();
    String getThumbnailUrl();
    String getPosterUrl();
}
```

#### Using FS_BUTTON
```xml
<FS_BUTTON name="st_dropVideo" alwaysEnabled="no" hFill="yes" onDrop="class:YoutubeVideoDropExecutable" useLanguages="no">
	<DROPTYPES>
		<MIME classname="YoutubeVideo"/>
	</DROPTYPES>
	<LANGINFOS>
		<LANGINFO lang="*" label="Drop Video here"/>
	</LANGINFOS>
	<PARAMS>
		<PARAM name="id">#field.st_videoId</PARAM>
		<PARAM name="title">#field.st_title</PARAM>
		<PARAM name="description">#field.st_description</PARAM>
	</PARAMS>
</FS_BUTTON>
```

Drop a video on this button will set the _id_, _title_ and/or _description_ to the specified form fields.
The params `id`, `title` and `description` are each optional. The value must be a String-based component.


## Features
### ContentCreator
* Report with a search bar
* Clicking a report item will open a dialog with a preview
* Hovering a report item shows detailed information about the video
* Drag and drop a video on InEdit targets
  * String-based, to clone the video title
  * FS_INDEX, adds the video to the list (if you want a single video list, keep in mind, that dropping a video on a index component will always extend the list)

### SiteArchitect
* Report with a search bar
* Clicking a report item will open a browser with a preview
* Drag and drop a video on String-based input component to clone the video title
* Drag and drop a video on FS_INDEX to add/replace a video
* Drag and drop on the global search bar to force a search for usages of this video

## Legal Notices
The YouTube-DAP-Integration is a reference of [e-Spirit GmbH](http://www.e-spirit.com/), Dortmund, Germany.
The YouTube-DAP-Integration is subject to the Apache-2.0 license.

## Disclaimer
This document is provided for information purposes only. e-Spirit may change the contents 
hereof without notice. This document is not warranted to be error-free, nor subject to any 
other warranties or conditions, whether expressed orally or implied in law, including implied 
warranties and conditions of merchantability or fitness for a particular purpose. e-Spirit 
specifically disclaims any liability with respect to this document and no contractual 
obligations are formed either directly or indirectly by this document. The technologies, 
functionality, services, and processes described herein are subject to change without notice.
