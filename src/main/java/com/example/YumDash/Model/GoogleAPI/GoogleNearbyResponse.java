package com.example.YumDash.Model.GoogleAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class GoogleNearbyResponse {
    @JsonProperty("results")
    List<GoogleResult> googleResultList;
}
