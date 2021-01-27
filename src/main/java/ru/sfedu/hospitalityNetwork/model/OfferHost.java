package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import ru.sfedu.hospitalityNetwork.enums.HouseType;

@Root(name = "OfferHost")
public class OfferHost extends Offer{

    @CsvBindByName
    private HouseType houseType;
    @CsvBindByName
    private boolean personalMeeting;
    @CsvBindByName
    private String addressHouse;

    @Attribute(name = "houseType")
    public HouseType getHouseType() {
        return houseType;
    }

    @Attribute(name = "houseType")
    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    @Attribute(name = "personalMeeting")
    public boolean isPersonalMeeting() {
        return personalMeeting;
    }

    @Attribute(name = "personalMeeting")
    public void setPersonalMeeting(boolean personalMeeting) {
        this.personalMeeting = personalMeeting;
    }

    @Attribute(name = "addressHouse")
    public String getAddressHouse() {
        return addressHouse;
    }

    @Attribute(name = "addressHouse")
    public void setAddressHouse(String addressHouse) {
        this.addressHouse = addressHouse;
    }

    @Override
    public String toString() {
        return "OfferHost{" +
                "idOffer='" + getIdOffer() + '\'' +
                ", listResponse=" + getListResponse() +
                ", idUser='" + getIdUser() + '\'' +
                ", name='" + getName() + '\'' +
                ", country='" + getCountry() + '\'' +
                ", city='" + getCity() + '\'' +
                ", typeOffer=" + isTypeOffer() +
                "houseType=" + houseType +
                ", personalMeeting=" + personalMeeting +
                ", addressHouse='" + addressHouse + '\'' +
                '}';
    }
}