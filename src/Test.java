

//import java.io.IOException;

import static org.junit.Assert.*;



public class Test {


    public static void main(String args[]) {

        Thread thread1 = new Thread (() -> {
            ChatClient.main(new String[0]);
        });

        Thread thread2 = new Thread (() -> {
            User testing = new User("test_2");
//            User testing2 = new User("user_2");
            test(testing);
//            test(testing2);
        });

        thread1.start();
        thread2.start();

    }

    public static void test (User client) {
        assertEquals("CLIENT TEST", "test_2", client.getName());
        assertEquals("TIME TEST", false, client.isTimeActive());
//        assertEquals("ID TEST", , client.getId());
    }


}
