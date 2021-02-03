package io.contentchef.sample;

import java.util.Collections;
import java.util.Date;

import io.contentchef.callback.CallbackContentChefProvider;
import io.contentchef.callback.common.Channel;
import io.contentchef.callback.common.ContentChef;
import io.contentchef.common.configuration.ContentChefEnvironment;
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration;
import io.contentchef.common.request.DefaultRequestValuesKt;
import io.contentchef.common.request.IndexedFilterCondition;
import io.contentchef.common.request.IndexedFilterItem;
import io.contentchef.common.request.IndexedFilterOperator;
import io.contentchef.common.request.OnlineContentRequestData;
import io.contentchef.common.request.PreviewContentRequestData;
import io.contentchef.common.request.PropFilters;
import io.contentchef.common.request.SearchOnlineRequestData;
import io.contentchef.common.request.SearchPreviewRequestData;
import io.contentchef.common.util.ContentChefDateFormat;
import kotlin.Unit;

@SuppressWarnings("Convert2MethodRef")
class Main {

    private static Unit printSuccess(Object object) {
        String formattedMessage = String.format("onSuccess %s", object);
        System.out.println(formattedMessage);
        return Unit.INSTANCE;
    }

    private static Unit printError(Object object) {
        String formattedMessage = String.format("onError %s", object);
        System.out.println(formattedMessage);
        return Unit.INSTANCE;
    }

    public static void main(String[] args) {
        ContentChef contentChef = CallbackContentChefProvider.getContentChef(
                new ContentChefEnvironmentConfiguration(
                        ContentChefEnvironment.LIVE, ContentChefConfiguration.SPACE_ID
                ), true
        );

        Channel<OnlineContentRequestData, SearchOnlineRequestData> onlineChannel = contentChef.getOnlineChannel(ContentChefConfiguration.ONLINE_API_KEY, ContentChefConfiguration.PUBLISHING_CHANNEL);
        Channel<PreviewContentRequestData, SearchPreviewRequestData> previewChannel = contentChef.getPreviewChannel(ContentChefConfiguration.PREVIEW_API_KEY, ContentChefConfiguration.PUBLISHING_CHANNEL);

        PreviewContentRequestData previewContentRequestData = new PreviewContentRequestData(
                "new-header", new Date()
        );

        OnlineContentRequestData onlineContentRequestData = new OnlineContentRequestData(
                "new-header"
        );

        Date targetDate = ContentChefDateFormat.parseDate("2019-11-22T05:42:17.945-05");

        SearchPreviewRequestData searchPreviewRequestData = new SearchPreviewRequestData(
                Collections.singletonList("default-header"),
                null,
                targetDate
        );

        SearchOnlineRequestData searchOnlineRequestData = new SearchOnlineRequestData(
                Collections.singletonList("default-header")
        );

        SearchPreviewRequestData searchPreviewWithPropFiltersRequestData = new SearchPreviewRequestData(
                Collections.singletonList("default-header"),
                null,
                null,
                null,
                null,
                DefaultRequestValuesKt.DEFAULT_REQUEST_SKIP_VALUE,
                DefaultRequestValuesKt.DEFAULT_REQUEST_TAKE_VALUE,
                new PropFilters.Builder()
                        .indexedFilterCondition(IndexedFilterCondition.AND)
                        .indexedFilterItem(
                                new IndexedFilterItem(
                                        IndexedFilterOperator.STARTS_WITH_IC,
                                        "header",
                                        "A"
                                )
                        )
                        .build()
        );

        previewChannel.getContent(
                previewContentRequestData,
                jsonObjectContentChefItemResponse -> printSuccess(jsonObjectContentChefItemResponse),
                throwable -> printError(throwable)
        );

        previewChannel.getContent(
                previewContentRequestData,
                jsonObjectContentChefItemResponse -> printSuccess(jsonObjectContentChefItemResponse),
                throwable -> printError(throwable),
                jsonObject -> new SampleHeader(jsonObject.getString("header"))
        );

        onlineChannel.getContent(
                onlineContentRequestData,
                jsonObjectContentChefItemResponse -> printSuccess(jsonObjectContentChefItemResponse),
                throwable -> printError(throwable)
        );

        onlineChannel.getContent(
                onlineContentRequestData,
                jsonObjectContentChefItemResponse -> printSuccess(jsonObjectContentChefItemResponse),
                throwable -> printError(throwable),
                jsonObject -> new SampleHeader(jsonObject.getString("header"))
        );

        previewChannel.search(
                searchPreviewRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable)
        );

        previewChannel.search(
                searchPreviewRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable),
                jsonObject -> new SampleHeader(jsonObject.getString("header"))
        );

        onlineChannel.search(
                searchOnlineRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable)
        );

        onlineChannel.search(
                searchOnlineRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable),
                jsonObject -> new SampleHeader(jsonObject.getString("header"))
        );

        //PROP FILTERS EXAMPLE
        previewChannel.search(
                searchPreviewWithPropFiltersRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable)
        );

        previewChannel.search(
                searchPreviewWithPropFiltersRequestData,
                jsonObjectContentChefSearchResponse -> printSuccess(jsonObjectContentChefSearchResponse),
                throwable -> printError(throwable),
                jsonObject -> new SampleHeader(jsonObject.getString("header"))
        );

    }

}
