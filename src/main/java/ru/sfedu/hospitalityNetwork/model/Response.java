package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "Response")
public class Response implements Serializable {

    @CsvBindByName
    private String idResponse;
    @CsvBindByName
    private String idUser;
    @CsvBindByName
    private String idOffer;

    @Attribute(name = "idResponse")
    public String getIdResponse() {
        return idResponse;
    }

    @Attribute(name = "idResponse")
    public void setIdResponse(String idResponse) {
        this.idResponse = idResponse;
    }

    @Attribute(name = "idUser")
    public String getIdUser() {
        return idUser;
    }

    @Attribute(name = "idUser")
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Attribute(name = "idOffer")
    public String getIdOffer() {
        return idOffer;
    }

    @Attribute(name = "idOffer")
    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    @Override
    public String toString() {
        return "Response{" +
                "idResponse='" + idResponse + '\'' +
                ", idUser='" + idUser + '\'' +
                ", idOffer='" + idOffer + '\'' +
                '}';
    }
}
