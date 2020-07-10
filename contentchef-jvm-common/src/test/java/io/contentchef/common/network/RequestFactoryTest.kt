package io.contentchef.common.network

import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefSearchResponse
import io.contentchef.common.exception.*
import io.contentchef.common.log.Logger
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class RequestFactoryTest {

    @MockK
    lateinit var connectionFactoryMock: ConnectionFactory
    @MockK
    lateinit var connectionStreamReaderMock: ConnectionStreamReader
    @MockK
    lateinit var loggerMock: Logger
    @MockK
    lateinit var httpURLConnectionMock: HttpURLConnection
    @MockK
    lateinit var onItemSuccessMock: ((ContentChefItemResponse<JSONObject>) -> Unit)
    @MockK
    lateinit var onSearchSuccessMock: ((ContentChefSearchResponse<JSONObject>) -> Unit)
    @MockK
    lateinit var onErrorMock: ((Throwable) -> Unit)

    private lateinit var requestFactory: RequestFactory

    companion object {
        const val VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE =
            "{\"publicId\":\"my-header\",\"definition\":\"default-header\",\"repository\":\"defaultRepository\",\"payload\":{\"header\":\"My header\"},\"onlineDate\":\"2019-10-16T02:06:00.000Z\",\"offlineDate\":\"2019-10-31T22:59:00.000Z\",\"metadata\":{\"id\":7,\"authoringContentId\":35,\"contentVersion\":3,\"contentLastModifiedDate\":\"2019-10-17T00:00:00.000Z\",\"tags\":[\"tag\",\"anothertag\"],\"publishedOn\":\"2019-10-17T10:10:59.835Z\"},\"requestContext\":{\"publishingChannel\":\"depc\",\"cloudName\":\"contentchef\",\"timestamp\":\"2019-10-18T12:49:45.700Z\"}}\n"
        const val VALID_ONLINE_SEARCH_CHEF_EXAMPLE_RESPONSE =
            "{\"items\":[{\"publicId\":\"my-header-2\",\"definition\":\"default-header\",\"repository\":\"defaultRepository\",\"payload\":{\"header\":\"my-header-2\"},\"onlineDate\":null,\"offlineDate\":null,\"metadata\":{\"id\":15,\"authoringContentId\":36,\"contentVersion\":2,\"contentLastModifiedDate\":\"2019-11-18T00:00:00.000Z\",\"publishedOn\":\"2019-11-18T11:06:25.889Z\",\"tags\":[]},\"requestContext\":{\"publishingChannel\":\"depc\",\"cloudName\":\"contentchef\",\"timestamp\":\"2019-11-18T11:06:54.203Z\"}},{\"publicId\":\"new-simple-header\",\"definition\":\"default-header\",\"repository\":\"defaultRepository\",\"payload\":{\"header\":\"New simple header\"},\"onlineDate\":null,\"offlineDate\":null,\"metadata\":{\"id\":4,\"authoringContentId\":31,\"contentVersion\":2,\"contentLastModifiedDate\":\"2019-10-10T00:00:00.000Z\",\"publishedOn\":\"2019-10-10T08:36:11.886Z\",\"tags\":[]},\"requestContext\":{\"publishingChannel\":\"depc\",\"cloudName\":\"contentchef\",\"timestamp\":\"2019-11-18T11:06:54.203Z\"}}],\"total\":2,\"skip\":0,\"take\":10,\"requestContext\":{\"publishingChannel\":\"depc\",\"cloudName\":\"contentchef\",\"timestamp\":\"2019-11-18T11:06:54.203Z\"}}"
        const val INVALID_JSON_CHEF_EXAMPLE_RESPONSE = "NOT_VALID_JSON_RESPONSE"
        const val INVALID_SPACE_ID_LENGTH_400_CHEF_EXAMPLE_RESPONSE =
            "{\"type\":\"ValidationError\",\"message\":\"Input data failed to pass validation\",\"input\":{\"spaceId\":\"dd\",\"publishingChannel\":\"depc\",\"filters\":{\"publicId\":\"prova\",\"legacyMetadata\":false,\"liveContents\":true}},\"validationsErrors\":[{\"value\":\"dd\",\"property\":\"spaceId\",\"children\":[],\"constraints\":{\"minLength\":\"spaceId must be longer than or equal to 4 characters\"}}]}"
        const val INVALID_TARGET_DATE_400_CHEF_EXAMPLE_RESPONSE =
            "{\"type\":\"ValidationError\",\"message\":\"Input data failed to pass validation\",\"input\":{\"spaceId\":\"website1-f16d\",\"targetDate\":\"\",\"publishingChannel\":\"depc\",\"filters\":{\"publicId\":\"x1x\",\"legacyMetadata\":false,\"liveContents\":true}},\"validationsErrors\":[{\"value\":\"\",\"property\":\"targetDate\",\"children\":[],\"constraints\":{\"isIso8601\":\"targetDate must be a valid ISO 8601 date string\",\"isDateString\":\"targetDate must be a ISOString\"}}]}"
        const val INVALID_TAKE_MIN_VALUE_400_CHEF_EXAMPLE_RESPONSE =
            "{\"type\":\"ValidationError\",\"message\":\"Input data failed to pass validation\",\"input\":{\"spaceId\":\"website1-f16d\",\"publishingChannel\":\"depc\",\"filters\":{\"legacyMetadata\":false,\"tags\":[\"prova\"],\"liveContents\":true,\"skip\":0,\"take\":0}},\"validationsErrors\":[{\"value\":{\"legacyMetadata\":false,\"tags\":[\"prova\"],\"liveContents\":true,\"skip\":0,\"take\":0},\"property\":\"filters\",\"children\":[{\"value\":0,\"property\":\"take\",\"children\":[],\"constraints\":{\"min\":\"take must not be less than 1\"}}]}]}"
        const val MISSING_AUTHENTICATION_TOKEN_403_CHEF_EXAMPLE_RESPONSE =
            "{\"message\":\"Missing Authentication Token\"}"
        const val GENERIC_ERROR_500_CHEF_EXAMPLE_RESPONSE =
            "{\"message\":\"Internal server error\"}"
    }

    @Before
    fun before() {
        MockKAnnotations.init(this)

        requestFactory = RequestFactory(
            ContentChefResponseMapper,
            connectionFactoryMock,
            connectionStreamReaderMock,
            loggerMock
        )

        every { httpURLConnectionMock.disconnect() } just Runs
        every { httpURLConnectionMock.connectTimeout = any() } just Runs
        every { httpURLConnectionMock.readTimeout = any() } just Runs
        every { httpURLConnectionMock.connect() } just Runs

        every { connectionFactoryMock.getConnection(any()) } returns httpURLConnectionMock

        every { onItemSuccessMock(any()) } just Runs
        every { onSearchSuccessMock(any()) } just Runs
        every { onErrorMock(any()) } just Runs

        every { loggerMock.log(any(), any(), any()) } just Runs

    }

    @After
    fun after() {
        clearAllMocks()
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs404ThanCheckContentNotFoundExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 404
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(ofType(ContentNotFoundException::class))
        }
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs500ThanCheckGenericErrorExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 500
        every { connectionStreamReaderMock.getContentAsString(any()) } returns GENERIC_ERROR_500_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                GenericErrorException(
                    ContentChefErrorResponse(
                        ErrorType.GENERIC,
                        "Internal server error",
                        emptyList()
                    )
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs400BecauseOfSpaceIdLengthThanCheckBadRequestExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 400
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_SPACE_ID_LENGTH_400_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                BadRequestException(
                    ContentChefErrorResponse(
                        ErrorType.VALIDATION, "Input data failed to pass validation",
                        listOf("spaceId must be longer than or equal to 4 characters")
                    )
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs400BecauseOfInvalidTargetDateThanCheckBadRequestExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 400
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_TARGET_DATE_400_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                BadRequestException(
                    ContentChefErrorResponse(
                        ErrorType.VALIDATION, "Input data failed to pass validation",
                        listOf(
                            "targetDate must be a ISOString",
                            "targetDate must be a valid ISO 8601 date string"
                        )
                    )
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs400BecauseOfInvalidTakeMinValueThanCheckBadRequestExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 400
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_TAKE_MIN_VALUE_400_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                BadRequestException(
                    ContentChefErrorResponse(
                        ErrorType.VALIDATION, "Input data failed to pass validation",
                        listOf("take must not be less than 1")
                    )
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndErrorIs403BecauseOfMissingAuthenticationTokenThanCheckGenericErrorExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 403
        every { connectionStreamReaderMock.getContentAsString(any()) } returns MISSING_AUTHENTICATION_TOKEN_403_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                GenericErrorException(
                    ContentChefErrorResponse(
                        ErrorType.GENERIC, "Missing Authentication Token",
                        emptyList()
                    )
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndContentIsWrongThanCheckInvalidResponseExceptionIsThrown() {
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns INVALID_JSON_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 1) {
            onErrorMock(
                InvalidResponseException(
                    INVALID_JSON_CHEF_EXAMPLE_RESPONSE
                )
            )
        }
    }

    @Test
    fun whenDoingNetworkRequestAndContentIsItemAndIsCorrectThanCheckItemCallbackIsCalled() {
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_CONTENT_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 1) {
            onItemSuccessMock(any())
        }

        verify(exactly = 0) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 0) {
            onErrorMock(any())
        }
    }

    @Test
    fun whenDoingNetworkRequestAndContentIsSearchAndIsCorrectThanCheckSearchCallbackIsCalled() {
        every { httpURLConnectionMock.responseCode } returns 200
        every { connectionStreamReaderMock.getContentAsString(any()) } returns VALID_ONLINE_SEARCH_CHEF_EXAMPLE_RESPONSE

        val request = requestFactory.generateRequest(
            ContentChefRequestData("", HttpMethod.GET, emptyMap(), ""),
            onItemSuccessMock,
            onSearchSuccessMock,
            onErrorMock
        )

        request()

        verify(exactly = 0) {
            onItemSuccessMock(any())
        }

        verify(exactly = 1) {
            onSearchSuccessMock(any())
        }

        verify(exactly = 0) {
            onErrorMock(any())
        }
    }
}