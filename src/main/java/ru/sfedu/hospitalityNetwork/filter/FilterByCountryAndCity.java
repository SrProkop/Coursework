package ru.sfedu.hospitalityNetwork.filter;

import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.model.Offer;

public class FilterByCountryAndCity extends Filter {
    String country;
    String city;

    public FilterByCountryAndCity(String country, String city) {
        this.country = country;
        this.city = city;
    }

    @Override
    public boolean valid(Offer offer) {
        if (offer.getCountry().equals(country) && offer.getCity().equals(city)) {
            return true;
        }
        return false;
    }
}