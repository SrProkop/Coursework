package ru.sfedu.hospitalityNetwork.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospitalityNetwork.Constants;
import ru.sfedu.hospitalityNetwork.Main;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderCSV;
import ru.sfedu.hospitalityNetwork.model.Response;
import utils.PropertyProvider;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResponseListConverter extends AbstractBeanField<Response, Integer> {

    private static final Logger log = LogManager.getLogger(Main.class);
    final Pattern patternWithout = Pattern.compile(Constants.CONVERTER_REGEXP_LIST_WITHOUT_QUOTES);
    final Pattern patternWith = Pattern.compile(Constants.CONVERTER_REGEXP_LIST_WITH_QUOTES);
    private DataProviderCSV dataProviderCSV = new DataProviderCSV();

    protected Object convert(String s) {
        try {
            final Matcher matcherWithout = patternWithout.matcher(s);
            final Matcher matcherWith = patternWith.matcher(s);
            String indexString;

            if (matcherWithout.find()) {
                indexString = s.substring(1, s.length() - 1);
            } else if (matcherWith.find()) {
                indexString = s.substring(2, s.length() - 2);
            } else {
                return new ArrayList<Response>();
            }
            String[] splitList = indexString.split(Constants.DELIMITER_SYMBOL);
            List<Response> listResponse = new ArrayList<>();
            for (String strIndex : splitList) {
                if (!strIndex.isEmpty()) {
                    listResponse.add(dataProviderCSV.getResponse(strIndex).get());
                }
            }
            return listResponse;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    protected String convertToWrite(Object value) {
        try {
            List<Response> responseList = (List<Response>) value;
            StringBuilder builder = new StringBuilder(Constants.FIRST_SYMBOL);
            if (responseList.size() > 0) {
                for (Response response : responseList) {
                    builder.append(response.getIdResponse());
                    builder.append(Constants.DELIMITER_SYMBOL);
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(Constants.LAST_SYMBOL);
            return builder.toString();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
