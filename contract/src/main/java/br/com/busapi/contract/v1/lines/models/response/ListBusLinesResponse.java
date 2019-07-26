package br.com.busapi.contract.v1.lines.models.response;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ListBusLinesResponse {

    @Singular
    private List<BusLineResponse> lines;
}
