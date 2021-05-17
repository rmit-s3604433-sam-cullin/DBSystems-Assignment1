package utils;

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
}
