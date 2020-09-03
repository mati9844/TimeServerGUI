package sample;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class LastServer {


    public LastServer(){
    }

    void addServer(String x){
        try{
            PrintWriter writer = new PrintWriter(new File("servers.txt"));
            writer.print("");
            writer.print(x);
            writer.close();

        }catch(Exception e){

        }
    }

    String getServer() {
        String x = null;
        try{
            x = Files.readString(Path.of("servers.txt"));
        }catch(Exception e){

        }

        if(x==null)
            return "";

        return ("In the previous session, you connected to: " + x);
    }


}
