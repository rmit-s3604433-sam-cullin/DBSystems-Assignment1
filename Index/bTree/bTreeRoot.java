package bTree;

import java.util.Iterator;

import dbstore.Idbentity;

public class bTreeRoot<TKey extends Comparable<TKey> & Idbentity<TKey> , TValue extends Idbentity<TValue> > {
	protected dbIndexNode<TKey, TValue> root;
	
	public bTreeRoot(TKey keyType, TValue valueType) {
		this.root = new dbLeafNode<TKey, TValue>(keyType,valueType);
	}
    public dbIndexNode<TKey, TValue> getNode(){
        return this.root;
    }
    public void setRoot(dbIndexNode<TKey, TValue> root){
        this.root = root;
    }

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
	public void insert(TKey key, TValue value) {
		dbLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		leaf.insertKey(key, value);
		
		if (leaf.isOverflow()) {
			dbIndexNode<TKey, TValue> n = leaf.dealOverflow();
			if (n != null)
				this.root = n; 
		}
	}
	
	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TValue search(TKey key) {
		dbLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}
	
	/**
	 * Delete a key and its associated value from the tree.
	 */
	public void delete(TKey key) {
		dbLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		if (leaf.delete(key) && leaf.isUnderflow()) {
			dbIndexNode<TKey, TValue> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n; 
		}
	}
	
	/**
	 * Search the leaf node which should contain the specified key
	 */
	private dbLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		dbIndexNode<TKey, TValue> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((dbInnerNode<TKey, TValue>)node).getChild( node.search(key) );
		}
		
		return (dbLeafNode<TKey, TValue>)node;
	}

    public Iterator<dbIndexNode<TKey,TValue>> iterator() {
        bTreeStats<TKey,TValue> stats = this.stats();
        return stats.nodes.iterator();
    }

    public bTreeStats<TKey,TValue> stats(){
        bTreeStats<TKey,TValue> stats = new bTreeStats<>();
        this.root.fillIterator(stats, 0);
        return stats;
    }
}
