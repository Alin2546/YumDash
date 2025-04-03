package com.example.YumDash.Model.GoogleAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    @JsonProperty("formatted_address")
    private String formattedAddress;
}
