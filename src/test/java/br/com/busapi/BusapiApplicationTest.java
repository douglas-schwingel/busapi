package br.com.busapi;

import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.impl.lines.models.Line;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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

    @LocalServerPort
    private int springPort;

    @Before
    public void setUp() {
        RestAssured.port = this.springPort;
    }

    @Test
    public void mustReturnAllLines() {
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get("/v1/lines")
                .andReturn()
                    .jsonPath();

        List<BusLineResponse> list = jsonPath.getObject("content", new TypeRef<List<BusLineResponse>>() {
        });

        assertTrue(list.size() > 980);
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
                    .get("/v1/lines")
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
        int reqPageNumber = 90;
        JsonPath jsonPath =
                given()
                    .header(ACCEPT, JSON)
                    .param("page", reqPageNumber)
                    .param("size", 15)
                .expect()
                    .statusCode(400)
                .when()
                    .get("/v1/lines")
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject("errors[0].message", String.class);
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
                    .get("/v1/lines/")
                .andReturn()
                    .jsonPath();

        String message = jsonPath.getObject("errors[0].message", String.class);

        assertTrue(message.contains("Invalid parameters"));
        assertTrue(message.contains("page - Value: " + page));
    }

    @Test
    public void mustSaveTheNewBusLineAndThenDelete() {
        int lineId = 1093;
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
                    .post("/v1/lines/")
                .thenReturn()
                    .jsonPath();

        Line returned = jsonPath.getObject("$", Line.class);
        assertEquals(lineRequest.getNome(), returned.getName());

        given()
        .expect()
            .statusCode(204)
        .when()
            .delete("/v1/lines/" + lineId);
    }

    @Test
    public void mustReturnBadRequestForInvalidId() {
        JsonPath jsonPath =
                given()
                .expect()
                    .statusCode(400)
                .when()
                    .delete("/v1/lines/" + 1093)
                .thenReturn()
                    .jsonPath();

        String message = jsonPath.getObject("errors[0].message", String.class);
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

        String message = jsonPath.getObject("errors[0].message", String.class);
        assertEquals("Not Found", message);
    }


}
