package br.com.busapi;

import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLinetinerary;
import br.com.busapi.impl.lines.models.Line;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:application-test.properties")
public class BusapiApplicationTest {

    private static final String ACCEPT = "Accept";
    private static final String JSON = "application/json";
    private static final String BASE_RESOURCE = "line-service/v1/lines";
    private static final String MESSAGE_PATH = "errors[0].message";
    private static boolean first = true;

    @LocalServerPort
    private int springPort;

    @Before
    public void setUp() throws IOException {
        RestAssured.port = this.springPort;
        if(first) {
            String ip = "localhost";
            int port = 37017;

            MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(ip, port), "test");
            File file = new ClassPathResource("prefill.json").getFile();

            List<Line> lines = new ObjectMapper().readValue(file, new TypeReference<List<Line>>() {
            });
            mongoTemplate.insertAll(lines);
            first = false;
        }
    }

    @Test
    public void mustReturnAllLines() {
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get(BASE_RESOURCE)
                .andReturn()
                    .jsonPath();

        List<BusLineResponse> list = jsonPath.getObject("content", new TypeRef<List<BusLineResponse>>() {
        });

        assertTrue(list.size() >= 15);
    }

    @Test
    public void mustReturnPage0With15Lines() {
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .param("page", 0)
                    .param("size", 15)
                .expect()
                    .statusCode(200)
                .when()
                    .get(BASE_RESOURCE)
                .andReturn()
                    .jsonPath();

        List<BusLineResponse> list = jsonPath.getObject("content", new TypeRef<List<BusLineResponse>>() {
        });
        Integer page = jsonPath.getObject("number", Integer.class);


        assertEquals(15, list.size());
        assertEquals(0, page, 0.1);
    }

    @Test
    public void mustReturnBadRequestFor90Pages() {
        Integer reqPageNumber = 90;
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .param("page", reqPageNumber)
                    .param("size", 15)
                .expect()
                    .statusCode(400)
                .when()
                    .get(BASE_RESOURCE)
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        String name = jsonPath.getObject("errors[0].name", String.class);
        assertTrue(message.contains("page(" + reqPageNumber + ")"));
        assertEquals("BAD_REQUEST", name);
    }

    @Test
    public void mustThrowExceptionWithBadRequestForInvalidParameters() {
        String page = "asdf";
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .param("page", page)
                .expect()
                    .statusCode(400)
                .when()
                    .get(BASE_RESOURCE)
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject(MESSAGE_PATH, String.class);

        assertTrue(message.contains("Invalid parameters"));
        assertTrue(message.contains("page - Value: " + page));
    }

    @Test
    public void mustSaveTheNewBusLineAndThenDelete() {
        Integer lineId = 1093;
        LineRequest lineRequest = LineRequest.builder()
                .id(lineId)
                .codigo("109-3")
                .nome("VIAMAO")
                .coordenada(new Double[]{-30.1, -51.1})
                .build();

        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .contentType(JSON)
                    .body(lineRequest)
                .expect()
                    .statusCode(201)
                .when()
                    .post(BASE_RESOURCE)
                .thenReturn()
                    .jsonPath();

        Line response = jsonPath.getObject("$", Line.class);
        assertEquals(lineRequest.getNome(), response.getName());
        assertEquals(lineId, response.getId());

        given()
        .expect()
            .statusCode(204)
        .when()
            .delete(BASE_RESOURCE + "/" + lineId);
    }

    @Test
    public void mustReturnBadRequestForInvalidId() {
        JsonPath jsonPath =
                given()
                .expect()
                    .statusCode(400)
                .when()
                    .delete(BASE_RESOURCE + "/" + 1093)
                .thenReturn()
                    .jsonPath();

        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        assertEquals("No line with the id 1093", message);
    }

    @Test
    public void mustReturnNotFoundForInvalidURI() {
        JsonPath jsonPath =
                given()
                .expect()
                    .statusCode(404)
                .when()
                    .get("/v1/not_existing_URI")
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        assertEquals("Not Found", message);
    }

    @Test
    public void mustReturnAllBusesNear() {
        JsonPath jsonPath =
                    given()
                        .header(ACCEPT, JSON)
                        .param("distance", 0.005)
                        .param("lat", -30.146200568328)
                        .param("lng", -51.214993133554)
                    .expect()
                        .statusCode(200)
                    .when()
                        .get(BASE_RESOURCE + "/find_near")
                    .andReturn()
                        .jsonPath();

        List<BusLineResponse> lines = jsonPath.getObject("lines", new TypeRef<List<BusLineResponse>>() {
        });

        assertEquals(3, lines.size());
    }

    @Test
    public void mustThrowBadRequestForInvalidLat() {

        String lat = "invalid";
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .param("distance", 0.005)
                    .param("lat", lat)
                    .param("lng", -51.214993133554)
                .expect()
                    .statusCode(400)
                .when()
                    .get(BASE_RESOURCE + "/find_near")
                .andReturn()
                    .jsonPath();
        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        assertTrue(message.contains("Value: " + lat));
    }

    @Test
    public void mustThrowExceptionForFindNearWithoutParameters() {
        given()
            .header(ACCEPT, JSON)
        .expect()
            .statusCode(500)
        .when()
            .get(BASE_RESOURCE + "/find_near")
        .andReturn()
            .jsonPath();
    }

    @Test
    public void mustReturnMethodNotAllowedForPostInFindNear() {
        given()
            .header(ACCEPT, JSON)
        .expect()
            .statusCode(405)
        .when()
            .post(BASE_RESOURCE + "/find_near")
        .andReturn()
            .jsonPath();
    }

    @Test
    public void mustUpdateLineSuccessfully() {
        Integer lineId = 1704;
        LineRequest lineRequest = LineRequest.builder()
                .id(lineId)
                .codigo("M170-4")
                .nome("VIAMAO")
                .coordenada(new Double[]{-30.146200568328, -51.214993133554})
                .build();

        JsonPath jsonPath = given()
                            .header(ACCEPT, JSON)
                            .contentType(JSON)
                            .body(lineRequest)
                        .expect()
                            .statusCode(200)
                        .when()
                            .put(BASE_RESOURCE)
                        .andReturn()
                            .jsonPath();

        Line response = jsonPath.getObject("$", Line.class);
        assertEquals(lineId, response.getId());
    }

    @Test
    public void mustReturnACompleteLineWithTheGivenId() {
        Integer lineId = 5586;
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get(BASE_RESOURCE + "/" + lineId)
                .andReturn()
                    .jsonPath();

        BusLinetinerary response = jsonPath.getObject("$", BusLinetinerary.class);
        assertEquals(lineId, response.getId());
        assertEquals("AGRONOMIA-UFRGS", response.getName());
    }

    @Test
    public void shouldReturnBadRequestForNotExistingId() {
        Integer lineId = 1074;
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                .expect()
                    .statusCode(400)
                .when()
                    .get(BASE_RESOURCE + "/" + lineId)
                .andReturn()
                    .jsonPath();
        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        assertEquals("No registered line with id: " + lineId, message);
    }

    @Test
    public void mustReturnBadRequestWhenTryingToDeleteNotExistingId() {
        Integer lineId = 1074;
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                .expect()
                    .statusCode(400)
                .when()
                    .delete(BASE_RESOURCE + "/" + lineId)
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject(MESSAGE_PATH, String.class);
        assertEquals("No line with the id " + lineId, message);
    }



}
