package ru.sfedu.hospitalityNetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProvider;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderCSV;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderJDBC;
import ru.sfedu.hospitalityNetwork.dataProviders.DataProviderXML;
import ru.sfedu.hospitalityNetwork.enums.Keys;

import java.util.Arrays;
import java.util.List;


public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        List<String> commandList = Arrays.asList(args);
        String params = "";
        DataProvider dataProvider;
        try {
            if (commandList.size() == 3) {
                params = commandList.get(2);
            }

            switch (Keys.valueOf(commandList.get(0).toUpperCase())) {
                case CSV:
                    dataProvider = new DataProviderCSV();
                    break;
                case XML:
                    dataProvider = new DataProviderXML();
                    break;
                case JDBC:
                    dataProvider = new DataProviderJDBC(true);
                    break;
                default:
                    log.error(Constants.PROVIDER_NOT_FOUND);
                    dataProvider = null;
            }

            log.info(selectMethod(dataProvider, commandList.get(1), params));
        } catch (Exception e) {
            log.error(Constants.COMMAND_NOT_FOUND);
            log.error(e);
        }
    }

    

    private static String selectMethod(DataProvider dataProvider, String cKey, String params) {
        try {
            switch (Keys.valueOf(cKey.toUpperCase())) {
                case CREATE_USER:
                    return createUser(dataProvider, params);
                case GET_USER:
                    return dataProvider.getUser(params).toString();
                case GET_USERS:
                    return dataProvider.getListUser().toString();
            }
        } catch (IllegalArgumentException e) {
            log.error(Constants.COMMAND_ERROR);
        }
        return "";
    }

    private static String createUser(DataProvider dataProvider, String params) {
        String[] parseCommand = checkNumber(params, 3);
        if (parseCommand == null) {
            return "";
        } else {
            return dataProvider.createUser(
                    parseCommand[0],
                    parseCommand[1],
                    parseCommand[2]
            ).toString();
        }
    }

    private static String[] checkNumber(String methodParams, int count) {
        String[] result = methodParams.split(Constants.DELIMITER_SYMBOL);
        if (result.length != count) {
            log.error(Constants.PARAMS_ERROR);
            return null;
        } else {
            return result;
        }
    }

}