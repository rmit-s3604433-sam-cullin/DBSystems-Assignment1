package bTree;

import java.util.HashMap;
import java.util.Map;

public enum TreeNodeType {
	InnerNode(0),
	LeafNode(1);
    private final Integer dbValue;

    private TreeNodeType(Integer dbValue){
        this.dbValue = dbValue;
    }

    public int toInt(){
        return this.dbValue;
    }

    public static final Map<Integer, TreeNodeType> deserializer = new HashMap<>();

    static {
        for (TreeNodeType value : values()) {
            deserializer.put(value.dbValue, value);
        }
    }

    public static TreeNodeType fromInt(Integer dbValue) {
        return deserializer.get(dbValue);
    }

    public String toString(){
        if(this.dbValue==0){
            return "InnerNode";
        }else{
            return "LeafNode ";
        }
    }
}