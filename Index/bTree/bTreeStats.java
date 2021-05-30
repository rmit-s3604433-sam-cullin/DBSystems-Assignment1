package bTree;

import java.util.ArrayList;
import java.util.List;

import dbstore.Idbentity;

public class bTreeStats<TKey extends Comparable<TKey> & Idbentity<TKey> , TValue extends Idbentity<TValue> > {
    public List<dbIndexNode<TKey,TValue>> nodes = new ArrayList<dbIndexNode<TKey,TValue>>();
    public List<Integer> depths = new ArrayList<Integer>();
    public dbIndexNode<TKey,TValue> root;
    public int leafCount = 0;
    public int innerCount = 0;

    public void addNode(dbIndexNode<TKey,TValue> node, int depth){
        if(this.root == null){
            this.root = node;
        }
        if(depth == 0){
            System.out.println("Adding depth 0");
        }
        this.nodes.add(node);
        this.depths.add(depth);
        if(node.getNodeType() == TreeNodeType.InnerNode){
            this.innerCount += 1;
        }else{
            this.leafCount += 1;
        }
    }

    public dbIndexNode<TKey,TValue> getNode(int index){
        return this.nodes.get(index);
    }

    public Integer getDepth(int index){
        return this.depths.get(index);
    }
    public Integer getDepth(dbIndexNode<TKey,TValue> node){
        int index = this.nodes.indexOf(node);
        return this.depths.get(index);
    }

    @Override
    public String toString() {
        return String.format("Total: %d Leaf: %d Inner: %d RootKey: %s", this.nodes.size(), this.leafCount, this.innerCount, this.root.key.toJsonString());
    }
}
