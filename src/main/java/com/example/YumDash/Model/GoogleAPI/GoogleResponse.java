package com.example.YumDash.Model.GoogleAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


import java.util.List;
@Getter

public class GoogleResponse {
    @JsonProperty("results")
    private List<Address> results;

}
