package ru.sfedu.hospitalityNetwork.filter;

import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.model.Offer;

public class FilterByCountry extends Filter {

    String country;

    public FilterByCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean valid(Offer offer) {
        if (offer.getCountry().equals(country)) {
            return true;
        }
        return false;
    }
}
