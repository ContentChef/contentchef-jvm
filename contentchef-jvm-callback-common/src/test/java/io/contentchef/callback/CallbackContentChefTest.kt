package io.contentchef.callback

import io.contentchef.callback.common.CallbackContentChef
import io.contentchef.callback.common.ContentChef
import io.contentchef.callback.common.OnlineChannel
import io.contentchef.callback.model.SampleHeader
import io.contentchef.common.configuration.ContentChefEnvironment
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration
import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefResponseMetadata
import io.contentchef.common.data.ContentChefResponseRequestContext
import io.contentchef.common.exception.ContentNotFoundException
import io.contentchef.common.exception.GenericErrorException
import io.contentchef.common.exception.InvalidResponseException
import io.contentchef.common.exception.UnableToUseProvidedMapperException
import io.contentchef.common.log.Logger
import io.contentchef.common.network.ConnectionFactory
import io.contentchef.common.network.ConnectionStreamReader
import io.contentchef.common.network.ContentChefResponseMapper
import io.contentchef.common.network.RequestFactory
import io.contentchef.common.request.OnlineContentRequestData
import io.contentchef.common.util.ContentChefDateFormat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class CallbackContentChefTest {

    companion object {
        const val VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE =
            "{\"publicId\":\"my-header\",\"definition\":\"default-header\",\"repository\":\"defaultRepository\",\"payload\":{\"header\":\"My header\"},\"onlineDate\":\"2019-10-16T02:06:00.000Z\",\"offlineDate\":\"2019-10-31T22:59:00.000Z\",\"metadata\":{\"id\":7,\"authoringContentId\":35,\"contentVersion\":3,\"contentLastModifiedDate\":\"2019-10-17T00:00:00.000Z\",\"tags\":[\"tag\",\"anothertag\"],\"publishedOn\":\"2019-10-17T10:10:59.835Z\"},\"requestContext\":{\"publishingChannel\":\"depc\",\"cloudName\":\"contentchef\",\"timestamp\":\"2019-10-18T12:49:45.700Z\"}}\n"
        const val INVALID_JSON_CHEF_EXAMPLE_RESPONSE = "NOT_VALID_JSON_RESPONSE"
        const val GENERIC_ERROR_500_CHEF_EXAMPLE_RESPONSE =
            "{\"message\":\"Internal server error\"}"
    }

    @MockK
    lateinit var contentChefEnvironmentConfigurationMock: ContentChefEnvironmentConfiguration
    @MockK
    lateinit var connectionFactoryMock: ConnectionFactory
    @MockK
    lateinit var connectionStreamReaderMock: ConnectionStreamReader
    @MockK
    lateinit var loggerMock: Logger
    @MockK
    lateinit var httpURLConnectionMock: HttpURLConnection
    @MockK
    lateinit var onSuccessJSONObjectMock: ((ContentChefItemResponse<JSONObject>) -> Unit)
    @MockK
    lateinit var onErrorMock: ((Throwable) -> Unit)
    private lateinit var requestFactory: RequestFactory

    private lateinit var contentChef: ContentChef
    private lateinit var onlineChannel: OnlineChannel


    @Before
    fun before() {
        MockKAnnotations.init(this)

        every { httpURLConnectionMock.disconnect() } just Runs
        every { httpURLConnectionMock.connectTimeout = any() } just Runs
        every { httpURLConnectionMock.readTimeout = any() } just Runs
        every { httpURLConnectionMock.connect() } just Runs

        every { connectionFactoryMock.getConnection(any()) } returns httpURLConnectionMock

        every { loggerMock.log(any(), any(), any()) } just Runs

        every { onSuccessJSONObjectMock(any()) } just Runs
        every { onErrorMock(any()) } just Runs

        requestFactory = RequestFactory(
            ContentChefResponseMapper,
            connectionFactoryMock,
            connectionStreamReaderMock,
            loggerMock
        )

    }

    private fun generateContentChefMocksBasedOn(contentChefEnvironment: ContentChefEnvironment) {
        every { contentChefEnvironmentConfigurationMock.contentChefEnvironment } returns contentChefEnvironment
        every { contentChefEnvironmentConfigurationMock.onlineApiKey } returns ""
        every { contentChefEnvironmentConfigurationMock.previewApiKey } returns ""

        every {
            contentChefEnvironmentConfigurationMock.generateWebserviceURL(
                any(),
                any()
            )
        } returns "http://localhost"

        contentChef = CallbackContentChef(
            contentChefEnvironmentConfigurationMock,
            requestFactory,
            SequentialRequestExecutor
        )

        onlineChannel = contentChef.getOnlineChannel("TEST_PUBLISHING_CHANNEL")
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenUsingOnlineContentMethodOnStagingEnvironmentThenCheckIllegalArgumentExceptionIsThrown() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.STAGING)

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessJSONObjectMock,
            onErrorMock
        )
    }

    @Test
    fun whenRequestingAContentWhichDoesNotExistThenCheckContentNotFoundExceptionIsThrown() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 404
        every { connectionStreamReaderMock.getContentAsString(any()) } returns ""

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessJSONObjectMock,
            onErrorMock
        )

        verify {
            onSuccessJSONObjectMock(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(ContentNotFoundException::class))
        }
    }

    @Test
    fun whenRequestingAContentWhichExistsWithoutAMapperThenCheckResponseIsParsedCorrectly() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessJSONObjectMock,
            onErrorMock
        )

        verify(exactly = 1) {
            val onlineDate = ContentChefDateFormat.parseDate("2019-10-16T02:06:00.000ZZ")!!
            val offlineDate = ContentChefDateFormat.parseDate("2019-10-31T22:59:00.000Z")!!
            val contentLastModifiedDate =
                ContentChefDateFormat.parseDate("2019-10-17T00:00:00.000Z")!!
            val publishedOn = ContentChefDateFormat.parseDate("2019-10-17T10:10:59.835Z")!!
            val timestamp = ContentChefDateFormat.parseDate("2019-10-18T12:49:45.700Z")!!

            val jsonObject = JSONObject(mapOf("header" to "My header"))

            onSuccessJSONObjectMock(
                withArg { actualArgument ->
                    assertContentChefResponseWithJsonObjectIsEqualsTo(
                        ContentChefItemResponse(
                            "my-header",
                            "default-header",
                            "defaultRepository",
                            jsonObject,
                            onlineDate,
                            offlineDate,
                            ContentChefResponseMetadata(
                                7L,
                                35L,
                                3L,
                                contentLastModifiedDate,
                                listOf("tag", "anothertag"),
                                publishedOn
                            ),
                            ContentChefResponseRequestContext(
                                "depc",
                                null,
                                "contentchef",
                                timestamp
                            )
                        ), actualArgument
                    )
                })

        }

        verify {
            onErrorMock(any()) wasNot Called
        }

    }

    @Test
    fun whenRequestingAContentWhichExistsWithAMapperThenCheckResponseIsParsedCorrectly() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE

        val onSuccessMyHeaderMock = mockk<((ContentChefItemResponse<SampleHeader>) -> Unit)>()

        val slot = slot<ContentChefItemResponse<SampleHeader>>()

        every { onSuccessMyHeaderMock(capture(slot)) } just Runs

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessMyHeaderMock,
            onErrorMock,
            {
                SampleHeader(it.getString("header"))
            })

        verify(exactly = 1) {
            val onlineDate = ContentChefDateFormat.parseDate("2019-10-16T02:06:00.000ZZ")!!
            val offlineDate = ContentChefDateFormat.parseDate("2019-10-31T22:59:00.000Z")!!
            val contentLastModifiedDate =
                ContentChefDateFormat.parseDate("2019-10-17T00:00:00.000Z")!!
            val publishedOn = ContentChefDateFormat.parseDate("2019-10-17T10:10:59.835Z")!!
            val timestamp = ContentChefDateFormat.parseDate("2019-10-18T12:49:45.700Z")!!

            onSuccessMyHeaderMock(
                ContentChefItemResponse(
                    "my-header",
                    "default-header",
                    "defaultRepository",
                    SampleHeader("My header"),
                    onlineDate,
                    offlineDate,
                    ContentChefResponseMetadata(
                        7L,
                        35L,
                        3L,
                        contentLastModifiedDate,
                        listOf("tag", "anothertag"),
                        publishedOn
                    ),
                    ContentChefResponseRequestContext("depc", null, "contentchef", timestamp)
                )
            )
        }

        verify {
            onErrorMock(any()) wasNot Called
        }
    }

    @Test
    fun whenBackendResponseIsInvalidThenCheckInvalidResponseExceptionIsThrown() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_JSON_CHEF_EXAMPLE_RESPONSE

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessJSONObjectMock,
            onErrorMock
        )

        verify {
            onSuccessJSONObjectMock(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(InvalidResponseException::class))
        }
    }

    @Test
    fun whenBackendResponseIsInvalidThenCheckInvalidResponseExceptionIsThrownAndMapperIsNotCalled() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_JSON_CHEF_EXAMPLE_RESPONSE

        val onSuccessMyHeaderMock = mockk<((ContentChefItemResponse<SampleHeader>) -> Unit)>()

        every { onSuccessMyHeaderMock(any()) } just Runs

        val mapper = mockk<((JSONObject) -> SampleHeader)>()
        every { mapper(any()) } returns SampleHeader("My header")

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessMyHeaderMock,
            onErrorMock,
            mapper
        )

        verify {
            onSuccessMyHeaderMock(any()) wasNot Called
        }

        verify {
            mapper(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(InvalidResponseException::class))
        }
    }

    @Test
    fun whenBackendResponseIs500ThenCheckGenericErrorExceptionIsThrown() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 500
        every { connectionStreamReaderMock.getContentAsString(any()) } returns GENERIC_ERROR_500_CHEF_EXAMPLE_RESPONSE

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessJSONObjectMock,
            onErrorMock
        )

        verify {
            onSuccessJSONObjectMock(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(GenericErrorException::class))
        }
    }

    @Test
    fun whenBackendResponseIs500ThenCheckGenericErrorExceptionIsThrownAndMapperIsNotCalled() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 500
        every { connectionStreamReaderMock.getContentAsString(any()) } returns GENERIC_ERROR_500_CHEF_EXAMPLE_RESPONSE

        val onSuccessMyHeaderMock = mockk<((ContentChefItemResponse<SampleHeader>) -> Unit)>()

        every { onSuccessMyHeaderMock(any()) } just Runs

        val mapper = mockk<((JSONObject) -> SampleHeader)>()
        every { mapper(any()) } returns SampleHeader("My header")

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessMyHeaderMock,
            onErrorMock,
            mapper
        )

        verify {
            onSuccessMyHeaderMock(any()) wasNot Called
        }

        verify {
            mapper(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(GenericErrorException::class))
        }
    }

    @Test
    fun whenUsedMapperIsUnableToParseJSONObjectThenCheckUnableToUseProvidedMapperExceptionIsThrown() {
        generateContentChefMocksBasedOn(ContentChefEnvironment.LIVE)
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE

        val onSuccessMyHeaderMock = mockk<((ContentChefItemResponse<SampleHeader>) -> Unit)>()

        every { onSuccessMyHeaderMock(any()) } just Runs

        val mapper = mockk<((JSONObject) -> SampleHeader)>()
        every { mapper(any()) } throws JSONException("")

        onlineChannel.getContent(
            OnlineContentRequestData("testPublicId"),
            onSuccessMyHeaderMock,
            onErrorMock,
            mapper
        )

        verify {
            onSuccessMyHeaderMock(any()) wasNot Called
        }

        verify {
            mapper(any()) wasNot Called
        }

        verify(exactly = 1) {
            onErrorMock(ofType(UnableToUseProvidedMapperException::class))
        }
    }

    /**
     * Custom [ContentChefItemResponse] matcher to allow correct [JSONObject] comparison
     */
    private fun assertContentChefResponseWithJsonObjectIsEqualsTo(
        expected: ContentChefItemResponse<JSONObject>,
        actual: ContentChefItemResponse<JSONObject>
    ) {
        assertEquals(expected.publicId, actual.publicId)
        assertEquals(expected.definition, actual.definition)
        assertEquals(expected.repository, actual.repository)
        assertEquals(expected.payload.toString(), actual.payload.toString())
        assertEquals(expected.onlineDate, actual.onlineDate)
        assertEquals(expected.offlineDate, actual.offlineDate)
        assertEquals(expected.metadata, actual.metadata)
        assertEquals(expected.requestContext, actual.requestContext)
    }
}