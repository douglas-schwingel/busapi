package br.com.busapi.impl.lines.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Coordinate {

    private Double lat;
    private Double lng;
}
