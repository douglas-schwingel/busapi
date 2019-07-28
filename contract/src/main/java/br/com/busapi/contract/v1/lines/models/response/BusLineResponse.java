package br.com.busapi.contract.v1.lines.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusLineResponse {

    private Integer id;
    @JsonProperty("codigo")
    private String code;
    @JsonProperty("nome")
    private String name;
}
