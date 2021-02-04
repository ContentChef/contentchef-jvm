<div align="center">
  <img src="assets/ContentChef_logo_banner.svg" height="128"/>
</div>

![buildStatusBadge](https://github.com/contentchef/contentchef-jvm/workflows/Build/badge.svg)

# ContentChef Java/Kotlin/Android SDK

Welcome to [ContentChef API-First CMS's](https://www.contentchef.io/) Java/Kotlin/Android SDK.

## How to use it

**Java/Kotlin/Android**

Create your ContentChef instance like this:

    val contentChef = CallbackContentChefProvider.getContentChef(  
        ContentChefEnvironmentConfiguration(  
            ContentChefEnvironment.LIVE, SPACE_ID
      )  
    )

*SPACE_ID* can be retrieved from your [ContentChef's dashboard](https://app.contentchef.io/).

You can now use your `contentChef` instance to get the channel you want to use to retrieve info: you have two channels, the `OnlineChannel` and the `PreviewChannel`.

With the `OnlineChannel` you can retrieve contents which are in _live_ state and which are actually visible, while with the `PreviewChannel` you can retrieve contents which are in in both _stage_ and _live_ states and even contents that are not visible in the current date.

Both the `OnlineChannel` and the `PreviewChannel` have two methods which are `getContent()` and `search()`

You can use the `getContent()` method to collect a specific content by its own `publicId`, for example to retrieve a single post from your blog, a single image from a gallery or a set of articles from your featured articles list. Otherwise you can use the `search()` method to find contents with multiple matching criteria, like content definition name, publishing dates and more.

## Examples

*PUBLISHING_CHANNEL* can be retrieved from your [ContentChef's dashboard](https://app.contentchef.io/).

*PREVIEW_API_KEY* and *ONLINE_API_KEY* must be used to retrieve contents from the `PreviewChannel` and the `OnlineChannel` respectively. Both the api keys can be retrieved from your [ContentChef's dashboard](https://app.contentchef.io/).

Retrieve the *new-header* content from the _live_ environment:

    val onlineContentRequestData = OnlineContentRequestData(  
        "new-header"  
    )
    
    val onlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL)
    
    onlineChannel.getContent(onlineContentRequestData, {  
      println("onSuccess $it")  
    }, {  
      println("onError $it")  
    })

Preview the *new-header* content in a given future date:

    val targetDate = ContentChefDateFormat.parseDate("2030-01-01T05:42:17.945-05")!!
    
    val previewContentRequestData = PreviewContentRequestData(  
        "new-header", targetDate
    )
    
    val previewChannel = contentChef.getPreviewChannel(PREVIEW_API_KEY, PUBLISHING_CHANNEL)
    
    previewChannel.getContent(previewContentRequestData, {  
      println("onSuccess $it")  
    }, {  
      println("onError $it")  
    })

Search for all the contents with definition as *default-header* in the _live_ environment:

    val searchOnlineRequestData = SearchOnlineRequestData(  
        contentDefinitions = listOf("default-header"),  
      take = 10  
    )
    
    val onlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL)
    
    onlineChannel.search(searchOnlineRequestData, {  
      println("onSuccess $it")  
    }, {  
      println("onError $it")  
    })

Preview all the contents with definition as *default-header* in a given future date:

    val targetDate = ContentChefDateFormat.parseDate("2030-01-01T05:42:17.945-05")!!
    
    val searchPreviewRequestData = SearchPreviewRequestData(  
        contentDefinitions = listOf("default-header"),  
      targetDate = targetDate,  
      take = 10  
    )
    
    val previewChannel = contentChef.getPreviewChannel(PREVIEW_API_KEY, PUBLISHING_CHANNEL)
    
    previewChannel.search(searchPreviewRequestData, {  
      println("onSuccess $it")  
    }, {  
      println("onError $it")  
    })

Look at *sampleapp* and *sampleapp-android* in the source code for more examples.

## Installation

To add ContentChef to your project you can use Maven or Gradle.

**Java/Kotlin:**

If using Maven, add this to your `pom.xml` dependencies:

    <dependency>
        <groupId>io.contentchef</groupId>
        <artifactId>contentchef-jvm-callback</artifactId>
        <version>x.y.z</version>
    </dependency>

If using Gradle, add this to your `build.gradle` dependencies:

    implementation 'io.contentchef:contentchef-jvm-callback:x.y.z'

**Android**

Add this to your `build.gradle` dependencies:

    implementation 'io.contentchef:contentchef-jvm-callback-android:x.y.z'

## Dependencies

The Java/Kotlin SDK depends on `org.json:json:20190722`.  
The Android SDK has no dependencies at all.
