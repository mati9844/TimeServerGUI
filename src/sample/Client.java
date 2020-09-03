package sample;

import java.util.LinkedList;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Client {
    LinkedList<String> offers = new LinkedList<>();
    MulticastReceiver multicastReceiver = null;

    private static final Pattern PATTERNIP = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private boolean isValidIP(String ip){
        if(ip.contains("127.0.0.1"))
            return false;

        return PATTERNIP.matcher(ip).matches();
    }

    void init() throws InterruptedException {
        multicastReceiver = new MulticastReceiver();
        multicastReceiver.start();

        Thread multicastSenderThread = new Thread(new MulticastSender());
        multicastSenderThread.start();

        while(!multicastReceiver.getPaused()){
            sleep(1000);
        }

        System.out.println("Printing offers");

        LinkedList<String> temps = new LinkedList<>();

        temps = multicastReceiver.getOffers();

        for(String temp : temps){
            if(temp.contains("OFFER")) {
                String[] spliter = temp.split(" ");
                if (spliter.length == 3) {
                    String x = spliter[1];
                    if (spliter[2].matches("-?\\d+")) {
                    }
                    if (isValidIP(x)) {
                        x += (":" + spliter[2]);
                        if(!offers.contains(x)) {
                            offers.add(x);
                           // CONTROLLER.addItemsChoiceBox(x);
                        }
                    }
                }
            }
        }

        for(String s : offers){
            System.out.println(s);
        }
    }

    void updateOffers() {
        Thread multicastSenderThread = new Thread(new MulticastSender());
        multicastSenderThread.start();
        multicastReceiver.setPaused(false);
        while(!multicastReceiver.getPaused()){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("temp");
        LinkedList<String> temps = new LinkedList<>();

        temps = multicastReceiver.getOffers();
        offers = new LinkedList<>();
        for(String temp : temps){
            System.out.println(temp);

            if(temp.contains("OFFER")) {
                String[] spliter = temp.split(" ");
                if (spliter.length == 3) {
                    String x = spliter[1];
                    if (spliter[2].matches("-?\\d+")) {
                    }
                    if (isValidIP(x)) {
                        x += (":" + spliter[2]);
                        if(!offers.contains(x)){
                                offers.add(x);

                        }
                    }
                }
            }
        }

        for(String s : offers){
            System.out.println(s);
        }
    }
}
