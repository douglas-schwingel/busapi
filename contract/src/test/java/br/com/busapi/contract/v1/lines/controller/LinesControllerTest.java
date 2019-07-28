package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class LinesControllerTest {

    @Test
    public void mustReturnAllLines() {
        JsonPath jsonPath = given().header("Accept", "application/json")
                .get("/v1/lines")
                .andReturn()
                .jsonPath();

        List<BusLineResponse> content = jsonPath.getObject("content", new TypeRef<List<BusLineResponse>>() {
        });

        assertEquals(990, content.size());
    }


}