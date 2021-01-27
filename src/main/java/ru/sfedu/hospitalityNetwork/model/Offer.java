package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import ru.sfedu.hospitalityNetwork.dataConvertors.ResponseListConverter;

import java.io.Serializable;
import java.util.List;

@Root(name = "Offer")
public class Offer implements Serializable {

    @CsvBindByName
    private String idOffer;
    @CsvCustomBindByName(converter = ResponseListConverter.class)
    private List<Response> listResponse;
    @CsvBindByName
    private String idUser;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String country;
    @CsvBindByName
    private String city;
    @CsvBindByName
    private boolean typeOffer;


    @Attribute(name = "idOffer")
    public String getIdOffer() {
        return idOffer;
    }

    @Attribute(name = "idOffer")
    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    @ElementList(name = "listResponse")
    public List<Response> getListResponse() {
        return listResponse;
    }

    @ElementList(name = "listResponse")
    public void setListResponse(List<Response> listResponse) {
        this.listResponse = listResponse;
    }

    @Attribute(name = "idUser")
    public String getIdUser() {
        return idUser;
    }

    @Attribute(name = "idUser")
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Attribute(name = "name")
    public String getName() {
        return name;
    }

    @Attribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @Attribute(name = "country")
    public String getCountry() {
        return country;
    }

    @Attribute(name = "country")
    public void setCountry(String country) {
        this.country = country;
    }

    @Attribute(name = "city")
    public String getCity() {
        return city;
    }

    @Attribute(name = "city")
    public void setCity(String city) {
        this.city = city;
    }

    @Attribute(name = "typeOffer")
    public boolean isTypeOffer() {
        return typeOffer;
    }

    @Attribute(name = "typeOffer")
    public void setTypeOffer(boolean typeOffer) {
        this.typeOffer = typeOffer;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "idOffer='" + idOffer + '\'' +
                ", listResponse=" + listResponse +
                ", idUser='" + idUser + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", typeOffer=" + typeOffer +
                '}';
    }

}
