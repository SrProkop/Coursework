package ru.sfedu.hospitalityNetwork.dataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import ru.sfedu.hospitalityNetwork.Constants;
import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.enums.HouseType;
import ru.sfedu.hospitalityNetwork.enums.Outcome;
import ru.sfedu.hospitalityNetwork.enums.Rating;
import ru.sfedu.hospitalityNetwork.filter.FilterByCountry;
import ru.sfedu.hospitalityNetwork.model.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class DataProviderTest {

    private static final Logger log = LogManager.getLogger(DataProviderTest.class);
    private static DataProvider dataProvider;
    private static User userOne;
    private static User userTwo;
    private static User userThree;
    private static User userFour;
    private static User userFive;
    private static OfferHost offerHostOne;
    private static OfferGuest offerGuestOne;
    private static Comment commentOne;
    private static Comment commentTwo;
    private static Response responseOne;

    static void setUp(DataProvider dataProviderInstance) {
        dataProvider = dataProviderInstance;

        dataProvider.deleteAll();
        dataProvider.initDB();

        dataProvider.createUser(
                Constants.TEST_USER_1_NAME,
                Constants.TEST_USER_1_COUNTRY,
                Constants.TEST_USER_1_CITY);
        dataProvider.createUser(
                Constants.TEST_USER_2_NAME,
                Constants.TEST_USER_2_COUNTRY,
                Constants.TEST_USER_2_CITY);
        dataProvider.createUser(
                Constants.TEST_USER_1_NAME,
                Constants.TEST_USER_1_COUNTRY,
                Constants.TEST_USER_1_CITY);
        dataProvider.createUser(
                Constants.TEST_USER_2_NAME,
                Constants.TEST_USER_2_COUNTRY,
                Constants.TEST_USER_2_CITY);
        dataProvider.createUser(
                Constants.TEST_USER_1_NAME,
                Constants.TEST_USER_1_COUNTRY,
                Constants.TEST_USER_1_CITY);


        userOne = dataProvider.getListUser().get().get(0);
        userTwo = dataProvider.getListUser().get().get(1);
        userThree = dataProvider.getListUser().get().get(2);
        userFour = dataProvider.getListUser().get().get(3);
        userFive = dataProvider.getListUser().get().get(4);

        dataProvider.createOfferHost(
                Constants.TEST_OFFER_HOST_NAME,
                Constants.TEST_OFFER_HOST_COUNTRY,
                Constants.TEST_OFFER_HOST_CITY,
                Constants.TEST_OFFER_HOST_ADDRESS,
                HouseType.ROOM,
                true,
                userTwo.getIdUser());

        dataProvider.createOfferGuest(
                Constants.TEST_OFFER_GUEST_NAME,
                Constants.TEST_OFFER_GUEST_COUNTRY,
                Constants.TEST_OFFER_GUEST_CITY,
                5,
                7,
                userOne.getIdUser());

        offerHostOne = dataProvider.getListOfferHosts(null).get().get(0);
        offerGuestOne = dataProvider.getListOfferGuests(null).get().get(0);

        dataProvider.createComment(Constants.TEST_COMMENT_1, userOne.getIdUser(), userTwo.getIdUser(), Rating.GOOD);
        dataProvider.createComment(Constants.TEST_COMMENT_2, userOne.getIdUser(), userTwo.getIdUser(), Rating.PERFECT);

        commentOne = dataProvider.getListComment().get().get(0);
        commentTwo = dataProvider.getListComment().get().get(1);

        dataProvider.createResponse(userOne.getIdUser(), offerHostOne.getIdOffer());

        responseOne = dataProvider.getListResponse().get().get(0);


    }

    /*User*/

    void createUserCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.createUser(
                        userOne.getName(),
                        userOne.getCountry(),
                        userOne.getCity())
        );
    }

    void createUserIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.createUser(
                null,
                userTwo.getCountry(),
                userTwo.getCity()));
    }

    void getUserByIdCorrect() {
        Optional<User> user = dataProvider.getUser(userTwo.getIdUser());
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(userTwo.getName(), user.get().getName());
        Assertions.assertEquals(userTwo.getCountry(), user.get().getCountry());
        Assertions.assertEquals(userTwo.getCity(), user.get().getCity());
    }

    void getUserByIdIncorrect() {
        Optional<User> user = dataProvider.getUser(UUID.randomUUID().toString());
        Assertions.assertFalse(user.isPresent());
    }

    void editUserCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.editUser(userFive.getIdUser(), userOne.getName(), userOne.getCountry(), userOne.getCity())
        );
    }

    void editUserIncorrect() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.editUser(
                userFive.getIdUser(),
                null,
                userFive.getCountry(),
                userFive.getCity()));
    }


    void deleteUserCorrect() {
        User user = userFour;

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.deleteUser(user.getIdUser()));
    }

    void deleteUserIncorrect() {
        Assertions.assertNotEquals(
                Outcome.SUCCESS,
                dataProvider.deleteUser(UUID.randomUUID().toString()));
    }

    void getListUserCorrect() {
        log.debug(dataProvider.getListUser());
    }

    void getListUserIncorrect() {
        Assertions.assertNotEquals(new ArrayList<>(), dataProvider.getListUser().get());   //???????
        log.debug(dataProvider.getListUser());
    }

    /*OfferHost*/

    void createOfferHostCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.createOfferHost(
                        offerHostOne.getName(),
                        offerHostOne.getCountry(),
                        offerHostOne.getCity(),
                        offerHostOne.getAddressHouse(),
                        offerHostOne.getHouseType(),
                        offerHostOne.isPersonalMeeting(),
                        userThree.getIdUser())
        );
    }

    void createOfferHostIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.createOfferHost(
                null,
                offerHostOne.getCountry(),
                offerHostOne.getCity(),
                offerHostOne.getAddressHouse(),
                offerHostOne.getHouseType(),
                offerHostOne.isPersonalMeeting(),
                UUID.randomUUID().toString())
        );
    }

    void getOfferHostByIdCorrect() {
        Optional<OfferHost> optionalOfferHost = dataProvider.getOfferHost(offerHostOne.getIdOffer());
        Assertions.assertTrue(optionalOfferHost.isPresent());
        Assertions.assertEquals(offerHostOne.getName(), optionalOfferHost.get().getName());
        Assertions.assertEquals(offerHostOne.getCountry(), optionalOfferHost.get().getCountry());
        Assertions.assertEquals(offerHostOne.getCity(), optionalOfferHost.get().getCity());
    }

    void getOfferHostByIdIncorrect() {
        Optional<OfferHost> optionalOfferHost = dataProvider.getOfferHost(UUID.randomUUID().toString());
        Assertions.assertFalse(optionalOfferHost.isPresent());
    }

    void editOfferHostCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.editOfferHost(offerHostOne.getIdOffer(),
                        offerHostOne.getName(),
                        offerHostOne.getCountry(),
                        offerHostOne.getCity(),
                        offerHostOne.getAddressHouse(),
                        offerHostOne.getHouseType(),
                        offerHostOne.isTypeOffer())
        );
    }

    void editOfferHostIncorrect() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.editOfferHost(
                offerHostOne.getIdOffer(),
                null,
                offerHostOne.getCountry(),
                offerHostOne.getCity(),
                offerHostOne.getAddressHouse(),
                offerHostOne.getHouseType(),
                offerHostOne.isTypeOffer()));
    }

    void deleteOfferHostCorrect() {
        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.deleteOfferHost(dataProvider.getListOfferHosts(null).get().get(0).getIdOffer()));
    }

    void deleteOfferHostIncorrect() {
        Assertions.assertNotEquals(
                Outcome.SUCCESS,
                dataProvider.deleteOfferHost(UUID.randomUUID().toString()));
    }

    void getListOfferHostsCorrect() {
        log.debug(dataProvider.getListOfferHosts(null));
    }

    void getListOfferHostsIncorrect() {
        Filter filter = new FilterByCountry("***");
        Assertions.assertNotEquals(true, dataProvider.getListOfferHosts(filter));
        log.debug(dataProvider.getListOfferHosts(filter));
    }


    /*OfferGuest*/

    void createOfferGuestCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.createOfferGuest(
                        offerGuestOne.getName(),
                        offerGuestOne.getCountry(),
                        offerGuestOne.getCity(),
                        offerGuestOne.getWeightBaggage(),
                        offerGuestOne.getNumberDay(),
                        userFour.getIdUser())
        );
    }

    void createOfferGuestIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.createOfferGuest(
                null,
                offerGuestOne.getCountry(),
                offerGuestOne.getCity(),
                offerGuestOne.getWeightBaggage(),
                offerGuestOne.getNumberDay(),
                dataProvider.getListUser().get().get(1).getIdUser())
        );
    }

    void getOfferGuestByIdCorrect() {
        Optional<OfferGuest> optionalOfferGuest = dataProvider.getOfferGuest(offerGuestOne.getIdOffer());
        Assertions.assertTrue(optionalOfferGuest.isPresent());
        Assertions.assertEquals(offerGuestOne.getName(), optionalOfferGuest.get().getName());
        Assertions.assertEquals(offerGuestOne.getCountry(), optionalOfferGuest.get().getCountry());
        Assertions.assertEquals(offerGuestOne.getCity(), optionalOfferGuest.get().getCity());
    }

    void getOfferGuestByIdIncorrect() {
        Optional<OfferGuest> optionalOfferGuest = dataProvider.getOfferGuest(UUID.randomUUID().toString());
        Assertions.assertFalse(optionalOfferGuest.isPresent());
    }

    void deleteOfferGuestCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.deleteOfferGuest(dataProvider.getListOfferGuests(null).get().get(0).getIdOffer()));
    }

    void editOfferGuestCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.editOfferGuest(offerGuestOne.getIdOffer(),
                        offerGuestOne.getName(),
                        offerGuestOne.getCountry(),
                        offerGuestOne.getCity(),
                        offerGuestOne.getWeightBaggage(),
                        offerGuestOne.getNumberDay())
        );
    }

    void editOfferGuestIncorrect() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.editOfferGuest(
                offerHostOne.getIdOffer(),
                null,
                offerHostOne.getCountry(),
                offerHostOne.getCity(),
                offerGuestOne.getWeightBaggage(),
                offerGuestOne.getNumberDay()));
    }

    void deleteOfferGuestIncorrect() {
        Assertions.assertNotEquals(
                Outcome.SUCCESS,
                dataProvider.deleteOfferHost(UUID.randomUUID().toString()));
    }

    void getListOfferGuestsCorrect() {
        log.debug(dataProvider.getListOfferGuests(null));
    }

    void getListOfferGuestsIncorrect() {
        Filter filter = new FilterByCountry("***");
        Assertions.assertNotEquals(true, dataProvider.getListOfferGuests(filter));
        log.debug(dataProvider.getListOfferGuests(filter));
    }

    /*Offer*/

    void getOfferUserCorrect() {
        Optional<Offer> offerUser = dataProvider.getOfferUser(dataProvider.getListOfferHosts(null).get().get(0).getIdUser());
        Assertions.assertTrue(offerUser.isPresent());
        Assertions.assertEquals(offerUser.get().getCountry(), offerHostOne.getCountry());
        Assertions.assertEquals(offerUser.get().getCity(), offerHostOne.getCity());
        Assertions.assertEquals(offerUser.get().isTypeOffer(), offerHostOne.isTypeOffer());
    }

    void getOfferUserIncorrect() {
        Optional<Offer> offerUser = dataProvider.getOfferUser(UUID.randomUUID().toString());
        Assertions.assertFalse(offerUser.isPresent());
    }

    /*Comment*/
    void createCommentCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.createComment(
                        Constants.TEST_COMMENT_2,
                        userThree.getIdUser(),
                        userTwo.getIdUser(),
                        Rating.PERFECT));
    }

    void createCommentIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.createComment(
                null,
                userOne.getIdUser(),
                userTwo.getIdUser(),
                Rating.GOOD));
    }

    void getCommentCorrect() {
        Optional<Comment> optionalComment = dataProvider.getComment(commentOne.getIdComment());
        Assertions.assertTrue(optionalComment.isPresent());
        Assertions.assertEquals(commentOne.getIdComment(), optionalComment.get().getIdComment());
        Assertions.assertEquals(commentOne.getIdUserTo(), optionalComment.get().getIdUserTo());
        Assertions.assertEquals(commentOne.getIdUserFrom(), optionalComment.get().getIdUserFrom());
        Assertions.assertEquals(commentOne.getComment(), optionalComment.get().getComment());
        Assertions.assertEquals(commentOne.getRating(), optionalComment.get().getRating());

    }

    void getCommentIncorrect() {
        Optional<Comment> optionalComment = dataProvider.getComment(UUID.randomUUID().toString());
        Assertions.assertFalse(optionalComment.isPresent());
    }

    void getListCommentCorrect() {
        log.debug(dataProvider.getListComment());
    }

    void getListCommentIncorrect() {
        Assertions.assertNotEquals(new ArrayList<>(), dataProvider.getListComment().get());
        log.debug(dataProvider.getListComment());
    }

    void getListCommentUserCorrect() {
        Assertions.assertNotEquals(new ArrayList<>(), dataProvider.getListCommentUser(userTwo.getIdUser()).get());
        log.debug(dataProvider.getListComment());
    }

    void getListCommentUserIncorrect() {
        Assertions.assertEquals(new ArrayList<>(), dataProvider.getListCommentUser(UUID.randomUUID().toString()).get());
        log.debug(dataProvider.getListComment());
    }

    void editCommentCorrect() {
        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.editComment(commentOne.getIdComment(), commentOne.getComment(), commentOne.getRating())
        );
    }

    void editCommentIncorrect() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.editComment(
                commentOne.getIdComment(),
                null,
                commentOne.getRating()));
    }

    void deleteCommentCorrect() {

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.deleteComment(commentTwo.getIdComment()));
    }

    void deleteCommentIncorrect() {
        Assertions.assertNotEquals(
                Outcome.SUCCESS,
                dataProvider.deleteComment(UUID.randomUUID().toString()));
    }

    /*Response*/

   void createResponseCorrect() {
       OfferHost offerHost = dataProvider.getListOfferHosts(null).get().get(0);

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.createResponse(
                        userOne.getIdUser(),
                        offerHost.getIdOffer()));
    }

    void createResponseIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> dataProvider.createResponse(
                null,
                offerHostOne.getIdOffer()));
    }

    void getResponseCorrect() {

        Optional<Response> optionalResponse = dataProvider.getResponse(responseOne.getIdResponse());
        Assertions.assertTrue(optionalResponse.isPresent());
        Assertions.assertEquals(responseOne.getIdResponse(), optionalResponse.get().getIdResponse());
        Assertions.assertEquals(responseOne.getIdOffer(), optionalResponse.get().getIdOffer());
        Assertions.assertEquals(responseOne.getIdUser(), optionalResponse.get().getIdUser());

    }

    void getResponseIncorrect() {
        Optional<Response> optionalResponse = dataProvider.getResponse(UUID.randomUUID().toString());
        Assertions.assertFalse(optionalResponse.isPresent());
    }

    void getListResponseCorrect() {
        log.debug(dataProvider.getListResponse());
    }

    void getListResponseIncorrect() {
        Assertions.assertNotEquals(new ArrayList<>(), dataProvider.getListResponse().get());
        log.debug(dataProvider.getListResponse());
    }

    void getListResponseOfferCorrect() {
       OfferHost offerHost = dataProvider.getListOfferHosts(null).get().get(0);

        Assertions.assertNotEquals(Optional.empty(), dataProvider.getListResponseOffer(offerHost.getIdOffer()));
        log.debug(dataProvider.getListResponseOffer(offerHostOne.getIdOffer()));
    }

    void getListResponseOfferIncorrect() {
        Assertions.assertEquals(Optional.empty(), dataProvider.getListResponseOffer(UUID.randomUUID().toString()));
        log.debug(dataProvider.getListResponseOffer(UUID.randomUUID().toString()));
    }

    void deleteResponseCorrect() {
       User user = dataProvider.getListUser().get().get(0);
       OfferHost offerHost = dataProvider.getListOfferHosts(null).get().get(0);
       dataProvider.createResponse(user.getIdUser(), offerHost.getIdOffer());

       Response response = dataProvider.getListResponse().get().get(0);

        Assertions.assertEquals(
                Outcome.SUCCESS,
                dataProvider.deleteResponse(response.getIdResponse()));
    }

    void deleteResponseIncorrect() {
        Assertions.assertNotEquals(
                Outcome.SUCCESS,
                dataProvider.deleteResponse(UUID.randomUUID().toString()));
    }

}
