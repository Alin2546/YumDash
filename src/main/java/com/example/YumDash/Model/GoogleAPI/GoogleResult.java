package com.example.YumDash.Model.GoogleAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class GoogleResult {
    @JsonProperty("geometry")
    private Geometry geometry;


}


