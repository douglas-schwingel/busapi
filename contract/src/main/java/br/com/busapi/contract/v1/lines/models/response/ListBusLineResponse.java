package br.com.busapi.contract.v1.lines.models.response;

import lombok.*;

import java.util.List;

@Data
@Builder
public class ListBusLineResponse {

    @Singular
    private List<BusLineResponse> lines;
}
