package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "OfferGuest")
public class OfferGuest extends Offer {

    @CsvBindByName
    private int weightBaggage;
    @CsvBindByName
    private int numberDay;

    @Attribute(name = "weightBaggage")
    public int getWeightBaggage() {
        return weightBaggage;
    }

    @Attribute(name = "weightBaggage")
    public void setWeightBaggage(int weightBaggage) {
        this.weightBaggage = weightBaggage;
    }

    @Attribute(name = "numberDay")
    public int getNumberDay() {
        return numberDay;
    }

    @Attribute(name = "numberDay")
    public void setNumberDay(int numberDay) {
        this.numberDay = numberDay;
    }

    @Override
    public String toString() {
        return "OfferGuest{" +
                "idOffer='" + getIdOffer() + '\'' +
                ", listResponse=" + getListResponse() +
                ", idUser='" + getIdUser() + '\'' +
                ", name='" + getName() + '\'' +
                ", country='" + getCountry() + '\'' +
                ", city='" + getCity() + '\'' +
                ", typeOffer=" + isTypeOffer() +
                "weightBaggage=" + weightBaggage +
                ", numberDay=" + numberDay +
                '}';
    }
}
