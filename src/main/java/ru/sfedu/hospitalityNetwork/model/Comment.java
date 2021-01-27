package ru.sfedu.hospitalityNetwork.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import ru.sfedu.hospitalityNetwork.enums.Rating;

import java.io.Serializable;

@Root(name = "Comment")
public class Comment implements Serializable {

    @CsvBindByName
    private String idComment;
    @CsvBindByName
    private String comment;
    @CsvBindByName
    private String idUserFrom;
    @CsvBindByName
    private String idUserTo;
    @CsvBindByName
    private Rating rating;

    @Attribute(name = "idComment")
    public String getIdComment() {
        return idComment;
    }

    @Attribute(name = "idComment")
    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    @Attribute(name = "comment")
    public String getComment() {
        return comment;
    }

    @Attribute(name = "comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Attribute(name = "idUserFrom")
    public String getIdUserFrom() {
        return idUserFrom;
    }

    @Attribute(name = "idUserFrom")
    public void setIdUserFrom(String idUserFrom) {
        this.idUserFrom = idUserFrom;
    }

    @Attribute(name = "idUserTo")
    public String getIdUserTo() {
        return idUserTo;
    }

    @Attribute(name = "idUserTo")
    public void setIdUserTo(String idUserTo) {
        this.idUserTo = idUserTo;
    }

    @Attribute(name = "rating")
    public Rating getRating() {
        return rating;
    }

    @Attribute(name = "rating")
    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "idComment='" + idComment + '\'' +
                ", comment='" + comment + '\'' +
                ", idUserFrom='" + idUserFrom + '\'' +
                ", idUserTo='" + idUserTo + '\'' +
                ", rating=" + rating +
                '}';
    }


}