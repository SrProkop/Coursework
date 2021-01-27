package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import ru.sfedu.hospitalityNetwork.dataConvertors.CommentListConverter;
import ru.sfedu.hospitalityNetwork.dataConvertors.OfferConverter;

import java.io.Serializable;
import java.util.List;

@Root(name = "User")
public class User implements Serializable{

    @CsvBindByName
    private String idUser;
    @CsvCustomBindByName(converter = OfferConverter.class)
    private Offer offer;
    @CsvCustomBindByName(converter = CommentListConverter.class)
    private List<Comment> listCommentForUser;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String country;
    @CsvBindByName
    private String city;

    @Attribute(name = "idUser")
    public String getIdUser() {
        return idUser;
    }

    @Attribute(name = "idUser")
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Element(required=false, name = "Offer")
    public Offer getOffer() {
        return offer;
    }

    @Element(required=false, name = "Offer")
    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @ElementList(required=false, name = "ListComment")
    public List<Comment> getListCommentForUser() {
        return listCommentForUser;
    }

    @ElementList(required=false, name = "ListComment")
    public void setListCommentForUser(List<Comment> listCommentForUser) {
        this.listCommentForUser = listCommentForUser;
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

    @Override
    public String toString() {
        if (this.getOffer() != null) {
            if (this.getOffer().getIdOffer() == null) {
                return "User{" +
                        ", idUser='" + idUser + '\'' +
                        ", offer=null" +
                        ", listCommentForUser=" + listCommentForUser +
                        ", name='" + name + '\'' +
                        ", country='" + country + '\'' +
                        ", city='" + city + '\'' +
                        '}';
            }
        }
        return "User{" +
                ", idUser='" + idUser + '\'' +
                ", offer=" + offer +
                ", listCommentForUser=" + listCommentForUser +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

}