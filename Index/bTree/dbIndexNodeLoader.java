package bTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import dbstore.dbBytePage;
import dbstore.dbQntPage;
import entity.dbEntityLoader;

public class dbIndexNodeLoader<TKey extends Comparable<TKey> & Idbentity<TKey>, TValue extends Idbentity<TValue>> {



    public dbEntityLoader<dbIndexNode<TKey,TValue> ,dbBytePage<dbIndexNode<TKey,TValue>>> leafStore;
    public dbEntityLoader<dbIndexNode<TKey,TValue> ,dbBytePage<dbIndexNode<TKey,TValue>>> innerStore;


    public dbInnerNode<TKey,TValue> loadInnerNode(dbInnerNode<TKey,TValue> unloadedNode)
    {

        try {
            byte[] DATA = this.innerStore.read(
            this.innerStore.getIndex(unloadedNode.getKey()),
            unloadedNode.getSize()
        );
        unloadedNode.isLoaded = true;
        return (dbInnerNode<TKey,TValue>) unloadedNode.deserialize(DATA);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public dbLeafNode<TKey,TValue> loadLeafNode(dbLeafNode<TKey,TValue> unloadedNode)
    {
        
        try {
            byte[] DATA;
            DATA = this.leafStore.read(
                this.leafStore.getIndex(unloadedNode.getKey()),
                unloadedNode.getSize()
            );
       
            unloadedNode.isLoaded = true;
            return (dbLeafNode<TKey,TValue>) unloadedNode.deserialize(DATA);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
        
    }



    public void addLeafNode(dbLeafNode<TKey,TValue> loadedNode) throws UnsupportedEncodingException, IOException
    {
        this.leafStore.insertEntity(loadedNode);
    }

    public void addInnerNode(dbInnerNode<TKey,TValue> loadedNode) throws UnsupportedEncodingException, IOException
    {
        this.innerStore.insertEntity(loadedNode);
    }

    public void connect() throws FileNotFoundException {
        this.innerStore.connect();
        this.leafStore.connect();
    }

    public void close(){
        this.innerStore.close();
        this.leafStore.close();
    }




    private dbLeafNode<TKey,TValue> leafType;
    private dbQntPage<dbIndexNode<TKey,TValue>> leafPageType;

    private dbInnerNode<TKey,TValue> innerType;
    private dbQntPage<dbIndexNode<TKey,TValue>> innerPageType;

    public dbIndexNodeLoader(String leafNodeStore, String innerNodeStore, int pageQnt, bTreeRoot<TKey,TValue> root) {
        
        this.leafType = new dbLeafNode<TKey,TValue>(root.root.keyType, root.root.valueType);
        this.leafPageType = new dbQntPage<>(pageQnt, this.leafType);
        this.leafStore = new dbEntityLoader<>(leafNodeStore , this.leafPageType,  this.leafType);

        this.innerType = new dbInnerNode<TKey, TValue>(root.root.keyType, root.root.valueType);
        this.innerPageType = new dbQntPage<>(pageQnt, this.innerType);
        this.innerStore = new dbEntityLoader<>(innerNodeStore , this.innerPageType, this.innerType);


    }






    


    
}
