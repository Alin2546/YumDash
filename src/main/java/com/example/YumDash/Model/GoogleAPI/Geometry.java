package com.example.YumDash.Model.GoogleAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Geometry {
    @JsonProperty("location")
    private Location location;
}
