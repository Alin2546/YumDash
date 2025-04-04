package com.example.YumDash.Service;

import com.example.YumDash.Model.GoogleAPI.Address;
import com.example.YumDash.Model.GoogleAPI.GoogleNearbyResponse;
import com.example.YumDash.Model.GoogleAPI.GoogleResponse;
import com.example.YumDash.Model.GoogleAPI.GoogleResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Value("${google.maps.api.url}")
    private String apiUrl;


    public List<String> getFormattedAddress(String address) {
        String requestUrl = apiUrl + "?address=" + address.replace(" ", "+") + "&key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleResponse> responseEntity =
                restTemplate.getForEntity(requestUrl, GoogleResponse.class);
        return responseEntity.getBody().getResults().stream()
                .map(Address::getFormattedAddress)
                .toList();
    }


    public double[] getCoordinates(String address) {
        String url = apiUrl + "?address=" + address.replace(" ", "+") + "&key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GoogleNearbyResponse> responseEntity =
                restTemplate.getForEntity(url, GoogleNearbyResponse.class);

        GoogleNearbyResponse response = responseEntity.getBody();
        if (response != null && !response.getGoogleResultList().isEmpty()) {
            GoogleResult result = response.getGoogleResultList().get(0);
            return new double[]{result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng()};
        }else {
            System.out.println("No results found or invalid response");
        }

        return null;
    }
}
