package teamproject.lam_server.controller.document.api;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import teamproject.lam_server.controller.ApiDocsTest;
import teamproject.lam_server.domain.city.constants.CityName;
import teamproject.lam_server.init.service.InitCityService;

import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static teamproject.lam_server.global.enumMapper.EnumClassConst.*;
import static teamproject.lam_server.utils.ApiDocumentUtils.*;
import static teamproject.lam_server.utils.DocsLinkGenerator.generateLinkCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CityApiDocsTest extends ApiDocsTest {
    static final String BASIC_URL = "/api/v1/city";
    @Autowired
    private InitCityService initCityService;

    private CityName cityName;

    @BeforeAll
    public void setUp() {
        initCityService.initCityIntroData();
        initCityService.initCityTransportData();
        initCityService.initCityWeatherData();
    }

    @BeforeEach
    public void setUpCityName() {
        cityName = CityName.values()[(new Random()).nextInt(CityName.values().length)];
    }

    @Test
    @DisplayName("도시 요약 정보 조회")
    void get_city_summary() throws Exception {
        // expected
        this.mockMvc.perform(get(BASIC_URL+"/grid-infos")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("city-summary-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(beneathPath("data[]").withSubsectionId("data"),
                                enumCodeFieldWithPath("name", CITY_NAME),
                                enumValueFieldWithPath("name", CITY_NAME),
                                fieldWithPath("image").type(STRING).description("이미지 주소(uri)"),
                                fieldWithPath("averageDegree").type(NUMBER).description("평균 기온"),
                                fieldWithPath("transportScore").type(NUMBER).description("교통 점수")
                        )
                ));
    }

    @Test
    @DisplayName("도시 소개 정보 조회")
    void get_city_intro() throws Exception {
        // expected
        this.mockMvc.perform(get(BASIC_URL+"/{cityName}", cityName)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("city-intro-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("cityName").description(generateLinkCode(CITY_NAME))
                        ),
                        responseFields(
                                beneathPath("data.FOOD[]").withSubsectionId("image-content"),
                                fieldWithPath("content").type(STRING).description("이미지 설명"),
                                fieldWithPath("image").type(STRING).description("이미지(uri)")
                        )
                ));
    }

    @Test
    @DisplayName("도시 추가 정보 조회")
    void get_extra_city_info() throws Exception {
        // expected
        this.mockMvc.perform(get(BASIC_URL+"/{cityName}/extra", cityName)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("city-extra-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("cityName").description(generateLinkCode(CITY_NAME))
                        ),
                        responseFields(
                                beneathPath("data.transports[]").withSubsectionId("transport"),
                                idFieldWithPath("transport id"),
                                enumCodeFieldWithPath("name", CITY_NAME),
                                enumValueFieldWithPath("name", CITY_NAME),
                                enumCodeFieldWithPath("category", TRANSPORT_CATEGORY),
                                enumValueFieldWithPath("category", TRANSPORT_CATEGORY),
                                fieldWithPath("stationCount").type(NUMBER).description("개수"),
                                fieldWithPath("score").type(NUMBER).description("점수")
                        ),
                        responseFields(
                                beneathPath("data.weathers[]").withSubsectionId("weather"),
                                idFieldWithPath("weather id"),
                                enumCodeFieldWithPath("name", CITY_NAME),
                                enumValueFieldWithPath("name", CITY_NAME),
                                enumCodeFieldWithPath("month", MONTH_CATEGORY),
                                enumValueFieldWithPath("month", MONTH_CATEGORY),
                                fieldWithPath("maxDegree").type(NUMBER).description("최고 기온"),
                                fieldWithPath("minDegree").type(NUMBER).description("최저 기온"),
                                fieldWithPath("averageDegree").type(NUMBER).description("평균 기온")
                        )

                ));

    }
}


