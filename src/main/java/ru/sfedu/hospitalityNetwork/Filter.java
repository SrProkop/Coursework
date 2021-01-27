package ru.sfedu.hospitalityNetwork;

import ru.sfedu.hospitalityNetwork.model.Offer;

abstract public class Filter {
    abstract public boolean valid(Offer offer);
}
