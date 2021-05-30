package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import utils.Args.IncorrectArgs;

public abstract class Cli {
    

    protected abstract String getHelp();
    protected abstract void loadArgs(String[] args) throws IncorrectArgs;


    public void Load(String[] args){
        try{
            this.loadArgs(args);
        }catch(IncorrectArgs e){
            System.out.println(e.toString());
            System.exit(1);
        }
    }

    public Integer readInteger(String message){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String input = "";
            try{
                System.out.println(message);
                input = reader.readLine();
                if(input == "exit"){
                    System.out.println("Exiting");
                    System.exit(0);
                }
                int output = Integer.parseInt(input);
                return output;
            }catch(Exception e){
                System.out.println("Could not cast "+ input+" to integer please enter an integer or (exit) to exit.");
            }
        }
    }
}
