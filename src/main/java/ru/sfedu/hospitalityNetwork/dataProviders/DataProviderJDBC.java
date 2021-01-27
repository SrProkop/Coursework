package ru.sfedu.hospitalityNetwork.dataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.sfedu.hospitalityNetwork.Constants;
import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.dataConvertors.CommentListConverter;
import ru.sfedu.hospitalityNetwork.enums.HouseType;
import ru.sfedu.hospitalityNetwork.enums.Outcome;
import ru.sfedu.hospitalityNetwork.enums.Rating;
import ru.sfedu.hospitalityNetwork.model.*;
import utils.PropertyProvider;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataProviderJDBC implements DataProvider {

    private static DataProvider INSTANCE = null;
    private static final Logger log = LogManager.getLogger(DataProviderJDBC.class);

    public DataProviderJDBC() {
    }

    public DataProviderJDBC(boolean isBoolean) {
        initDB();
    }

    public static DataProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataProviderJDBC();
        }
        return INSTANCE;
    }

    private Outcome executeQuery(String queryString) {
        log.info(queryString);
        try {
            PreparedStatement statement = connection().prepareStatement(queryString);
            statement.executeUpdate();
            statement.close();
            return Outcome.SUCCESS;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private ResultSet getResultSet(String queryString) {
        log.info(queryString);
        try {
            PreparedStatement statement = connection().prepareStatement(queryString);
            return statement.executeQuery();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            log.error(e);
            return null;
        }
    }

    private Connection connection() throws ClassNotFoundException, SQLException, IOException {
        Class.forName(PropertyProvider.getProperty(Constants.JDBC_DRIVER));
        return DriverManager.getConnection(
                PropertyProvider.getProperty(Constants.JDBC_URL),
                PropertyProvider.getProperty(Constants.JDBC_USER),
                PropertyProvider.getProperty(Constants.JDBC_PASSWORD));
    }

    @Override
    public void deleteAll() {
        executeQuery(Constants.CLEAR_BD);
    }

    @Override
    public void initDB() {
        try {
            String s;
            FileReader fr = new FileReader(new File(PropertyProvider.getProperty(Constants.JDBC_INIT_PATH)));
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                executeQuery(s);
            }
            br.close();
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Override
    public Outcome createUser(@NotNull String name, @NotNull String country, @NotNull String city) {
        try {
            String uuid = UUID.randomUUID().toString();
            Offer offer = new Offer();
            return executeQuery(
                    String.format(Constants.INSERT_USER,
                            uuid,
                            name,
                            country,
                            city,
                            offer.getIdOffer(),
                            new ArrayList<>().toString()
                    )
            );
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }
    @Override
    public Optional<List<User>> getListUser() {
        ResultSet resultSet = this.getResultSet(Constants.SELECT_USERS);
        try {
            if (resultSet != null && resultSet.next()) {
                List<User> list = new ArrayList<>();
                do {
                    Optional<Offer> offerOptional = getOfferUser(resultSet.getString(Constants.COLUMN_USER_ID));
                    Optional<List<Comment>> commentList = getListCommentUser(resultSet.getString(Constants.COLUMN_USER_ID));

                    User user = new User();
                    user.setIdUser(resultSet.getString(Constants.COLUMN_USER_ID));
                    user.setName(resultSet.getString(Constants.COLUMN_USER_NAME));
                    user.setCountry(resultSet.getString(Constants.COLUMN_USER_COUNTRY));
                    user.setCity(resultSet.getString(Constants.COLUMN_USER_CITY));
                    if (offerOptional.isPresent()) {
                        user.setOffer(offerOptional.get());
                    } else {
                        user.setOffer(new Offer());
                    }
                    if (commentList.isPresent() && commentList.get().size() > 0) {
                        user.setListCommentForUser(commentList.get());
                    } else {
                        user.setListCommentForUser(new ArrayList<>());
                    }
                    list.add(user);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.USER_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUser(@NotNull String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_USER, userId));
        try {
            if (resultSet != null && resultSet.next()) {
                Optional<Offer> offerOptional = getOfferUser(resultSet.getString(Constants.COLUMN_USER_ID));
                Optional<List<Comment>> commentList = getListCommentUser(resultSet.getString(Constants.COLUMN_USER_ID));

                User user = new User();
                user.setIdUser(resultSet.getString(Constants.COLUMN_USER_ID));
                user.setName(resultSet.getString(Constants.COLUMN_USER_NAME));
                user.setCountry(resultSet.getString(Constants.COLUMN_USER_COUNTRY));
                user.setCity(resultSet.getString(Constants.COLUMN_USER_CITY));
                if (offerOptional.isPresent()) {
                    user.setOffer(offerOptional.get());
                } else {
                    user.setOffer(new Offer());
                }
                if (commentList.isPresent() && commentList.get().size() > 0) {
                    user.setListCommentForUser(commentList.get());
                } else {
                    user.setListCommentForUser(new ArrayList<>());
                }
                return Optional.of(user);
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public  Outcome editUser(@NotNull String userId,
                             @NotNull String name,
                             @NotNull String country,
                             @NotNull String city) {
        Optional<User> userOptional = getUser(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(name);
            user.setCountry(country);
            user.setCity(city);
            userRewrite(user);
            return Outcome.SUCCESS;
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }


    @Override
    public Outcome deleteUser(@NotNull String userId) {
        try {
            Optional<User> user = getUser(userId);
            if (user.isPresent()) {
                executeQuery(String.format(Constants.DELETE_USER, userId));

                executeQuery(String.format(Constants.DELETE_COMMENT_FOR_USER, userId));

                if (user.get().getOffer().getIdOffer() != null) {
                    deleteOfferHost(user.get().getOffer().getIdOffer());
                    deleteOfferGuest(user.get().getOffer().getIdOffer());
                }

                Optional<List<Comment>> listCommentForUserTo = getListCommentForUserTo(userId);
                List<Comment> list = listCommentForUserTo.get();
                list.stream().forEach(comment -> deleteComment(comment.getIdComment()));

                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
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
        try {
            if (getUser(idUser).isPresent() && getUser(idUser).get().getOffer().getIdOffer() == null) {
                String uuid = UUID.randomUUID().toString();
                executeQuery(
                        String.format(Constants.INSERT_OFFER_HOST,
                                uuid,
                                name,
                                country,
                                city,
                                addressHouse,
                                houseType.toString(),
                                personalMeeting,
                                idUser,
                                true,
                                new ArrayList<>().toString()
                        )
                );
                OfferHost offerHost = getOfferHost(uuid).get();
                User user = getUser(idUser).get();
                user.setOffer(offerHost);
                userRewrite(user);
                return Outcome.SUCCESS;
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Outcome.FAILED;
            }
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<List<OfferHost>> getListOfferHosts(Filter filter) {
        ResultSet resultSet = this.getResultSet(Constants.SELECT_OFFER_HOSTS);
        try {
            if (resultSet != null && resultSet.next()) {
                List<OfferHost> list = new ArrayList<>();
                do {
                    Optional<User> userOptional = getUser(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID_USER));
                    Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID));
                    if (userOptional.isPresent()) {
                        OfferHost offer = new OfferHost();
                        offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID_USER));
                        offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID));
                        offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_HOST_TYPE_OFFER));
                        offer.setName(resultSet.getString(Constants.COLUMN_OFFER_HOST_NAME));
                        offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_HOST_COUNTRY));
                        offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_HOST_CITY));
                        offer.setPersonalMeeting(resultSet.getBoolean(Constants.COLUMN_OFFER_HOST_PERSONAL_MEETING));
                        offer.setAddressHouse(resultSet.getString(Constants.COLUMN_OFFER_HOST_ADDRESS_HOUSE));
                        offer.setHouseType(HouseType.valueOf(resultSet.getString(Constants.COLUMN_OFFER_HOST_HOUSE_TYPE)));
                        if (responseList.isPresent()) {
                            offer.setListResponse(responseList.get());
                        } else {
                            offer.setListResponse(new ArrayList<>());
                        }
                        list.add(offer);
                    } else {
                        log.error(Constants.USER_NOT_FOUND);
                    }
                } while (resultSet.next());
                if (filter == null) {
                    return Optional.of(list);
                }
                List<OfferHost> offerHostsListFilter = list;
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
                log.error(Constants.USER_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<OfferHost> getOfferHost(@NotNull String idOffer) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_OFFER_HOST, idOffer));
        try {
            if (resultSet != null && resultSet.next()) {
                Optional<User> userOptional = getUser(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID_USER));
                Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID));
                if (userOptional.isPresent()) {
                    OfferHost offer = new OfferHost();
                    offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID_USER));
                    offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_HOST_ID));
                    offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_HOST_TYPE_OFFER));
                    offer.setName(resultSet.getString(Constants.COLUMN_OFFER_HOST_NAME));
                    offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_HOST_COUNTRY));
                    offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_HOST_CITY));
                    offer.setPersonalMeeting(resultSet.getBoolean(Constants.COLUMN_OFFER_HOST_PERSONAL_MEETING));
                    offer.setAddressHouse(resultSet.getString(Constants.COLUMN_OFFER_HOST_ADDRESS_HOUSE));
                    offer.setHouseType(HouseType.valueOf(resultSet.getString(Constants.COLUMN_OFFER_HOST_HOUSE_TYPE)));
                    if (responseList.isPresent()) {
                        offer.setListResponse(responseList.get());
                    } else {
                        offer.setListResponse(new ArrayList<>());
                    }
                    return Optional.of(offer);
                } else {
                    log.error(Constants.USER_NOT_FOUND);
                    return Optional.empty();
                }
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public  Outcome editOfferHost(@NotNull String offerId,
                                  @NotNull String name,
                                  @NotNull String country,
                                  @NotNull String city,
                                  @NotNull String addressHouse,
                                  @NotNull HouseType houseType,
                                  @NotNull boolean personalMeeting) {
        Optional<OfferHost> offerHostOptional = getOfferHost(offerId);
        if (offerHostOptional.isPresent()) {
            OfferHost offerHost = offerHostOptional.get();
            offerHost.setName(name);
            offerHost.setCountry(country);
            offerHost.setCity(city);
            offerHost.setAddressHouse(addressHouse);
            offerHost.setHouseType(houseType);
            offerHost.setPersonalMeeting(personalMeeting);
            offerHostRewrite(offerHost);
            return Outcome.SUCCESS;
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteOfferHost(@NotNull String idOffer) {
        try {
            Optional<OfferHost> offerHost = getOfferHost(idOffer);
            if (offerHost.isPresent()) {
                executeQuery(String.format(Constants.DELETE_OFFER_HOST, idOffer));

                executeQuery(String.format(Constants.DELETE_RESPONSE_OFFER, idOffer));

                Optional<User> user = getUser(offerHost.get().getIdUser());
                if (user.isPresent()) {
                    Offer offer = new Offer();
                    user.get().setOffer(offer);
                    userRewrite(user.get());
                }

                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
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
        try {
            if (getUser(idUser).isPresent() && getUser(idUser).get().getOffer().getIdOffer() == null) {
                String uuid = UUID.randomUUID().toString();
                executeQuery(
                        String.format(Constants.INSERT_OFFER_GUEST,
                                uuid,
                                name,
                                country,
                                city,
                                weightBaggage,
                                numberDay,
                                idUser,
                                false,
                                new ArrayList<>().toString()
                        )
                );
                OfferGuest offerGuest = getOfferGuest(uuid).get();
                User user = getUser(idUser).get();
                user.setOffer(offerGuest);
                userRewrite(user);
                return Outcome.SUCCESS;
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Outcome.FAILED;
            }
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    @Override
    public Optional<List<OfferGuest>> getListOfferGuests(Filter filter) {
        ResultSet resultSet = this.getResultSet(Constants.SELECT_OFFER_GUESTS);
        try {
            if (resultSet != null && resultSet.next()) {
                List<OfferGuest> list = new ArrayList<>();
                do {
                    Optional<User> userOptional = getUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                    Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                    if (userOptional.isPresent()) {
                        OfferGuest offer = new OfferGuest();
                        offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                        offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                        offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_GUEST_TYPE_OFFER));
                        offer.setName(resultSet.getString(Constants.COLUMN_OFFER_GUEST_NAME));
                        offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_GUEST_COUNTRY));
                        offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_GUEST_CITY));
                        offer.setWeightBaggage(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_WEIGHT_BAGGAGE));
                        offer.setNumberDay(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_NUMBER_DAY));
                        if (responseList.isPresent()) {
                            offer.setListResponse(responseList.get());
                        } else {
                            offer.setListResponse(new ArrayList<>());
                        }
                        list.add(offer);
                    } else {
                        log.error(Constants.USER_NOT_FOUND);
                    }
                } while (resultSet.next());
                if (filter == null) {
                    return Optional.of(list);
                }
                List<OfferGuest> offerGuestListFilter = list;
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
                log.error(Constants.USER_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<OfferGuest> getOfferGuest(@NotNull String idOffer) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_OFFER_GUEST, idOffer));
        try {
            if (resultSet != null && resultSet.next()) {
                Optional<User> userOptional = getUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                if (userOptional.isPresent()) {
                    OfferGuest offer = new OfferGuest();
                    offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                    offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                    offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_GUEST_TYPE_OFFER));
                    offer.setName(resultSet.getString(Constants.COLUMN_OFFER_GUEST_NAME));
                    offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_GUEST_COUNTRY));
                    offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_GUEST_CITY));
                    offer.setWeightBaggage(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_WEIGHT_BAGGAGE));
                    offer.setNumberDay(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_NUMBER_DAY));
                    if (responseList.isPresent()) {
                        offer.setListResponse(responseList.get());
                    } else {
                        offer.setListResponse(new ArrayList<>());
                    }
                    return Optional.of(offer);
                } else {
                    log.error(Constants.USER_NOT_FOUND);
                    return Optional.empty();
                }
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public  Outcome editOfferGuest(@NotNull String offerId,
                                   @NotNull String name,
                                   @NotNull String country,
                                   @NotNull String city,
                                   @NotNull int weightBaggage,
                                   @NotNull int numberDay) {
        Optional<OfferGuest> offerGuestOptional = getOfferGuest(offerId);
        if (offerGuestOptional.isPresent()) {
            OfferGuest offerGuest = offerGuestOptional.get();
            offerGuest.setName(name);
            offerGuest.setCountry(country);
            offerGuest.setCity(city);
            offerGuest.setWeightBaggage(weightBaggage);
            offerGuest.setNumberDay(numberDay);
            offerGuestRewrite(offerGuest);
            return Outcome.SUCCESS;
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteOfferGuest(@NotNull String idOffer) {
        try {
            Optional<OfferGuest> offerGuest = getOfferGuest(idOffer);
            if (offerGuest.isPresent()) {
                executeQuery(String.format(Constants.DELETE_OFFER_GUEST, idOffer));

                executeQuery(String.format(Constants.DELETE_RESPONSE_OFFER, idOffer));
                Optional<User> user = getUser(offerGuest.get().getIdUser());
                if (user.isPresent()) {
                    Offer offer = new Offer();
                    user.get().setOffer(offer);
                    userRewrite(user.get());
                }

                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome createComment(@NotNull String comment,
                                 @NotNull String idUserFrom,
                                 @NotNull String idUserTo,
                                 @NotNull Rating rating) {
        try {
            Optional<User> userTo = getUser(idUserTo);
            Optional<User> userFrom = getUser(idUserFrom);
            if (userTo.isPresent() && userFrom.isPresent()) {
                String uuid = UUID.randomUUID().toString();
                executeQuery(
                        String.format(Constants.INSERT_COMMENT,
                                uuid,
                                comment,
                                idUserFrom,
                                idUserTo,
                                rating.toString()
                        )
                );
                Comment commentUser = getComment(uuid).get();
                User user = userTo.get();
                user.getListCommentForUser().add(commentUser);
                userRewrite(user);
                return Outcome.SUCCESS;
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Outcome.FAILED;
            }

        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }
    @Override
    public Optional<List<Comment>> getListComment() {
        ResultSet resultSet = this.getResultSet(Constants.SELECT_COMMENTS);
        try {
            if (resultSet != null && resultSet.next()) {
                List<Comment> list = new ArrayList<>();
                do {
                    Comment comment = new Comment();
                    comment.setIdComment(resultSet.getString(Constants.COLUMN_COMMENT_ID_COMMENT));
                    comment.setComment(resultSet.getString(Constants.COLUMN_COMMENT_NAME));
                    comment.setIdUserFrom(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_FROM));
                    comment.setIdUserTo(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_TO));
                    comment.setRating(Rating.valueOf(resultSet.getString(Constants.COLUMN_COMMENT_RATING)));
                    list.add(comment);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.COMMENT_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Comment> getComment(@NotNull String idComment) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_COMMENT_BY_ID, idComment));
        try {
            if (resultSet != null && resultSet.next()) {
                Comment comment = new Comment();
                comment.setIdComment(resultSet.getString(Constants.COLUMN_COMMENT_ID_COMMENT));
                comment.setComment(resultSet.getString(Constants.COLUMN_COMMENT_NAME));
                comment.setIdUserFrom(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_FROM));
                comment.setIdUserTo(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_TO));
                comment.setRating(Rating.valueOf(resultSet.getString(Constants.COLUMN_COMMENT_RATING)));
                return Optional.of(comment);
            } else {
                log.error(Constants.COMMENT_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public  Outcome editComment(@NotNull String idComment,
                                @NotNull String comment,
                                @NotNull Rating rating) {
        Optional<Comment> commentOptional = getComment(idComment);
        if (commentOptional.isPresent()) {
            Comment newComment = commentOptional.get();
            newComment.setComment(comment);
            newComment.setRating(rating);
            commentRewrite(newComment);
            return Outcome.SUCCESS;
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome deleteComment(@NotNull String commentId) {
        try {
            Optional<Comment> comment = getComment(commentId);
            if (comment.isPresent()) {
                executeQuery(String.format(Constants.DELETE_COMMENT, commentId));
                Optional<User> user = getUser(comment.get().getIdUserTo());
                if (user.isPresent()) {
                    user.get().getListCommentForUser().remove(comment.get());
                    userRewrite(user.get());
                }

                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    @Override
    public Outcome createResponse(@NotNull String idUser,
                                  @NotNull String idOffer) {
        try {
            Optional<Offer> offerOptional = getOffer(idOffer);
            String uuid = UUID.randomUUID().toString();
            executeQuery(
                    String.format(Constants.INSERT_RESPONSE,
                            uuid,
                            idUser,
                            idOffer
                    )
            );
            Response response = getResponse(uuid).get();
            if (offerOptional.get().isTypeOffer()) {
                OfferHost offerHost = getOfferHost(idOffer).get();
                offerHost.getListResponse().add(response);
                offerHostRewrite(offerHost);
            } else {
                OfferGuest offerGuest = getOfferGuest(idOffer).get();
                offerGuest.getListResponse().add(response);
                offerGuestRewrite(offerGuest);
            }
            return Outcome.SUCCESS;

        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }
    @Override
    public Optional<List<Response>> getListResponse() {
        ResultSet resultSet = this.getResultSet(Constants.SELECT_RESPONSES);
        try {
            if (resultSet != null && resultSet.next()) {
                List<Response> list = new ArrayList<>();
                do {
                    Response response = new Response();
                    response.setIdResponse(resultSet.getString(Constants.COLUMN_RESPONSE_ID));
                    response.setIdOffer(resultSet.getString(Constants.COLUMN_RESPONSE_ID_OFFER));
                    response.setIdUser(resultSet.getString(Constants.COLUMN_RESPONSE_ID_USER));
                    list.add(response);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.RESPONSE_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Response> getResponse(@NotNull String idResponse) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_RESPONSE_BY_ID, idResponse));
        try {
            if (resultSet != null && resultSet.next()) {
                Response response = new Response();
                response.setIdResponse(resultSet.getString(Constants.COLUMN_RESPONSE_ID));
                response.setIdOffer(resultSet.getString(Constants.COLUMN_RESPONSE_ID_OFFER));
                response.setIdUser(resultSet.getString(Constants.COLUMN_RESPONSE_ID_USER));
                return Optional.of(response);
            } else {
                log.error(Constants.COMMENT_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Outcome deleteResponse(@NotNull String responseId) {
        try {
            Optional<Response> response = getResponse(responseId);
            if (response.isPresent()) {
                executeQuery(String.format(Constants.DELETE_RESPONSE, responseId));

                Optional<Offer> offer = getOffer(response.get().getIdOffer());
                if (offer.isPresent()) {
                    if (offer.get().isTypeOffer()) {
                        Optional<OfferHost> offerHost = getOfferHost(offer.get().getIdOffer());
                        offerHost.get().getListResponse().remove(response);
                        offerHostRewrite(offerHost.get());
                    } else {
                        Optional<OfferGuest> offerGuest = getOfferGuest(offer.get().getIdOffer());
                        offerGuest.get().getListResponse().remove(response);
                        offerGuestRewrite(offerGuest.get());
                    }
                }
                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }


    @Override
    public Optional<Offer> getOfferUser(@NotNull String userId) {
        Optional<Offer> offerOne = getOfferHostUser(userId);
        Optional<Offer> offerTwo = getOfferGuestUser(userId);
        if (offerOne.isPresent()) {
            return offerOne;
        } else if (offerTwo.isPresent()) {
            return offerTwo;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Comment>> getListCommentUser(@NotNull String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_COMMENT_BY_USER, userId));
        try {
            if (resultSet != null && resultSet.next()) {
                List<Comment> list = new ArrayList<>();
                do {
                    Comment comment = new Comment();
                    comment.setIdComment(resultSet.getString(Constants.COLUMN_COMMENT_ID_COMMENT));
                    comment.setComment(resultSet.getString(Constants.COLUMN_COMMENT_NAME));
                    comment.setIdUserFrom(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_FROM));
                    comment.setIdUserTo(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_TO));
                    comment.setRating(Rating.valueOf(resultSet.getString(Constants.COLUMN_COMMENT_RATING)));
                    list.add(comment);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.COMMENT_LIST_NOT_FOUND);
                return Optional.of(new ArrayList<>());
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Response>> getListResponseOffer (@NotNull String offerId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_RESPONSE_BY_OFFER, offerId));
        try {
            if (resultSet != null && resultSet.next()) {
                List<Response> list = new ArrayList<>();
                do {
                    Response response = new Response();
                    response.setIdResponse(resultSet.getString(Constants.COLUMN_RESPONSE_ID));
                    response.setIdOffer(resultSet.getString(Constants.COLUMN_RESPONSE_ID_OFFER));
                    response.setIdUser(resultSet.getString(Constants.COLUMN_RESPONSE_ID_USER));
                    list.add(response);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.RESPONSE_LIST_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private boolean isUser (@NotNull String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_USER, userId));
        try {
            if (resultSet != null && resultSet.next()) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e) {
            log.error(e);
            return false;
        }
    }

    private Outcome userRewrite (@NotNull User user) {
        Optional<User> optionalUser = getUser(user.getIdUser());
        if (optionalUser.isPresent()) {
            deleteUserForRewrite(user.getIdUser());

            return executeQuery(
                    String.format(Constants.INSERT_USER,
                            user.getIdUser(),
                            user.getName(),
                            user.getCountry(),
                            user.getCity(),
                            user.getOffer().getIdOffer(),
                            listToStringForComment(user.getListCommentForUser())
                    )
            );
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    private Outcome commentRewrite (@NotNull Comment comment) {
        Optional<Comment> optionalComment = getComment(comment.getIdComment());
        if (optionalComment.isPresent()) {
            deleteCommentForRewrite(comment.getIdComment());
            return executeQuery(
                    String.format(Constants.INSERT_COMMENT,
                            comment.getIdComment(),
                            comment.getComment(),
                            comment.getIdUserFrom(),
                            comment.getIdUserTo(),
                            comment.getRating().toString()
                    )
            );
        } else {
            log.error(Constants.USER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    private Outcome offerHostRewrite (@NotNull OfferHost offerHost) {
        Optional<OfferHost> optionalOfferHost = getOfferHost(offerHost.getIdOffer());
        if (optionalOfferHost.isPresent()) {
            deleteOfferHostFOrRewrite(offerHost.getIdOffer());
            return executeQuery(
                    String.format(Constants.INSERT_OFFER_HOST,
                            offerHost.getIdOffer(),
                            offerHost.getName(),
                            offerHost.getCountry(),
                            offerHost.getCity(),
                            offerHost.getAddressHouse(),
                            offerHost.getHouseType().toString(),
                            offerHost.isPersonalMeeting(),
                            offerHost.getIdUser(),
                            offerHost.isTypeOffer(),
                            listToStringForResponse(offerHost.getListResponse())
                    )
            );
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    private Outcome offerGuestRewrite (@NotNull OfferGuest offerGuest) {
        Optional<OfferGuest> optionalOfferGuest = getOfferGuest(offerGuest.getIdOffer());
        if (optionalOfferGuest.isPresent()) {
            deleteOfferGuestForRewrite(offerGuest.getIdOffer());
            return executeQuery(
                    String.format(Constants.INSERT_OFFER_GUEST,
                            offerGuest.getIdOffer(),
                            offerGuest.getName(),
                            offerGuest.getCountry(),
                            offerGuest.getCity(),
                            offerGuest.getWeightBaggage(),
                            offerGuest.getNumberDay(),
                            offerGuest.getIdUser(),
                            offerGuest.isTypeOffer(),
                            listToStringForResponse(offerGuest.getListResponse())
                    )
            );
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Outcome.FAILED;
        }
    }

    private Optional<Offer> getOffer (@NotNull String idOffer) {
        Optional<OfferHost> offerHost = getOfferHost(idOffer);
        Optional<OfferGuest> offerGuest = getOfferGuest(idOffer);
        if (offerHost.isPresent()) {
            Offer offer;
            offer = offerHost.get();
            return Optional.of(offer);
        } else if (offerGuest.isPresent()) {
            Offer offer;
            offer = offerGuest.get();
            return Optional.of(offer);
        } else {
            log.error(Constants.OFFER_NOT_FOUND);
            return Optional.empty();
        }
    }

    private Outcome deleteUserForRewrite(@NotNull String userId) {
        try {
            Optional<User> user = getUser(userId);
            if (user.isPresent()) {
                executeQuery(String.format(Constants.DELETE_USER, userId));
                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteCommentForRewrite(@NotNull String idComment) {
        try {
            Optional<Comment> comment = getComment(idComment);
            if (comment.isPresent()) {
                executeQuery(String.format(Constants.DELETE_COMMENT, idComment));
                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteOfferHostFOrRewrite(@NotNull String idOffer) {
        try {
            Optional<OfferHost> offerHost = getOfferHost(idOffer);
            if (offerHost.isPresent()) {
                executeQuery(String.format(Constants.DELETE_OFFER_HOST, idOffer));
                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Outcome deleteOfferGuestForRewrite(@NotNull String idOffer) {
        try {
            Optional<OfferGuest> offerGuest = getOfferGuest(idOffer);
            if (offerGuest.isPresent()) {
                executeQuery(String.format(Constants.DELETE_OFFER_GUEST, idOffer));
                return Outcome.SUCCESS;
            }
            return Outcome.FAILED;
        } catch (Exception e) {
            log.error(e);
            return Outcome.FAILED;
        }
    }

    private Optional<Offer> getOfferHostUser(@NotNull String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_OFFER_HOST_USER, userId));
        try {
            if (resultSet != null && resultSet.next()) {
                Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                if (isUser(userId)) {
                    OfferHost offer = new OfferHost();
                    offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                    offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                    offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_GUEST_TYPE_OFFER));
                    offer.setName(resultSet.getString(Constants.COLUMN_OFFER_GUEST_NAME));
                    offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_GUEST_COUNTRY));
                    offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_GUEST_CITY));
                    offer.setPersonalMeeting(resultSet.getBoolean(Constants.COLUMN_OFFER_HOST_PERSONAL_MEETING));
                    offer.setAddressHouse(resultSet.getString(Constants.COLUMN_OFFER_HOST_ADDRESS_HOUSE));
                    offer.setHouseType(HouseType.valueOf(resultSet.getString(Constants.COLUMN_OFFER_HOST_HOUSE_TYPE)));
                    if (responseList.isPresent()) {
                        offer.setListResponse(responseList.get());
                    } else {
                        offer.setListResponse(new ArrayList<>());
                    }
                    return Optional.of(offer);
                } else {
                    log.error(Constants.USER_NOT_FOUND);
                    return Optional.empty();
                }
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Optional<Offer> getOfferGuestUser(@NotNull String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_OFFER_GUEST_USER, userId));
        try {
            if (resultSet != null && resultSet.next()){
                Optional<List<Response>> responseList = getListResponseOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                if (isUser(userId)) {
                    OfferGuest offer = new OfferGuest();
                    offer.setIdUser(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID_USER));
                    offer.setIdOffer(resultSet.getString(Constants.COLUMN_OFFER_GUEST_ID));
                    offer.setTypeOffer(resultSet.getBoolean(Constants.COLUMN_OFFER_GUEST_TYPE_OFFER));
                    offer.setName(resultSet.getString(Constants.COLUMN_OFFER_GUEST_NAME));
                    offer.setCountry(resultSet.getString(Constants.COLUMN_OFFER_GUEST_COUNTRY));
                    offer.setCity(resultSet.getString(Constants.COLUMN_OFFER_GUEST_CITY));
                    offer.setWeightBaggage(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_WEIGHT_BAGGAGE));
                    offer.setNumberDay(resultSet.getInt(Constants.COLUMN_OFFER_GUEST_NUMBER_DAY));
                    if (responseList.isPresent()) {
                        offer.setListResponse(responseList.get());
                    } else {
                        offer.setListResponse(new ArrayList<>());
                    }
                    return Optional.of(offer);
                } else {
                    log.error(Constants.USER_NOT_FOUND);
                    return Optional.empty();
                }
            } else {
                log.error(Constants.USER_NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Optional<List<Comment>> getListCommentForUserTo(String userId) {
        ResultSet resultSet = this.getResultSet(String.format(Constants.SELECT_COMMENT_BY_USER_FROM, userId));
        try {
            if (resultSet != null && resultSet.next()) {
                List<Comment> list = new ArrayList<>();
                do {
                    Comment comment = new Comment();
                    comment.setIdComment(resultSet.getString(Constants.COLUMN_COMMENT_ID_COMMENT));
                    comment.setComment(resultSet.getString(Constants.COLUMN_COMMENT_NAME));
                    comment.setIdUserFrom(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_FROM));
                    comment.setIdUserTo(resultSet.getString(Constants.COLUMN_COMMENT_ID_USER_TO));
                    comment.setRating(Rating.valueOf(resultSet.getString(Constants.COLUMN_COMMENT_RATING)));
                    list.add(comment);
                } while (resultSet.next());
                return Optional.of(list);
            } else {
                log.error(Constants.COMMENT_LIST_NOT_FOUND);
                return Optional.of(new ArrayList<>());
            }
        } catch (SQLException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private String listToStringForComment(List<Comment> commentList) {
        try {
            StringBuilder builder = new StringBuilder(Constants.FIRST_SYMBOL);
            if (commentList.size() > 0) {
                commentList.forEach(comment -> builder.append(comment.getIdComment()).append(Constants.DELIMITER_SYMBOL));
                builder.delete(builder.length() - 2, builder.length());
            }
            builder.append(Constants.LAST_SYMBOL);
            return builder.toString();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    private String listToStringForResponse(List<Response> responseList) {
        try {
            StringBuilder builder = new StringBuilder(Constants.FIRST_SYMBOL);
            if (responseList.size() > 0) {
                responseList.forEach(response -> builder.append(response.getIdResponse()).append(Constants.DELIMITER_SYMBOL));
                builder.delete(builder.length() - 2, builder.length());
            }
            builder.append(Constants.LAST_SYMBOL);
            return builder.toString();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

}
