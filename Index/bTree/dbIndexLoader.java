package bTree;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import dbstore.IdbStorable;
import dbstore.rafdb;
import entity.dbEntityKey;

public class dbIndexLoader<TKey extends Comparable<TKey> & IdbStorable<TKey>, TValue> extends rafdb {

    // private dbLeafNode<TKey,TValue> leafNodeType;
    // private dbInnerNode<TKey> innerNodeType;
    // private dbIndexElement<TKey> keyType;




    public dbIndexLoader(String datastorePath, TKey type) {
        super(datastorePath);
        // this.leafNodeType = new dbLeafNode<TKey,TValue>();
        // this.innerNodeType = new dbInnerNode<TKey>();
        // this.keyType = new dbIndexElement<TKey>(type);
    }




    @SuppressWarnings("unchecked")
    public <T extends dbIndexNode<TKey> > T findNode(T unloadedNode)
        throws IOException
    {
        dbEntityKey key = unloadedNode.getKey();
        long nodeIndex = key.getPageId() * unloadedNode.getSize();
        System.out.println("Getting node at index "+ nodeIndex);
        byte[] PAGE = this.read(nodeIndex, unloadedNode.getSize());
        return (T) unloadedNode.deserialize(PAGE);
    }
    public <T extends dbIndexNode<TKey>> T savePage(T loadedNode)
       throws UnsupportedEncodingException, IOException
    {
        long nodeIndex = loadedNode.getKey().getPageId() * loadedNode.getSize();
        byte[] NODE = loadedNode.serialize();
        this.write(
            nodeIndex
        , NODE);
        return loadedNode;
    }

    


    
}
