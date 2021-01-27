package ru.sfedu.hospitalityNetwork.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospitalityNetwork.Main;
import ru.sfedu.hospitalityNetwork.model.Offer;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderCSV;

import java.util.Optional;

public class OfferConverter extends AbstractBeanField<Offer, Integer> {
    private DataProviderCSV dataProviderCSV = new DataProviderCSV();
    private static final Logger log = LogManager.getLogger(Main.class);
    @Override
    protected Object convert(String s) {
        try {
            if (!s.equals("null")) {
                Optional<Offer> offer = dataProviderCSV.getOfferOptional(s);
                return offer.get();
            }
            Offer newOffer = new Offer();
            return newOffer;
        } catch (Exception e) {
            log.error(e);
            Offer newOffer = new Offer();
            return newOffer;
        }
    }

    @Override
    protected String convertToWrite(Object value) {
        Offer offer = (Offer) value;
        if (offer != null) {
            return String.valueOf((offer.getIdOffer()));
        } else {
            return "null";
        }
    }

}
