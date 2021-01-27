package ru.sfedu.hospitalityNetwork;

public class Constants {

    public static final String COMMAND_NOT_FOUND = "Command not found";
    public static final String PROVIDER_NOT_FOUND = "Provider not found";
    public static final String COMMAND_ERROR = "Error method name";
    public static final String PARAMS_ERROR = "Wrong number parameters";

    public static final String USER_LIST_NOT_FOUND = "User list not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String OFFER_NOT_FOUND = "Offer not found";
    public static final String OFFER_LIST_NOT_FOUND = "Offer list not found";
    public static final String COMMENT_LIST_NOT_FOUND = "Comment list not found";
    public static final String COMMENT_NOT_FOUND = "Comment not found";
    public static final String RESPONSE_LIST_NOT_FOUND = "Response list not found";
    public static final String RESPONSE_NOT_FOUND = "Response not found";

    public static final String CONFIG_PATH = "config.path";
    public static final String CSV_PATH = "csv.path";
    public static final String CSV_EXTENSION = "csv.extension";

    public static final String XML_PATH = "xml.path";
    public static final String XML_EXTENSION = "xml.extension";
    public static final String EXCEPTION_CANNOT_CREATE_FILE = "unable to create file %s";

    public static final String JDBC_INIT_PATH = "db_init_path";
    public static final String JDBC_URL = "db_url";
    public static final String JDBC_USER = "db_user";
    public static final String JDBC_PASSWORD = "db_password";
    public static final String JDBC_DRIVER = "db_driver";

    public static final String CONVERTER_REGEXP_LIST_WITHOUT_QUOTES = "^\\[";
    public static final String CONVERTER_REGEXP_LIST_WITH_QUOTES = "^\\\"\\[";
    public static final String DELIMITER_SYMBOL = ",";
    public static final String FIRST_SYMBOL = "[";
    public static final String LAST_SYMBOL = "]";

    public static final String DELETE_FILE = "Delete file: ";
    public static final String CREATE_FILE = "Create file: ";

    /* FOR JDBC */

    public static final String COLUMN_USER_ID = "idUser";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_COUNTRY = "country";
    public static final String COLUMN_USER_CITY = "city";

    public static final String COLUMN_COMMENT_ID_COMMENT = "idComment";
    public static final String COLUMN_COMMENT_NAME = "comment";
    public static final String COLUMN_COMMENT_ID_USER_FROM= "idUserFrom";
    public static final String COLUMN_COMMENT_ID_USER_TO = "idUserTo";
    public static final String COLUMN_COMMENT_RATING = "Rating";

    public static final String COLUMN_OFFER_HOST_ID = "idOffer";
    public static final String COLUMN_OFFER_HOST_ID_USER = "idUser";
    public static final String COLUMN_OFFER_HOST_NAME = "name";
    public static final String COLUMN_OFFER_HOST_COUNTRY = "country";
    public static final String COLUMN_OFFER_HOST_CITY = "city";
    public static final String COLUMN_OFFER_HOST_TYPE_OFFER = "typeOffer";
    public static final String COLUMN_OFFER_HOST_HOUSE_TYPE = "houseType";
    public static final String COLUMN_OFFER_HOST_PERSONAL_MEETING = "personalMeeting";
    public static final String COLUMN_OFFER_HOST_ADDRESS_HOUSE = "addressHouse";

    public static final String COLUMN_OFFER_GUEST_ID = "idOffer";
    public static final String COLUMN_OFFER_GUEST_ID_USER = "idUser";
    public static final String COLUMN_OFFER_GUEST_NAME = "name";
    public static final String COLUMN_OFFER_GUEST_COUNTRY = "country";
    public static final String COLUMN_OFFER_GUEST_CITY = "city";
    public static final String COLUMN_OFFER_GUEST_TYPE_OFFER = "typeOffer";
    public static final String COLUMN_OFFER_GUEST_WEIGHT_BAGGAGE = "weightBaggage";
    public static final String COLUMN_OFFER_GUEST_NUMBER_DAY = "numberDay";

    public static final String COLUMN_RESPONSE_ID = "idResponse";
    public static final String COLUMN_RESPONSE_ID_USER = "idUser";
    public static final String COLUMN_RESPONSE_ID_OFFER = "idOffer";

    public static final String INSERT_USER = "INSERT INTO USER VALUES ('%s', '%s', '%s', '%s', '%s', '%s');";
    public static final String SELECT_USERS = "SELECT * FROM USER";
    public static final String SELECT_USER = "SELECT * FROM USER WHERE idUser='%s';";
    public static final String DELETE_USER = "DELETE FROM USER WHERE idUser='%s';";

    public static final String INSERT_COMMENT = "INSERT INTO COMMENT VALUES ('%s', '%s', '%s', '%s', '%s');";
    public static final String SELECT_COMMENTS = "SELECT * FROM COMMENT;";
    public static final String SELECT_COMMENT_BY_ID = "SELECT * FROM COMMENT WHERE idComment='%s';";
    public static final String SELECT_COMMENT_BY_USER = "SELECT * FROM COMMENT WHERE idUserTo='%s';";
    public static final String SELECT_COMMENT_BY_USER_FROM = "SELECT * FROM COMMENT WHERE idUserFrom='%s';";
    public static final String DELETE_COMMENT = "DELETE FROM COMMENT WHERE idComment='%s';";
    public static final String DELETE_COMMENT_FOR_USER = "DELETE FROM COMMENT WHERE idUserTo='%s';";

    public static final String INSERT_OFFER_HOST = "INSERT INTO OFFER_HOST VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
    public static final String SELECT_OFFER_HOSTS = "SELECT * FROM OFFER_HOST";
    public static final String SELECT_OFFER_HOST = "SELECT * FROM OFFER_HOST WHERE idOffer='%s';";
    public static final String DELETE_OFFER_HOST = "DELETE FROM OFFER_HOST WHERE idOffer='%s';";

    public static final String INSERT_OFFER_GUEST = "INSERT INTO OFFER_GUEST VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
    public static final String SELECT_OFFER_GUESTS = "SELECT * FROM OFFER_GUEST";
    public static final String SELECT_OFFER_GUEST = "SELECT * FROM OFFER_GUEST WHERE idOffer='%s';";
    public static final String DELETE_OFFER_GUEST = "DELETE FROM OFFER_GUEST WHERE idOffer='%s';";

    public static final String SELECT_OFFER_HOST_USER = "SELECT * FROM OFFER_HOST WHERE idUser='%s';";
    public static final String SELECT_OFFER_GUEST_USER = "SELECT * FROM OFFER_GUEST WHERE idUser='%s';";

    public static final String INSERT_RESPONSE = "INSERT INTO RESPONSE VALUES ('%s', '%s', '%s');";
    public static final String SELECT_RESPONSES = "SELECT * FROM RESPONSE;";
    public static final String SELECT_RESPONSE_BY_ID = "SELECT * FROM RESPONSE WHERE idResponse='%s';";
    public static final String SELECT_RESPONSE_BY_OFFER = "SELECT * FROM RESPONSE WHERE idOffer='%s';";
    public static final String DELETE_RESPONSE = "DELETE FROM RESPONSE WHERE idResponse='%s';";
    public static final String DELETE_RESPONSE_OFFER = "DELETE FROM RESPONSE WHERE idOffer='%s';";

    public static final String CLEAR_BD = "drop table if exists comment cascade; drop table if exists response cascade; drop table if exists offer_Guest cascade; drop table if exists offer_Host cascade; drop table if exists user cascade;";

    /* FOR TEST */

    public static final String TEST_USER_1_NAME = "Ivanov Ivan Ivanovich";
    public static final String TEST_USER_1_COUNTRY = "Russia";
    public static final String TEST_USER_1_CITY = "Moscow";

    public static final String TEST_USER_2_NAME = "Petrov Petr Petrovich";
    public static final String TEST_USER_2_COUNTRY = "Japan";
    public static final String TEST_USER_2_CITY = "Tokyo";

    public static final String TEST_OFFER_HOST_NAME = "I will gladly welcome you";
    public static final String TEST_OFFER_HOST_COUNTRY = "Japan";
    public static final String TEST_OFFER_HOST_CITY = "Tokyo";
    public static final String TEST_OFFER_HOST_ADDRESS = "Street Meecheeline h.77";

    public static final String TEST_OFFER_GUEST_NAME = "Coming to Osaka";
    public static final String TEST_OFFER_GUEST_COUNTRY = "Japan";
    public static final String TEST_OFFER_GUEST_CITY = "Osaka";

    public static final String TEST_COMMENT_1 = "Good user";
    public static final String TEST_COMMENT_2 = "Perfect user";

}