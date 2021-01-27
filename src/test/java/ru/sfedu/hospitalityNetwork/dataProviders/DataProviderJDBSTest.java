package ru.sfedu.hospitalityNetwork.dataProviders;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DataProviderJDBSTest extends DataProviderTest {

    private static final DataProvider dataProvider = DataProviderJDBC.getInstance();

    @BeforeAll
    static void setUp() {
        setUp(dataProvider);
    }

    @Test
    void createUserCorrect() {
        super.createUserCorrect();
    }

    @Test
    void createUserIncorrect() {
        super.createUserIncorrect();
    }

    @Test
    void getUserByIdCorrect() {
        super.getUserByIdCorrect();
    }

    @Test
    void getUserByIdIncorrect() {
        super.getUserByIdIncorrect();
    }

    @Test
    void editUserCorrect() {
        super.editUserCorrect();
    }

    @Test
    void editUserIncorrect() {
        super.editUserIncorrect();
    }

    @Test
    void deleteUserCorrect() {
        super.deleteUserCorrect();
    }

    @Test
    void deleteUserIncorrect() {
        super.deleteUserIncorrect();
    }

    @Test
    void getListUserCorrect() {
        super.getListUserCorrect();
    }

    @Test
    void getListUserIncorrect() {
        super.getListUserIncorrect();
    }

    @Test
    void createOfferHostCorrect() {
        super.createOfferHostCorrect();
    }

    @Test
    void createOfferHostIncorrect() {
        super.createOfferHostIncorrect();
    }

    @Test
    void getOfferHostByIdCorrect() {
        super.getOfferHostByIdCorrect();
    }

    @Test
    void getOfferHostByIdIncorrect() {
        super.getOfferHostByIdIncorrect();
    }

    @Test
    void editOfferHostCorrect() {
        super.editOfferHostCorrect();
    }

    @Test
    void editOfferHostIncorrect() {
        super.editOfferHostIncorrect();
    }

    @Test
    void deleteOfferHostCorrect() {
        super.deleteOfferHostCorrect();
    }

    @Test
    void deleteOfferHostIncorrect() {
        super.deleteOfferHostIncorrect();
    }

    @Test
    void getListOfferHostsCorrect() {
        super.getListOfferHostsCorrect();
    }

    @Test
    void getListOfferHostsIncorrect() {
        super.getListOfferHostsIncorrect();
    }

    @Test
    void createOfferGuestCorrect() {
        super.createOfferGuestCorrect();
    }

    @Test
    void createOfferGuestIncorrect() {
        super.createOfferGuestIncorrect();
    }

    @Test
    void getOfferGuestByIdCorrect() {
        super.getOfferGuestByIdCorrect();
    }

    @Test
    void getOfferGuestByIdIncorrect() {
        super.getOfferGuestByIdIncorrect();
    }

    @Test
    void editOfferGuestCorrect() {
        super.editOfferGuestCorrect();
    }

    @Test
    void editOfferGuestIncorrect() {
        super.editOfferGuestIncorrect();
    }

    @Test
    void deleteOfferGuestCorrect() {
        super.deleteOfferGuestCorrect();
    }

    @Test
    void deleteOfferGuestIncorrect() {
        super.deleteOfferGuestIncorrect();
    }

    @Test
    void getListOfferGuestsCorrect() {
        super.getListOfferGuestsCorrect();
    }

    @Test
    void getListOfferGuestsIncorrect() {
        super.getListOfferGuestsIncorrect();
    }

    @Test
    void getOfferUserCorrect() {
        super.getOfferUserCorrect();
    }
    @Test
    void getOfferUserIncorrect() {
        super.getOfferUserIncorrect();
    }

    @Test
    void createCommentCorrect() {
        super.createCommentCorrect();
    }

    @Test
    void createCommentIncorrect() {
        super.createCommentIncorrect();
    }

    @Test
    void getCommentCorrect() {
        super.getCommentCorrect();
    }

    @Test
    void getCommentIncorrect() {
        super.getCommentIncorrect();
    }

    @Test
    void getListCommentCorrect() {
        super.getListCommentCorrect();
    }

    @Test
    void getListCommentIncorrect() {
        super.getListCommentIncorrect();
    }

    @Test
    void getListCommentUserCorrect() {
        super.getListCommentUserCorrect();
    }

    @Test
    void getListCommentUserIncorrect() {
        super.getListCommentUserIncorrect();
    }

    @Test
    void editCommentCorrect() {
        super.editCommentCorrect();
    }

    @Test
    void editCommentIncorrect() {
        super.editCommentIncorrect();
    }

    @Test
    void deleteCommentCorrect() {
        super.deleteCommentCorrect();
    }

    @Test
    void deleteCommentIncorrect() {
        super.deleteCommentIncorrect();
    }

    @Test
    void createResponseCorrect() {
        super.createResponseCorrect();
    }

    @Test
    void createResponseIncorrect() {
        super.createResponseIncorrect();
    }

    @Test
    void getResponseCorrect() {
        super.getResponseCorrect();
    }

    @Test
    void getResponseIncorrect() {
        super.getResponseIncorrect();
    }

    @Test
    void getListResponseCorrect() {
        super.getListResponseCorrect();
    }

    @Test
    void getListResponseIncorrect() {
        super.getListResponseIncorrect();
    }

    @Test
    void getListResponseOfferCorrect() {
        super.getListResponseOfferCorrect();
    }

    @Test
    void getListResponseOfferIncorrect() {
        super.getListResponseOfferIncorrect();
    }

    @Test
    void deleteResponseCorrect() {
        super.deleteResponseCorrect();
    }

    @Test
    void deleteResponseIncorrect() {
        super.deleteResponseIncorrect();
    }

}
