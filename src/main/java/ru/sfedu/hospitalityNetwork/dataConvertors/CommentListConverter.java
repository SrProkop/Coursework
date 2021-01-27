package ru.sfedu.hospitalityNetwork.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospitalityNetwork.Constants;
import ru.sfedu.hospitalityNetwork.Main;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderCSV;
import ru.sfedu.hospitalityNetwork.model.Comment;
import ru.sfedu.hospitalityNetwork.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentListConverter extends AbstractBeanField<Comment, Integer> {

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
            List<Comment> listComment = new ArrayList<>();
            for (String strIndex : splitList) {
                if (!strIndex.isEmpty()) {
                    listComment.add(dataProviderCSV.getComment(strIndex).get());
                }
            }
            return listComment;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    protected String convertToWrite(Object value) {
        try {
            List<Comment> commentList = (List<Comment>) value;
            StringBuilder builder = new StringBuilder(Constants.FIRST_SYMBOL);
            if (commentList.size() > 0) {
                for (Comment comment : commentList) {
                    builder.append(comment.getIdComment());
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
