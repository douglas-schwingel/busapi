package br.com.busapi.impl.lines.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coordinate {

    private Double lat;
    private Double lng;
}
