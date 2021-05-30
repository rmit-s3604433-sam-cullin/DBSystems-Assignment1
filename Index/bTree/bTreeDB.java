package bTree;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import dbstore.Idbentity;

public class bTreeDB<TKey extends Comparable<TKey> & Idbentity<TKey> , TValue extends Idbentity<TValue> > extends dbIndexNodeLoader<TKey, TValue> {
    private bTreeRoot<TKey,TValue> root;
    public bTreeDB(String datastorePath, int pageSize,bTreeRoot<TKey,TValue> root) {
        super(datastorePath+".leaf.heap", datastorePath+".inner.heap", pageSize, root);
        this.root = root;
    }
    public void setIds(){
        dbIndexNode<TKey,TValue> root = this.root.getNode();
        TreeNodeType type =root.getNodeType();

    }

    public void save() throws UnsupportedEncodingException, IOException{
        Iterator<dbIndexNode<TKey,TValue>> iter = root.iterator();
        int index = 0;
        while(iter.hasNext()){
            dbIndexNode<TKey,TValue> node = iter.next();
            
            
            if(node.getNodeType() == TreeNodeType.InnerNode){
                this.addInnerNode((dbInnerNode<TKey,TValue>) node);
            }else if(node.getNodeType() == TreeNodeType.LeafNode){
                this.addLeafNode((dbLeafNode<TKey,TValue>) node);
            }else{
                throw new UnsupportedEncodingException("Unknown Node type");
            }
        }
    }

    public void log(){
        bTreeStats<TKey,TValue> stats = root.stats();
        System.out.println(stats.toString());
        for(dbIndexNode<TKey,TValue> node: stats.nodes){
            int depth = stats.getDepth(node);
            String padding = "";
            for(int i = 0; i < depth; i ++){
                padding += "    ";
            }
            System.out.println(padding +"("+ depth+ ")"+ node.toJsonString());
        }
    }
    
}
