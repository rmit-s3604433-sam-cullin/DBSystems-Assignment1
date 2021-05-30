package utils;

import java.util.Arrays;

public class Args {
    public static class IncorrectArgs extends Exception {
        public IncorrectArgs(String message){
            super(message);
        }
    }

    private static <T> IncorrectArgs Error(T key ,String message){
        return new IncorrectArgs("Error Param["+key.toString()+"] ->"+ message);
    }

    public abstract static class ArgOptions<T> {
        String flag = null;
        int index = -1;
        String message = null;
        T defaultVal = null;
        boolean require = false;
        boolean isBool = false;

        abstract public T Load(String[] args) throws IncorrectArgs;

        public ArgOptions<T> required(){
            this.require = true;
            return this;
        }
        public ArgOptions<T> defaultVal(T val ){
            this.defaultVal = val;
            return this;
        }
        public ArgOptions<T> index(int index ){
            this.index = index;
            return this;
        }
        public ArgOptions<T> flag(String flag){
            this.flag = flag;
            return this;
        }
        public ArgOptions<T> message(String message){
            this.message = message;
            return this;
        }
        public ArgOptions<T> setBoolean(){
            this.isBool = true;
            return this;
        }


        public IncorrectArgs getError(){
            return Error(this.getIndex(), this.message);
        }
        public String getIndex(){
            if(this.flag != null){
                return this.flag.toString();
            }else{
                return Integer.toString(this.index);
            }
        }
        
    }

    public static <T> String getArg(String[] args, ArgOptions<T> options)
        throws IncorrectArgs
    {
        int index = -1;
        if(options.flag != null){ 
            index = Arrays.asList(args).indexOf(options.flag);
            index = index == -1 ? index : index +1; 
        }
        if(options.index > -1 && index == -1){ index = options.index; }

        if(index == -1 && options.defaultVal != null ){ return null; } // Item not found but there is default value
        if(options.require == false) { return null; } // Not required
        if(index == -1 || index + 1 > args.length){ 
            if(options.defaultVal != null){ return null; }
            options.getError(); 
        } // Item not found or 
        return args[index];

    } 

    public static class StringArg extends ArgOptions<String> {

        @Override
        public String Load(String[] args)
            throws IncorrectArgs
        {
            String arg = getArg(args, this);
            if(arg == null && this != null){
                return this.defaultVal;
            }
            return arg;
        }
        
    }

    public static class IntArg extends ArgOptions<Integer> {

        @Override
        public Integer Load(String[] args)
        throws IncorrectArgs
        {
            String argS = getArg(args, this);
            Integer arg = null;
            if(argS != null){
                try{
                    arg = Integer.parseInt(argS);
                }catch(NumberFormatException e){
                    throw Error(this.getIndex(), "Incorrect Type Must be Int");
                }
            }else if(this.defaultVal != null){
                arg = this.defaultVal;
            }
            return arg;
        }
    }
    

    public static String getString(String args[], int index , String message)
        throws IncorrectArgs
    {
        if(args.length < index ){ throw Error(index, message); }
        return args[index];
    }

    public static String getString(String args[], String flag, String message)
        throws IncorrectArgs
    {
        int index = Arrays.asList(args).indexOf(flag);
        if(index == -1 || index + 1 > args.length){ throw Error(flag, message); }
        return args[index];
    }

    public static int getInt(String args[], int index, String message)
        throws IncorrectArgs
    {
        if(args.length < index ){ throw Error(index, message); }
        try{
            return Integer.parseInt(args[index]);
        }catch(NumberFormatException e){
            throw Error(index, "Incorrect Type Must be Int");
        }
        
    }

    public static int getInt(String args[], String flag, String message)
        throws IncorrectArgs
    {
        int index = Arrays.asList(args).indexOf(flag);
        if(index == -1 || index + 1 > args.length){ throw Error(flag, message); }
        try{
            return Integer.parseInt(args[index]);
        }catch(NumberFormatException e){
            throw Error(flag, "Incorrect Type Must be Int");
        }
        
    }
}
