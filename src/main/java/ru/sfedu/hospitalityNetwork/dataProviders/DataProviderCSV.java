package ru.sfedu.hospitalityNetwork.dataProviders;

import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.enums.Rating;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.sfedu.hospitalityNetwork.Constants;
import ru.sfedu.hospitalityNetwork.Main;
import ru.sfedu.hospitalityNetwork.enums.HouseType;
import ru.sfedu.hospitalityNetwork.enums.Outcome;
import ru.sfedu.hospitalityNetwork.model.*;
import utils.PropertyProvider;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataProviderCSV implements DataProvider {

    private static DataProvider INSTANCE = null;

    private static final Logger log = LogManager.getLogger(Main.class);

    public static DataProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataProviderCSV();
        }
        return INSTANCE;
    }

    private <T> void insertIntoCsv(T object) throws IOException {
        insertIntoCsv(object.getClass(), Collections.singletonList(object), false);
    }

    private <T> void insertIntoCsv(Class<?> tClass,
                                   List<T> objectList,
                                   boolean overwrite) throws IOException {
        List<T> tList;
        if (!overwrite) {
            tList = (List<T>) getFromCsv(tClass);
            tList.addAll(objectList);
        } else {
            tList = objectList;
        }
        if (tList.isEmpty()) {
            deleteFile(tClass);
        }
        CSVWriter csvWriter = getCsvWriter(tClass);
        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                .withApplyQuotesToAll(false)
                .build();
        try {
            beanToCsv.write(tList);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(e);
        }
        csvWriter.close();
    }

    private <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
        FileWriter writer;
        File path = new File(PropertyProvider.getProperty(Constants.CSV_PATH));
        File file = new File(getFilePath(tClass));
        if (!file.exists()) {
            if (path.mkdirs()) {
                if (!file.createNewFile()) {
                    throw new IOException(
                            String.format(Constants.EXCEPTION_CANNOT_CREATE_FILE,
                                    file.getName()));
                }
            }
        }
        writer = new FileWriter(file);
        return new CSVWriter(writer);
    }


    private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
        File file = new File(getFilePath(tClass));

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException(
                        String.format(
                                Constants.EXCEPTION_CANNOT_CREATE_FILE,
                                file.getName()));
            }
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return new CSVReader(bufferedReader);
    }


    private <T> List<T> getFromCsv(Class<T> tClass) throws IOException {
        List<T> tList;
        try {
            CSVReader csvReader = getCsvReader(tClass);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                    .withType(tClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            tList = csvToBean.parse();
            csvReader.close();
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
        return tList;
    }


    private <T> String getFilePath(Class<T> tClass) throws IOException {
        return PropertyProvider.getProperty(Constants.CSV_PATH)
                + tClass.getSimpleName().toLowerCase()
                + PropertyProvider.getProperty(Constants.CSV_EXTENSION);
    }

    private <T> File getFile(Class<T> tClass) throws IOException{
        return new File(PropertyProvider.getProperty(Constants.CSV_PATH)
                + tClass.getSimpleName().toLowerCase()
                + PropertyProvider.getProperty(Constants.CSV_EXTENSION));
    }

    private <T> void deleteFile(Class<T> tClass) {
        try {
            log.debug(Constants.DELETE_FILE + getFile(tClass));
            log.debug(getFile(tClass).delete());
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public void deleteAll() {
        List<Class> classList = new ArrayList<>();
        classList.add(User.class);
        classList.add(OfferGuest.class);
        classList.add(OfferHost.class);
        classList.add(Response.class);
        classList.add(Comment.class);
        classList.forEach(this::deleteFile);
    }

    @Override
    public void initDB() {
        try {
            insertIntoCsv(User.class, new ArrayList<>(), true);
            insertIntoCsv(OfferHost.class, new ArrayList<>(), true);
            insertIntoCsv(OfferHost.class, new ArrayList<>(), true);
            insertIntoCsv(Comment.class, new ArrayList<>(), true);
            insertIntoCsv(Response.class, new ArrayList<>(), true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public Outcome createUser(@NotNull String name,
                              @NotNull String country,
                              @NotNull String city) {
        Optional<User> userOptional = createUserOptional(name, country, city);
        if (userOptional.isPresent()) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<User> getUser(@NotNull String userId) {
        Optional<User> optionalUser = getUserOptional(userId);
        if (optionalUser.isPresent()) {
            return optionalUser;
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<User>> getListUser(){
        Optional<List<User>> optionalUserList = getUsersOptional();
        if (optionalUserList.isPresent()) {
            return optionalUserList;
        } else {
            log.error(Constants.USER_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome editUser(@NotNull String userId,
                            @NotNull String name,
                            @NotNull String country,
                            @NotNull String city) {
        Optional<User> userOptional = getUserOptional(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(name);
            user.setCountry(country);
            user.setCity(city);

            Optional<List<User>> userListOptionalNew = getUsersOptional();
            List<User> users = userListOptionalNew.get();
            users = users.stream()
                    .filter(userTrue -> !userTrue.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(user);
            return rewriteUsers(users);
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteUser(@NotNull String userId) {
        Optional<List<User>> userListOptional = getUsersOptional();
        Optional<User> optionalUser = getUser(userId);
        if (optionalUser.isPresent()) {
            if (userListOptional.isPresent()) {
                deleteCommentForUser(userId);
                deleteCommentToUser(userId);
                deleteOffer(userId);
                deleteResponseUser(userId);
                Optional<List<User>> userListOptionalNew = getUsersOptional();
                List<User> users = userListOptionalNew.get();
                users = users.stream()
                        .filter(user -> !user.getIdUser()
                                .equals(userId))
                        .collect(Collectors.toList());
                return rewriteUsers(users);
            } else {
                log.error(Constants.USER_LIST_NOT_FOUND);
                return Outcome.NOT_FOUNDED;
            }
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.NOT_FOUNDED;
        }
    }

    @Override
    public Outcome createOfferHost(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull String addressHouse,
            @NotNull HouseType houseType,
            @NotNull boolean personalMeeting,
            @NotNull String idUser) {
        Optional<OfferHost> offerHostOptional = createOfferHostOptional(name, country, city, addressHouse, houseType, personalMeeting, idUser);
        if (offerHostOptional.isPresent()) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<OfferHost> getOfferHost(@NotNull String idOffer) {
        Optional<OfferHost> offerHostOptional = getOfferHostOptional(idOffer);
        if (offerHostOptional.isPresent()) {
            return offerHostOptional;
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome editOfferHost(@NotNull String offerId,
                                 @NotNull String name,
                                 @NotNull String country,
                                 @NotNull String city,
                                 @NotNull String addressHouse,
                                 @NotNull HouseType houseType,
                                 @NotNull boolean personalMeeting) {
        Optional<OfferHost> offerHostOptional = getOfferHostOptional(offerId);
        if (offerHostOptional.isPresent()) {
            OfferHost offerHost = offerHostOptional.get();
            offerHost.setName(name);
            offerHost.setCountry(country);
            offerHost.setCity(city);
            offerHost.setAddressHouse(addressHouse);
            offerHost.setHouseType(houseType);
            offerHost.setPersonalMeeting(personalMeeting);

            Optional<List<OfferHost>> offerHostsOptional = getOfferHostsOptional();
            List<OfferHost> offerHosts = offerHostsOptional.get();
            offerHosts = offerHosts.stream()
                    .filter(offerHostTrue -> !offerHostTrue.getIdOffer()
                            .equals(offerId))
                    .collect(Collectors.toList());
            offerHosts.add(offerHost);
            return rewriteOfferHosts(offerHosts);
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteOfferHost(@NotNull String idOffer) {
        Optional<List<OfferHost>> offerHostListOptional = getOfferHostsOptional();
        Optional<OfferHost> optionalOfferHost = getOfferHost(idOffer);
        if (optionalOfferHost.isPresent()) {
            if (offerHostListOptional.isPresent()) {
                List<OfferHost> offerHosts = offerHostListOptional.get();
                offerHosts = offerHosts.stream()
                        .filter(offerHost -> !offerHost.getIdOffer()
                                .equals(idOffer))
                        .collect(Collectors.toList());
                deleteOfferFromUser(optionalOfferHost.get().getIdUser());
                deleteResponseOffer(idOffer);

                return rewriteOfferHosts(offerHosts);
            } else {
                log.error(Constants.OFFER_LIST_NOT_FOUND);
                return Outcome.FAILED;
            }
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.NOT_FOUNDED;
        }
    }

    @Override
    public Optional<List<OfferHost>> getListOfferHosts(Filter filter) {
        Optional<List<OfferHost>> offerHostList = getOfferHostsOptional();
        if (offerHostList.isPresent()) {
            if (filter == null) {
                return offerHostList;
            }

            List<OfferHost> offerHostsListFilter = offerHostList.get();
            offerHostsListFilter = offerHostsListFilter.stream()
                    .filter(offerHost -> filter.valid(offerHost) == true)
                    .collect(Collectors.toList());

            if (offerHostsListFilter.size() > 0) {
                return Optional.of(offerHostsListFilter);
            } else {
                log.error(Constants.OFFER_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } else {
            log.error(Constants.OFFER_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome createOfferGuest(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull int weightBaggage,
            @NotNull int numberDay,
            @NotNull String idUser) {
        Optional<OfferGuest> offerGuestOptional = createOfferGuestOptional(name, country, city, weightBaggage, numberDay, idUser);
        if (offerGuestOptional.isPresent()) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<OfferGuest> getOfferGuest(@NotNull String idOffer) {
        Optional<OfferGuest> offerGuestOptional = getOfferGuestOptional(idOffer);
        if (offerGuestOptional.isPresent()) {
            return offerGuestOptional;
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome editOfferGuest(@NotNull String offerId,
                                  @NotNull String name,
                                  @NotNull String country,
                                  @NotNull String city,
                                  @NotNull int weightBaggage,
                                  @NotNull int numberDay) {
        Optional<OfferGuest> offerGuestOptional = getOfferGuestOptional(offerId);
        if (offerGuestOptional.isPresent()) {
            OfferGuest offerGuest = offerGuestOptional.get();
            offerGuest.setName(name);
            offerGuest.setCountry(country);
            offerGuest.setCity(city);
            offerGuest.setWeightBaggage(weightBaggage);
            offerGuest.setNumberDay(numberDay);

            Optional<List<OfferGuest>> offerGuestsOptional = getOfferGuestsOptional();
            List<OfferGuest> offerGuests = offerGuestsOptional.get();
            offerGuests = offerGuests.stream()
                    .filter(offerGuestTrue -> !offerGuestTrue.getIdOffer()
                            .equals(offerId))
                    .collect(Collectors.toList());
            offerGuests.add(offerGuest);
            return rewriteOfferGuests(offerGuests);
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteOfferGuest(@NotNull String idOffer) {
        Optional<List<OfferGuest>> offerGuestListOptional = getOfferGuestsOptional();
        Optional<OfferGuest> optionalOfferGuest = getOfferGuest(idOffer);
        if (optionalOfferGuest.isPresent()) {
            List<OfferGuest> offerGuests = offerGuestListOptional.get();

            offerGuests = offerGuests.stream()
                    .filter(offerGuest -> !offerGuest.getIdOffer()
                            .equals(idOffer))
                    .collect(Collectors.toList());
            deleteOfferFromUser(optionalOfferGuest.get().getIdUser());
            deleteResponseOffer(idOffer);
            return rewriteOfferGuests(offerGuests);
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<List<OfferGuest>> getListOfferGuests(Filter filter) {
        Optional<List<OfferGuest>> offerGuestList = getOfferGuestsOptional();
        if (offerGuestList.isPresent()) {
            if (filter == null) {
                return offerGuestList;
            }

            List<OfferGuest> offerGuestListFilter = offerGuestList.get();
            offerGuestListFilter = offerGuestListFilter.stream()
                    .filter(offerGuest -> filter.valid(offerGuest) == true)
                    .collect(Collectors.toList());

            if (offerGuestListFilter.size() > 0) {
                return Optional.of(offerGuestListFilter);
            } else {
                log.error(Constants.OFFER_LIST_NOT_FOUND);
                return Optional.empty();
            }

        } else {
            log.error(Constants.OFFER_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Offer> getOfferUser(@NotNull String idUser) {
        Optional<OfferHost> optionalOfferHost = getOfferHostUserOptional(idUser);
        Optional<OfferGuest> optionalOfferGuests = getOfferGuestUserOptional(idUser);
        Optional<User> userOptional = getUserOptional(idUser);
        if (userOptional.isPresent()) {
            if (optionalOfferHost.isPresent()) {
                Offer offer = optionalOfferHost.get();
                return Optional.of(offer);
            }
            if (optionalOfferGuests.isPresent()) {
                Offer offer = optionalOfferGuests.get();
                return Optional.of(offer);
            }
            log.error(Constants.OFFER_NOT_FOUND);
            return Optional.empty();
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome createComment(
            @NotNull String comment,
            @NotNull String idUserFrom,
            @NotNull String idUserTo,
            @NotNull Rating rating) {
        Optional<Comment> commentOptional = createCommentOptional(comment, idUserFrom, idUserTo, rating);
        if (commentOptional.isPresent()) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<Comment> getComment(@NotNull String idComment) {
        Optional<Comment> optionalComment = getCommentOptional(idComment);
        if (optionalComment.isPresent()) {
            return optionalComment;
        } else {
            log.error(Constants.COMMENT_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Comment>> getListComment() {
        Optional<List<Comment>> optionalComments = getCommentsOptional();
        if (optionalComments.isPresent()) {
            return optionalComments;
        } else {
            log.error(Constants.COMMENT_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Comment>> getListCommentUser(@NotNull String idUser) {
        Optional<List<Comment>> commentList = getCommentsOptional();
        if (commentList.isPresent()) {
            List<Comment> list = commentList.get();
            list = list.stream()
                    .filter(comment -> comment.getIdUserTo()
                            .equals(idUser))
                    .collect(Collectors.toList());
            return Optional.of(list);
        } else {
            log.error(Constants.COMMENT_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome editComment(@NotNull String idComment,
                               @NotNull String comment,
                               @NotNull Rating rating) {
        Optional<Comment> commentOptional = getCommentOptional(idComment);
        if (commentOptional.isPresent()) {
            Comment newComment = commentOptional.get();
            newComment.setComment(comment);
            newComment.setRating(rating);

            List<Comment> commentList = getListComment().get();
            commentList = commentList.stream()
                    .filter(commentTrue -> !commentTrue.getIdComment()
                            .equals(idComment))
                    .collect(Collectors.toList());
            commentList.add(newComment);
            return rewriteComments(commentList);
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteComment(@NotNull String idComment) {
        Optional<List<Comment>> commentListOptional = getCommentsOptional();
        Optional<Comment> optionalComment = getComment(idComment);
        if (commentListOptional.isPresent() && optionalComment.isPresent()) {
            List<Comment> comments = commentListOptional.get();

            comments = comments.stream()
                    .filter(comment -> !comment.getIdComment()
                            .equals(idComment))
                    .collect(Collectors.toList());
            deleteCommentFromUser(optionalComment.get().getIdUserTo(), idComment);
            return rewriteComments(comments);
        } else {
            log.error(Constants.COMMENT_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome createResponse(
            @NotNull String idUser,
            @NotNull String idOffer) {
        Optional<Response> responseOptional = createResponseOptional(idUser, idOffer);
        if (responseOptional.isPresent()) {
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<Response> getResponse(@NotNull String idResponse) {
        Optional<Response> optionalResponse = getResponseOptional(idResponse);
        if (optionalResponse.isPresent()) {
            return optionalResponse;
        } else {
            log.error(Constants.RESPONSE_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Response>> getListResponse(){
        Optional<List<Response>> optionalResponses = getResponsesOptional();
        if (optionalResponses.isPresent()) {
            return optionalResponses;
        } else {
            log.error(Constants.RESPONSE_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Response>> getListResponseOffer(@NotNull String idOffer) {
        Optional<List<Response>> responseList = getResponsesOptional();
        Optional<Offer> optionalOffer = getOfferOptional(idOffer);
        if (optionalOffer.isEmpty()) {
            log.error(Constants.OFFER_NOT_FOUND);
            return Optional.empty();
        }
        if (responseList.isPresent()) {
            List<Response> list = responseList.get();
            list = list.stream()
                    .filter(response -> response.getIdOffer()
                            .equals(idOffer))
                    .collect(Collectors.toList());

            return Optional.of(list);
        } else {
            log.error(Constants.RESPONSE_LIST_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Outcome deleteResponse(@NotNull String idResponse) {
        Optional<List<Response>> responseListOptional = getResponsesOptional();
        Optional<Response> optionalResponse = getResponse(idResponse);
        if (responseListOptional.isPresent() && optionalResponse.isPresent()) {
            List<Response> responses = responseListOptional.get();

            responses = responses.stream()
                    .filter(response -> !response.getIdResponse()
                            .equals(idResponse))
                    .collect(Collectors.toList());
            deleteResponseFromOffer(optionalResponse.get().getIdOffer(), idResponse);
            return rewriteResponse(responses);
        } else {
            log.error(Constants.RESPONSE_NOT_FOUND);
            return Outcome.FAILED;
        }
    }


    private Optional<User> createUserOptional(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city) {
        try {
            String uuid = UUID.randomUUID().toString();
            User user = new User();
            user.setIdUser(uuid);
            user.setName(name);
            user.setCountry(country);
            user.setCity(city);
            user.setListCommentForUser(new ArrayList<>());
            insertIntoCsv(user);
            return Optional.of(user);
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Optional<User> getUserOptional(String userId) {
        Optional<List<User>> userList = getUsersOptional();
        if (userList.isPresent()) {
            List<User> users = userList.get();
            return users
                    .stream()
                    .filter(user -> user.getIdUser().equals(userId))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<User>> getUsersOptional() {
        try {
            return Optional.of(getFromCsv(User.class));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome rewriteUsers(List<User> users) {
        try {
            insertIntoCsv(User.class, users, true);
            return Outcome.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }



    private Optional<OfferGuest> createOfferGuestOptional(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull int weightBaggage,
            @NotNull int numberDay,
            @NotNull String idUser) {
        try {
            Optional<User> user = getUserOptional(idUser);
            if (user.isPresent() && user.get().getOffer().getIdOffer() == null) {
                String uuid = UUID.randomUUID().toString();
                OfferGuest offerGuest = new OfferGuest();
                offerGuest.setIdOffer(uuid);
                offerGuest.setIdUser(idUser);
                offerGuest.setName(name);
                offerGuest.setCountry(country);
                offerGuest.setCity(city);
                offerGuest.setWeightBaggage(weightBaggage);
                offerGuest.setNumberDay(numberDay);
                offerGuest.setListResponse(new ArrayList<>());
                offerGuest.setTypeOffer(false);
                addOfferGuestToUser(offerGuest, idUser);
                insertIntoCsv(offerGuest);
                return Optional.of(offerGuest);
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome addOfferGuestToUser(@NotNull OfferGuest offerGuest,
                                        @NotNull String userId) {
        Optional<User> userOptional = getUserOptional(userId);
        if (userOptional.isPresent() && userOptional.get().getOffer() != null) {
            userOptional.get().setOffer(offerGuest);
            Optional<List<User>> optionalUserList = getUsersOptional();
            List<User> users = optionalUserList.get();
            users = users.stream()
                    .filter(user -> !user.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(userOptional.get());
            rewriteUsers(users);
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }

    private Optional<OfferGuest> getOfferGuestOptional(@NotNull String idOffer) {
        Optional<List<OfferGuest>> offerGuestList = getOfferGuestsOptional();
        if (offerGuestList.isPresent()) {
            List<OfferGuest> offerGuests = offerGuestList.get();
            return offerGuests
                    .stream()
                    .filter(offerGuest -> offerGuest.getIdOffer().equals(idOffer))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }


    private Optional<List<OfferGuest>> getOfferGuestsOptional() {
        try {
            return Optional.of(getFromCsv(OfferGuest.class));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Optional<OfferGuest> getOfferGuestUserOptional(@NotNull String idUser) {
        Optional<List<OfferGuest>> offerGuestList = getOfferGuestsOptional();
        if (offerGuestList.isPresent()) {
            List<OfferGuest> offerGuests = offerGuestList.get();
            return offerGuests
                    .stream()
                    .filter(offerGuest -> offerGuest.getIdUser().equals(idUser))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private Outcome rewriteOfferGuests(List<OfferGuest> offerGuests) {
        try {
            insertIntoCsv(OfferGuest.class, offerGuests, true);
            return Outcome.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Optional<OfferHost> createOfferHostOptional(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull String addressHouse,
            @NotNull HouseType houseType,
            @NotNull boolean personalMeeting,
            @NotNull String idUser) {
        try {
            Optional<User> user = getUserOptional(idUser);
            if (user.isPresent() && user.get().getOffer().getIdOffer() == null) {
                String uuid = UUID.randomUUID().toString();
                OfferHost offerHost = new OfferHost();
                offerHost.setIdOffer(uuid);
                offerHost.setIdUser(idUser);
                offerHost.setName(name);
                offerHost.setCountry(country);
                offerHost.setCity(city);
                offerHost.setAddressHouse(addressHouse);
                offerHost.setHouseType(houseType);
                offerHost.setPersonalMeeting(personalMeeting);
                offerHost.setListResponse(new ArrayList<>());
                offerHost.setTypeOffer(true);
                addOfferHostToUser(offerHost, idUser);
                insertIntoCsv(offerHost);
                return Optional.of(offerHost);
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome addOfferHostToUser(@NotNull OfferHost offerHost,
                                       @NotNull String userId) {
        Optional<User> userOptional = getUserOptional(userId);
        if (userOptional.isPresent() && userOptional.get().getOffer() != null) {
            userOptional.get().setOffer(offerHost);
            Optional<List<User>> optionalUserList = getUsersOptional();
            List<User> users = optionalUserList.get();
            users = users.stream()
                    .filter(user -> !user.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(userOptional.get());
            rewriteUsers(users);
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }

    private Optional<OfferHost> getOfferHostOptional(@NotNull String idOffer) {
        Optional<List<OfferHost>> offerHostList = getOfferHostsOptional();
        if (offerHostList.isPresent()) {
            List<OfferHost> offerHosts = offerHostList.get();
            return offerHosts
                    .stream()
                    .filter(offerHost -> offerHost.getIdOffer().equals(idOffer))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private Optional<OfferHost> getOfferHostUserOptional(@NotNull String idUser) {
        Optional<List<OfferHost>> offerHostList = getOfferHostsOptional();
        if (offerHostList.isPresent()) {
            List<OfferHost> offerHosts = offerHostList.get();
            return offerHosts
                    .stream()
                    .filter(offerHost -> offerHost.getIdUser().equals(idUser))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }


    private Optional<List<OfferHost>> getOfferHostsOptional() {
        try {
            return Optional.of(getFromCsv(OfferHost.class));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome rewriteOfferHosts(List<OfferHost> offerHosts) {
        try {
            insertIntoCsv(OfferHost.class, offerHosts, true);
            return Outcome.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteOfferFromUser(String userId) {
        Optional<User> optionalUser = getUserOptional(userId);
        if (optionalUser.isPresent()) {
            optionalUser.get().setOffer(null);
            Optional<List<User>> optionalUserList = getUsersOptional();
            List<User> users = optionalUserList.get();
            users = users.stream()
                    .filter(user -> !user.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(optionalUser.get());
            rewriteUsers(users);
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    private Optional<Comment> createCommentOptional(
            @NotNull String comment,
            @NotNull String idUserFrom,
            @NotNull String idUserTo,
            @NotNull Rating rating) {
        try {
            Optional<User> optionalUserFrom = getUser(idUserFrom);
            Optional<User> optionalUserTo = getUser(idUserTo);
            if (optionalUserFrom.isPresent() && optionalUserTo.isPresent()) {
                String uuid = UUID.randomUUID().toString();
                Comment commentObject = new Comment();
                commentObject.setIdComment(uuid);
                commentObject.setComment(comment);
                commentObject.setIdUserFrom(idUserFrom);
                commentObject.setIdUserTo(idUserTo);
                commentObject.setRating(rating);
                addCommentToUser(commentObject, idUserTo);
                insertIntoCsv(commentObject);
                return Optional.of(commentObject);
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }


    private Outcome addCommentToUser(@NotNull Comment comment,
                                     @NotNull String userId) {
        Optional<User> userOptional = getUserOptional(userId);
        if (userOptional.isPresent()) {
            userOptional.get().getListCommentForUser().add(comment);
            Optional<List<User>> optionalUserList = getUsersOptional();
            List<User> users = optionalUserList.get();
            users = users.stream()
                    .filter(user -> !user.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(userOptional.get());
            rewriteUsers(users);
            return Outcome.SUCCESS;
        }
        return  Outcome.FAILED;
    }

    private Optional<Comment> getCommentOptional(String commentId) {
        Optional<List<Comment>> commentList = getCommentsOptional();
        if (commentList.isPresent()) {
            List<Comment> comments = commentList.get();
            return comments
                    .stream()
                    .filter(comment -> comment.getIdComment().equals(commentId))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<Comment>> getCommentsForUserOptional(String userId) {
        Optional<List<Comment>> commentList = getCommentsOptional();
        if (commentList.isPresent()) {
            List<Comment> list = commentList.get();
            list = list.stream()
                    .filter(comment -> comment.getIdUserTo()
                            .equals(userId))
                    .collect(Collectors.toList());
            return Optional.of(list);
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<Comment>> getCommentsToUserOptional(String userId) {
        Optional<List<Comment>> commentList = getCommentsOptional();
        if (commentList.isPresent()) {
            List<Comment> list = commentList.get();
            list = list.stream()
                    .filter(comment -> comment.getIdUserFrom()
                            .equals(userId))
                    .collect(Collectors.toList());
            return Optional.of(list);
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<Comment>> getCommentsOptional() {
        try {
            return Optional.of(getFromCsv(Comment.class));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome rewriteComments(List<Comment> comments) {
        try {
            insertIntoCsv(Comment.class, comments, true);
            return Outcome.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteCommentFromUser(String userId, String commentId) {
        Optional<User> optionalUser = getUserOptional(userId);
        Optional<List<Comment>> optionalComments = getCommentsOptional();
        Optional<List<Comment>> optionalCommentsUser = getCommentsForUserOptional(userId);
        if (optionalUser.isPresent() && optionalComments.isPresent() && optionalCommentsUser.get().size() > 0) {
            List<Comment> comments = optionalCommentsUser.get();
            comments = comments.stream()
                    .filter(comment -> !comment.getIdComment()
                            .equals(commentId))
                    .collect(Collectors.toList());

            optionalUser.get().setListCommentForUser(comments);
            Optional<List<User>> optionalUserList = getUsersOptional();
            List<User> users = optionalUserList.get();
            users = users.stream()
                    .filter(user -> !user.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());
            users.add(optionalUser.get());
            rewriteUsers(users);
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    private Outcome deleteCommentForUser(String userId) {
        Optional<User> optionalUser = getUserOptional(userId);
        Optional<List<Comment>> optionalComments = getCommentsOptional();
        Optional<List<Comment>> optionalCommentsUser = getCommentsForUserOptional(userId);
        if (optionalUser.isPresent() && optionalComments.isPresent() && optionalCommentsUser.get().size() > 0) {
            List<Comment> list = optionalCommentsUser.get();
            list.stream().forEach(comment -> deleteComment(comment.getIdComment()));
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }


    private Outcome deleteCommentToUser(String userId) {
        Optional<User> optionalUser = getUserOptional(userId);
        Optional<List<Comment>> optionalComments = getCommentsOptional();
        Optional<List<Comment>> optionalCommentsUser = getCommentsToUserOptional(userId);
        if (optionalUser.isPresent() && optionalComments.isPresent() && optionalCommentsUser.get().size() > 0) {
            List<Comment> list = optionalCommentsUser.get();
            list.stream().forEach(comment -> deleteComment(comment.getIdComment()));
            return Outcome.SUCCESS;
        } else {
            return Outcome.FAILED;
        }
    }

    private Optional<Response> createResponseOptional(
            @NotNull String idUser,
            @NotNull String idOffer) {
        try {
            Optional<User> optionalUser = getUserOptional(idUser);
            Optional<Offer> optionalOffer = getOfferOptional(idOffer);
            if (optionalUser.isPresent()) {
                if (optionalOffer.isPresent()) {
                    String uuid = UUID.randomUUID().toString();
                    Response response = new Response();
                    response.setIdResponse(uuid);
                    response.setIdUser(idUser);
                    response.setIdOffer(idOffer);
                    addResponseToOffer(response, idOffer);
                    insertIntoCsv(response);
                    return Optional.of(response);
                } else {
                    log.error(Constants.OFFER_NOT_FOUND);
                    return Optional.empty();
                }
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome addResponseToOffer(@NotNull Response response,
                                       @NotNull String idOffer) {
        Optional<OfferHost> optionalOfferHost = getOfferHostOptional(idOffer);
        Optional<OfferGuest> optionalOfferGuest = getOfferGuestOptional(idOffer);
        if (optionalOfferHost.isPresent()) {

            optionalOfferHost.get().getListResponse().add(response);

            Optional<List<OfferHost>> optionalOfferHosts = getOfferHostsOptional();
            List<OfferHost> offerHosts = optionalOfferHosts.get();
            offerHosts = offerHosts.stream()
                    .filter(offerHost -> !offerHost.getIdOffer()
                            .equals(idOffer))
                    .collect(Collectors.toList());
            offerHosts.add(optionalOfferHost.get());
            rewriteOfferHosts(offerHosts);
            return Outcome.SUCCESS;
        } else if (optionalOfferGuest.isPresent()) {

            optionalOfferGuest.get().getListResponse().add(response);

            Optional<List<OfferGuest>> optionalOfferGuests = getOfferGuestsOptional();
            List<OfferGuest> offerGuests = optionalOfferGuests.get();
            offerGuests = offerGuests.stream()
                    .filter(offerGuest -> !offerGuest.getIdOffer()
                            .equals(idOffer))
                    .collect(Collectors.toList());
            offerGuests.add(optionalOfferGuest.get());
            rewriteOfferGuests(offerGuests);
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }

    private Optional<Response> getResponseOptional(String responseId) {
        Optional<List<Response>> responseList = getResponsesOptional();
        if (responseList.isPresent()) {
            List<Response> responses = responseList.get();
            return responses
                    .stream()
                    .filter(response -> response.getIdResponse().equals(responseId))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }


    private Optional<List<Response>> getResponsesOptional() {
        try {
            return Optional.of(getFromCsv(Response.class));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Outcome rewriteResponse(List<Response> responses) {
        try {
            insertIntoCsv(Response.class, responses, true);
            return Outcome.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteResponseFromOffer(String offerId, String responseId) {
        Optional<OfferHost> optionalOfferHost = getOfferHostOptional(offerId);
        Optional<OfferGuest> optionalOfferGuest = getOfferGuestOptional(offerId);
        Optional<List<Response>> optionalResponsesOffer = getOptionalResponsesOffer(offerId);
        if (optionalOfferHost.isPresent() && optionalResponsesOffer.isPresent()) {
            List<Response> responses = optionalOfferHost.get().getListResponse();
            responses = responses.stream()
                    .filter(response -> !response.getIdResponse()
                            .equals(responseId))
                    .collect(Collectors.toList());
            optionalOfferHost.get().setListResponse(responses);
            Optional<List<OfferHost>> optionalOfferHosts = getOfferHostsOptional();
            List<OfferHost> offerHosts = optionalOfferHosts.get();
            offerHosts = offerHosts.stream()
                    .filter(offerHost -> !offerHost.getIdOffer()
                            .equals(offerId))
                    .collect(Collectors.toList());
            offerHosts.add(optionalOfferHost.get());
            rewriteOfferHosts(offerHosts);
            return Outcome.SUCCESS;
        } else if (optionalOfferGuest.isPresent() && optionalResponsesOffer.isPresent()) {
            List<Response> responses = optionalOfferGuest.get().getListResponse();
            responses = responses.stream()
                    .filter(response -> !response.getIdResponse()
                            .equals(responseId))
                    .collect(Collectors.toList());
            optionalOfferGuest.get().setListResponse(responses);
            Optional<List<OfferGuest>> optionalOfferGuests = getOfferGuestsOptional();
            List<OfferGuest> offerGuests = optionalOfferGuests.get();
            offerGuests = offerGuests.stream()
                    .filter(offerGuest -> !offerGuest.getIdOffer()
                            .equals(offerId))
                    .collect(Collectors.toList());
            offerGuests.add(optionalOfferGuest.get());
            rewriteOfferGuests(offerGuests);
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }


    private Outcome deleteResponseUser(String idUser) {
        Optional<List<Response>> optionalResponsesUser = getOptionalResponsesUser(idUser);
        if (optionalResponsesUser.isPresent() && optionalResponsesUser.get().size() > 0) {
            List<Response> list = optionalResponsesUser.get();
            list.stream().forEach(response -> deleteResponse(response.getIdResponse()));
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }

    private Outcome deleteResponseOffer(String idOffer) {
        Optional<List<Response>> optionalResponsesOffer = getOptionalResponsesOffer(idOffer);
        if (optionalResponsesOffer.isPresent() && optionalResponsesOffer.get().size() > 0) {
            List<Response> list = optionalResponsesOffer.get();
            list.stream().forEach(response -> deleteResponse(response.getIdResponse()));
            return Outcome.SUCCESS;
        }
        return Outcome.FAILED;
    }

    private Optional<List<Response>> getOptionalResponsesUser(String userId) {
        Optional<List<Response>> responseList = getResponsesOptional();
        if (responseList.isPresent()) {
            List<Response> list = responseList.get();
            list = list.stream()
                    .filter(response -> response.getIdUser()
                            .equals(userId))
                    .collect(Collectors.toList());

            return Optional.of(list);
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<Response>> getOptionalResponsesOffer(String offerId) {
        Optional<List<Response>> responseList = getResponsesOptional();
        if (responseList.isPresent()) {
            List<Response> list = responseList.get();
            list = list.stream()
                    .filter(response -> response.getIdOffer()
                            .equals(offerId))
                    .collect(Collectors.toList());

            return Optional.of(list);
        } else {
            return Optional.empty();
        }
    }



    private Outcome deleteOffer(String idUser) {
        Optional<Offer> optionalOffer = getOfferUser(idUser);
        if (optionalOffer.isPresent()) {
            if (optionalOffer.get().isTypeOffer()) {
                deleteOfferHost(optionalOffer.get().getIdOffer());
                return Outcome.SUCCESS;
            } else {
                deleteOfferGuest(optionalOffer.get().getIdOffer());
                return Outcome.FAILED;
            }
        }
        return Outcome.FAILED;
    }

    public Optional<Offer> getOfferOptional(@NotNull String idOffer) {
        Optional<List<OfferHost>> offerHostList = getOfferHostsOptional();
        Optional<List<OfferGuest>> offerGuestList = getOfferGuestsOptional();
        Optional<OfferHost> optionalOfferHost = getOfferHostOptional(idOffer);
        Optional<OfferGuest> optionalOfferGuest = getOfferGuestOptional(idOffer);
        if (offerHostList.isPresent() && optionalOfferHost.isPresent()) {
            Optional<Offer> offer = Optional.of(optionalOfferHost.get());
            return offer;
        } else if(offerGuestList.isPresent() && optionalOfferGuest.isPresent()) {
            Optional<Offer> offer = Optional.of(optionalOfferGuest.get());
            return offer;
        }
        return Optional.empty();
    }

}