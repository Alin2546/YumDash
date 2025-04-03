package com.example.YumDash.Controller;


import com.example.YumDash.Service.FoodService;
import com.example.YumDash.Service.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {



    private final GoogleMapsService googleMapsService;
    private final FoodService foodService;


    @GetMapping
    public String homePage(Model model) {
        return "home";
    }

    @GetMapping("/chooseAddress")
    public String getAddresses(@RequestParam String address, Model model) {
        if (address != null && !address.isEmpty()) {
            List<String> addresses = googleMapsService.getFormattedAddress(address);
            model.addAttribute("addressSuggestions", addresses);
        }
        return "AddressChooser";
    }


}

