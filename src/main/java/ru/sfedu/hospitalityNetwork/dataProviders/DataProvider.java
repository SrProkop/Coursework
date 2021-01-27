package ru.sfedu.hospitalityNetwork.dataProviders;

import ru.sfedu.hospitalityNetwork.Filter;
import ru.sfedu.hospitalityNetwork.filter.*;
import org.jetbrains.annotations.NotNull;
import ru.sfedu.hospitalityNetwork.enums.HouseType;
import ru.sfedu.hospitalityNetwork.enums.Outcome;
import ru.sfedu.hospitalityNetwork.enums.Rating;
import ru.sfedu.hospitalityNetwork.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataProvider {

    /**
     * Очиститка бд
     */
    void deleteAll();

    /**
     * Инициализация
     */
    void initDB();

    /*User*/

    /**
     * Метод для создания пользователя
     * @param name имя пользователя
     * @param country страна в котором проживает пользователь
     * @param city город в котором проживает пользователь
     * @return Outcome
     */
    Outcome createUser(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city);

    /**
     * Получить нужного нам пользователя по индентификатору
     * @param userId идентификатор конкретного пользователя
     * @return модель пользователя
     */
    Optional<User> getUser(@NotNull String userId);

    /**
     * Получить список всех пользователей
     */
    Optional<List<User>> getListUser();

    /**
     * Метод для удаления пользователя
     * @param userId идентификатор конкретного пользователя, которого нужно отредактировать
     * @param name имя пользователя
     * @param country страна в котором проживает пользователь
     * @param city город в котором проживает пользователь
     * @return Outcome
     */
    Outcome editUser(@NotNull String userId,
                     @NotNull String name,
                     @NotNull String country,
                     @NotNull String city);

    /**
     * Метод для удаления пользователя
     * @param userId идентификатор конкретного пользователя
     * @return Outcome
     */
    Outcome deleteUser(@NotNull String userId);


    /*OfferHost*/
    /**
     * Метод для создания предложения от хоста
     * @param name название предложения
     * @param country страна в которой предлагается остановиться
     * @param city город в котором предлагается остановиться
     * @param addressHouse адресс дома в котором предлагают остановиться
     * @param houseType тип дома в котором предлагают остановиться
     * @param personalMeeting возможность лично встретить в аэропорту/на вокзале при приезде
     * @param idUser идентификатор пользователя, создавшего сделку
     * @return Outcome
     */
    Outcome createOfferHost(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull String addressHouse,
            @NotNull HouseType houseType,
            @NotNull boolean personalMeeting,
            @NotNull String idUser);


    /**
     * Получить нужное нам предложение хоста
     * @param idOffer идентификатор конкретного предложения
     * @return модель предложения
     */
    Optional<OfferHost> getOfferHost(@NotNull String idOffer);

    /**
     * Метод для удаления пользователя
     * @param offerId идентификатор конкретного предложения от хоста, которое нужно отредактировать
     * @param name название предложения
     * @param country страна в которой предлагается остановиться
     * @param city город в котором предлагается остановиться
     * @param addressHouse адресс дома в котором предлагают остановиться
     * @param houseType тип дома в котором предлагают остановиться
     * @param personalMeeting возможность лично встретить в аэропорту/на вокзале при приезде
     * @return Outcome
     */
    Outcome editOfferHost(@NotNull String offerId,
                          @NotNull String name,
                          @NotNull String country,
                          @NotNull String city,
                          @NotNull String addressHouse,
                          @NotNull HouseType houseType,
                          @NotNull boolean personalMeeting);

    /**
     * Удалить ненужное предложение хоста
     * @param idOffer идентификатор конкретного пользователя
     * @return Outcome
     */
    Outcome deleteOfferHost(@NotNull String idOffer);

    /**
     * Получить все предложения от хостов
     * @param filter объект абстрактного класса фильтр c методом фильтрации нашего списка
     * @return список объектов предложений от хостов
     */
    Optional<List<OfferHost>> getListOfferHosts(Filter filter);

    /*OfferGuest*/
    /**
     * Метод для создания предложения гостя
     * @param name название предложения
     * @param country страна в которой хотят остановиться
     * @param city город в котором хотят остановиться
     * @param weightBaggage вес багажа
     * @param numberDay количество дней, на сколько хотят приехать
     * @param idUser идентификатор пользователя, создавшего сделку
     * @return Outcome
     */
    Outcome createOfferGuest(
            @NotNull String name,
            @NotNull String country,
            @NotNull String city,
            @NotNull int weightBaggage,
            @NotNull int numberDay,
            @NotNull String idUser);


    /**
     * Получить нужное нам предложение гостя
     * @param idOffer идентификатор конкретного предложения
     * @return модель предложения
     */
    Optional<OfferGuest> getOfferGuest(@NotNull String idOffer);

    /**
     * Метод для удаления пользователя
     * @param offerId идентификатор конкретного предложения от хоста, которое нужно отредактировать
     * @param name название предложения
     * @param country страна в которой предлагается остановиться
     * @param city город в котором предлагается остановиться
     * @param weightBaggage вес багажа
     * @param numberDay количество дней, на сколько хотят приехать
     * @return Outcome
     */
    Outcome editOfferGuest(@NotNull String offerId,
                           @NotNull String name,
                           @NotNull String country,
                           @NotNull String city,
                           @NotNull int weightBaggage,
                           @NotNull int numberDay);


    /**
     * Удалить не нужное предложение гостя
     * @param idOffer идентификатор конкретного пользователя
     * @return Outcome
     */

    Outcome deleteOfferGuest(@NotNull String idOffer);

    /**
     * Получить все предложения от хостов
     * @param filter объект абстрактного класса фильтр c методом фильтрации нашего списка
     * @return список объектов предложений от хостов
     */
    Optional<List<OfferGuest>> getListOfferGuests(Filter filter);

    /*Offer*/
    /**
     * Получить предложение пользователя
     * @param idUser идентификатор конкретного предложения
     * @return модель предложения
     */
    Optional<Offer> getOfferUser(@NotNull String idUser);

    /*Comment*/

    /**
     * Метод для создания нового комментария
     * @param comment комментарий к пользователю
     * @param idUserFrom идентификатор пользователя, от которого поступил комментарий
     * @param idUserTo идентификатор пользователя, которому поступил комментарий
     * @param rating оценка пользователя
     * @return Outcome
     */
    Outcome createComment(
            @NotNull String comment,
            @NotNull String idUserFrom,
            @NotNull String idUserTo,
            @NotNull Rating rating);

    /**
     * Запросить комментарий
     * @param idComment идентификатор конкретного комментария
     * @return модель комментария
     */
    Optional<Comment> getComment(@NotNull String idComment);

    /**
     * Запросить список комментариев пользователя
     * @return модель списка комментариев
     */
    Optional<List<Comment>> getListComment();

    /**
     * Запросить список комментариев пользователя
     * @param idUser идентификатор конкретного пользователя
     * @return модель списка комментариев
     */
    Optional<List<Comment>> getListCommentUser(@NotNull String idUser);

    /**
     * Метод для редактирования комментария
     * @param idComment идентификатор комментария, который нужно отредактировать
     * @param comment новый комментарий
     * @param rating новая оценка пользователя
     * @return Outcome
     */
    Outcome editComment(
            @NotNull String idComment,
            @NotNull String comment,
            @NotNull Rating rating);


    /**
     * Метод для удаления комментария
     * @param idComment идентификатор конкретного пользователя
     * @return Outcome
     */
    Outcome deleteComment(@NotNull String idComment);

    /*Response*/

    /**
     * Метод для создания отклика на предложение
     * @param idUser идентификатор пользователя, от которого поступил отклик
     * @param idOffer идентификатор предложения, для которого поступил отклик
     * @return Outcome
     */
    Outcome createResponse(
            @NotNull String idUser,
            @NotNull String idOffer);

    /**
     * Запросить отзыв
     * @param idResponse идентификатор конкретного комментария
     * @return модель комментария
     */
    Optional<Response> getResponse(@NotNull String idResponse);

    /**
     * Запросить список отзывов
     * @return модель комментария
     */
    Optional<List<Response>> getListResponse();

    /**
     * Запросить список откликов для предложения
     * @param idOffer идентификатор конкретного предложения
     * @return модель списка комментариев
     */
    Optional<List<Response>> getListResponseOffer(@NotNull String idOffer);

    /**
     * Метод для удаления отклика
     * @param idResponse идентификатор конкретного пользователя
     * @return Outcome
     */
    Outcome deleteResponse(@NotNull String idResponse);

}