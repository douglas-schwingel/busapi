package br.com.busapi.contract.v1.lines.models.response;

import lombok.*;

import java.util.List;

@Data
@Builder
public class ListBusLinesResponse {

    @Singular
    private List<BusLineResponse> lines;
}
