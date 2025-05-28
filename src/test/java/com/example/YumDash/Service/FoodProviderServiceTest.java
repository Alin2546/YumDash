package com.example.YumDash.Service;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodProviderServiceTest {

    @Mock
    private GoogleMapsService googleMapsService;

    @Mock
    private FoodProviderRepo foodProviderRepo;

    @InjectMocks
    private FoodProviderService foodProviderService;

    private FoodProvider p1;
    private FoodProvider p2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        p1 = new FoodProvider();
        p1.setName("Restaurant A");
        p1.setLatitude(47.1585);
        p1.setLongitude(27.6014);

        p2 = new FoodProvider();
        p2.setName("Restaurant B");
        p2.setLatitude(47.1600);
        p2.setLongitude(27.6000);
    }


    @Test
    void testGetNearbyRestaurants_NoInput() {
        List<FoodProvider> result = foodProviderService.getNearbyRestaurants(null);
        assertNull(result);
        verifyNoInteractions(googleMapsService, foodProviderRepo);
    }

    @Test
    void testGetNearbyRestaurants_OneInput() {
        String address = "Iași, Palas Mall";

        when(googleMapsService.getCoordinates(address)).thenReturn(new double[]{47.1585, 27.6014});
        when(foodProviderRepo.findAll()).thenReturn(List.of(p1, p2));

        List<FoodProvider> result = foodProviderService.getNearbyRestaurants(address);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        verify(googleMapsService).getCoordinates(address);
        verify(foodProviderRepo).findAll();
    }

    @Test
    void testGetNearbyRestaurants_ManyInputs() {

        String address = "Iași, Palas Mall";

        when(googleMapsService.getCoordinates(address)).thenReturn(new double[]{47.1585, 27.6014});
        when(foodProviderRepo.findAll()).thenReturn(List.of(p1, p2));

        List<FoodProvider> result = foodProviderService.getNearbyRestaurants(address);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetNearbyRestaurants_WrongInput() {
        String address = "planet Marte";

        when(googleMapsService.getCoordinates(address)).thenReturn(null);

        List<FoodProvider> result = foodProviderService.getNearbyRestaurants(address);

        assertNull(result);

        verify(googleMapsService).getCoordinates(address);
        verifyNoInteractions(foodProviderRepo);
    }


    @Test
    void testIsWithinDeliveryRange_NoInput() {
        boolean result = foodProviderService.isWithinDeliveryRange(null, null);
        assertFalse(result);
        verifyNoInteractions(googleMapsService);
    }

    @Test
    void testIsWithinDeliveryRange_OneInput() {
        String address = "Iași, Palas Mall";

        when(googleMapsService.getCoordinates(address)).thenReturn(new double[]{47.1585, 27.6014});

        boolean result = foodProviderService.isWithinDeliveryRange(p1, address);
        assertTrue(result);

        verify(googleMapsService).getCoordinates(address);
    }

    @Test
    void testIsWithinDeliveryRange_ManyInputs() {
        String address = "Iași, Palas Mall";

        when(googleMapsService.getCoordinates(address)).thenReturn(new double[]{47.1585, 27.6014});

        boolean result1 = foodProviderService.isWithinDeliveryRange(p1, address);
        boolean result2 = foodProviderService.isWithinDeliveryRange(p2, address);

        assertTrue(result1);
        assertTrue(result2);

        verify(googleMapsService, times(2)).getCoordinates(address);
    }

    @Test
    void testIsWithinDeliveryRange_WrongInput() {
        String address = "narnia";

        when(googleMapsService.getCoordinates(address)).thenReturn(null);

        boolean result = foodProviderService.isWithinDeliveryRange(p1, address);
        assertFalse(result);

        verify(googleMapsService).getCoordinates(address);
    }

}
