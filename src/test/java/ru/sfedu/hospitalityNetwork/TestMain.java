package ru.sfedu.hospitalityNetwork;
import org.junit.jupiter.api.Test;

public class TestMain {

    @Test
    void createUserCorrect() {
        Main.main(new String[]{"CSV", "CREATE_USER", "Alex,Russia,Moscow"});
        Main.main(new String[]{"XML", "CREATE_USER", "Alex,Russia,Moscow"});
        Main.main(new String[]{"JDBC", "CREATE_USER", "Alex,Russia,Moscow"});
    }

    @Test
    void createUserIncorrect() {
        Main.main(new String[]{"CSV", "CREATE_USER", "Alex,Russia"});
        Main.main(new String[]{"XML", "CREATE_USER", "Alex,Russia"});
        Main.main(new String[]{"JDBC", "CREATE_USER", "Alex,Russia"});
    }

    @Test
    void getUsers() {
        Main.main(new String[]{"CSV", "GET_USERS"});
        Main.main(new String[]{"XML", "GET_USERS"});
        Main.main(new String[]{"JDBC", "GET_USERS"});
    }

}
