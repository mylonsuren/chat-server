

import java.awt.event.KeyEvent;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.TimeUnit;

public class Testing {



    public static void main (String args[]) {



        Thread chatServer = new Thread(() -> {
            ChatClient.main(new String[0]);
        });

        Thread testBot = new Thread(() -> {
            try {
                Robot testRobot = new Robot();
                Words word = new Words(testRobot);
                start(word);
            } catch (AWTException|InterruptedException e) {
                e.printStackTrace();
            }
        });

        chatServer.start();
        testBot.start();

    }


    public static void timeout (int time) throws InterruptedException {
        TimeUnit.SECONDS.sleep(time);
    }

    public static void start (Words word) throws InterruptedException {
        timeout(2);

        word.messages(3);
        timeout(1);

        word.messages(1);
        timeout(1);

        word.action(3);
        timeout(1);

        word.action(2);
        timeout(1);

        word.action(4);
        timeout(1);

        word.action(7);
        timeout(1);

        word.action(6);
        timeout(1);

        word.action(8);
        timeout(3);

        word.action(5);
    }

}


class Words extends Testing {

    public Robot bot;

    public Words(Robot bot) {
        super();
        this.bot = bot;
    }

    public void messages(int value) {
        switch (value) {
            case 1 :
                bot.keyPress(KeyEvent.VK_H);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_L);
                bot.keyPress(KeyEvent.VK_L);
                bot.keyPress(KeyEvent.VK_O);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 2 :
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_G);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 3 :
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            default :
                bot.keyPress(KeyEvent.VK_ENTER);
        }
    }

    public void action(int value) {
        switch (value) {
            case 0 :
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 1 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_Q);
                bot.keyPress(KeyEvent.VK_U);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 2 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_U);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_R);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_V);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_W);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 3 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_C);
                bot.keyPress(KeyEvent.VK_H);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_SPACE);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_G);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 4 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_C);
                bot.keyPress(KeyEvent.VK_H);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_R);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_O);
                bot.keyPress(KeyEvent.VK_V);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 5 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_H);
                bot.keyPress(KeyEvent.VK_U);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_D);
                bot.keyPress(KeyEvent.VK_O);
                bot.keyPress(KeyEvent.VK_W);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 6 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_C);
                bot.keyPress(KeyEvent.VK_H);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_V);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_W);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 7 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_O);
                bot.keyPress(KeyEvent.VK_D);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_F);
                bot.keyPress(KeyEvent.VK_Y);
                bot.keyPress(KeyEvent.VK_MINUS);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_M);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_SPACE);
                bot.keyPress(KeyEvent.VK_U);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_R);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            case 8 :
                bot.keyPress(KeyEvent.VK_SLASH);
                bot.keyPress(KeyEvent.VK_R);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_S);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_R);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
            default :
                bot.keyPress(KeyEvent.VK_D);
                bot.keyPress(KeyEvent.VK_E);
                bot.keyPress(KeyEvent.VK_F);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_U);
                bot.keyPress(KeyEvent.VK_L);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_SPACE);
                bot.keyPress(KeyEvent.VK_A);
                bot.keyPress(KeyEvent.VK_C);
                bot.keyPress(KeyEvent.VK_T);
                bot.keyPress(KeyEvent.VK_I);
                bot.keyPress(KeyEvent.VK_O);
                bot.keyPress(KeyEvent.VK_N);
                bot.keyPress(KeyEvent.VK_ENTER);
                break;
        }
    }


}
