package bTree;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import dbstore.IdbStorable;
import entity.dbEntity;
import entity.dbEntityKey;
import entity.dbPage;
import utils.Deserialize;
import utils.Serialize;

enum TreeNodeType {
	InnerNode,
	LeafNode
}

public abstract class dbIndexNode<TKey extends Comparable<TKey> & IdbStorable<TKey>>  extends dbEntity<dbIndexNode<TKey>> {
    
    
    protected Object[] keys;
    protected int keyCount;
    protected dbIndexNode<TKey> parentNode;
    protected dbIndexNode<TKey> leftSibling;
    protected dbIndexNode<TKey> rightSibling;

    

	//#region BTree
    //#region BTree Helpers
	protected dbIndexNode() {
		this.keyCount = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() {
		return this.keyCount;
	}
	
	@SuppressWarnings("unchecked")
	public TKey getKey(int index) {
		return (TKey)this.keys[index];
	}

	public void setKey(int index, TKey key) {
		this.keys[index] = key;
	}

	public dbIndexNode<TKey> getParent() {
		return this.parentNode;
	}

	public void setParent(dbIndexNode<TKey> parent) {
		this.parentNode = parent;
	}	
	
	public abstract TreeNodeType getNodeType();
	//#endregion
	
	/**
	 * Search a key on current node, if found the key then return its position,
	 * otherwise return -1 for a leaf node, 
	 * return the child node index which should contain the key for a internal node.
	 */
	public abstract int search(TKey key);
	
	
	
	/* The codes below are used to support insertion operation */
	//#region Insert Support
	public boolean isOverflow() {
		return this.getKeyCount() == this.keys.length;
	}
	
	public dbIndexNode<TKey> dealOverflow() {
		int midIndex = this.getKeyCount() / 2;
		TKey upKey = this.getKey(midIndex);
		
		dbIndexNode<TKey> newRNode = this.split();
				
		if (this.getParent() == null) {
			this.setParent(new dbInnerNode<TKey>());
		}
		newRNode.setParent(this.getParent());
		
		// maintain links of sibling nodes
		newRNode.setLeftSibling(this);
		newRNode.setRightSibling(this.rightSibling);
		if (this.getRightSibling() != null)
			this.getRightSibling().setLeftSibling(newRNode);
		this.setRightSibling(newRNode);
		
		// push up a key to parent internal node
		return this.getParent().pushUpKey(upKey, this, newRNode);
	}
	
	protected abstract dbIndexNode<TKey> split();
	
	protected abstract dbIndexNode<TKey> pushUpKey(TKey key, dbIndexNode<TKey> leftChild, dbIndexNode<TKey> rightNode);
	//#endregion
	
	
	
	
	
	/* The codes below are used to support deletion operation */
	//#region Delete Support
	public boolean isUnderflow() {
		return this.getKeyCount() < (this.keys.length / 2);
	}
	
	public boolean canLendAKey() {
		return this.getKeyCount() > (this.keys.length / 2);
	}
	
	public dbIndexNode<TKey> getLeftSibling() {
		if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void setLeftSibling(dbIndexNode<TKey> sibling) {
		this.leftSibling = sibling;
	}

	public dbIndexNode<TKey> getRightSibling() {
		if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(dbIndexNode<TKey> silbling) {
		this.rightSibling = silbling;
	}
	
	public dbIndexNode<TKey> dealUnderflow() {
		if (this.getParent() == null)
			return null;
		
		// try to borrow a key from sibling
		dbIndexNode<TKey> leftSibling = this.getLeftSibling();
		if (leftSibling != null && leftSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
			return null;
		}
		
		dbIndexNode<TKey> rightSibling = this.getRightSibling();
		if (rightSibling != null && rightSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, rightSibling, 0);
			return null;
		}
		
		// Can not borrow a key from any sibling, then do fusion with sibling
		if (leftSibling != null) {
			return this.getParent().processChildrenFusion(leftSibling, this);
		}
		else {
			return this.getParent().processChildrenFusion(this, rightSibling);
		}
	}
	
	protected abstract void processChildrenTransfer(dbIndexNode<TKey> borrower, dbIndexNode<TKey> lender, int borrowIndex);
	
	protected abstract dbIndexNode<TKey> processChildrenFusion(dbIndexNode<TKey> leftChild, dbIndexNode<TKey> rightChild);
	
	protected abstract void fusionWithSibling(TKey sinkKey, dbIndexNode<TKey> rightSibling);
	
	protected abstract TKey transferFromSibling(TKey sinkKey, dbIndexNode<TKey> sibling, int borrowIndex);
    //#endregion
    //#endregion
   
    //#region Db storable
    public static final int KEY_COUNT_SIZE = 4;
    public static final int PARENT_NODE_SIZE = dbEntityKey.CONTENT_SIZE;
    public static final int LEFT_SIBLING_SIZE = dbEntityKey.CONTENT_SIZE;
    public static final int RIGHT_SIBLING_SIZE = dbEntityKey.CONTENT_SIZE;

    public static final int KEY_COUNT_OFFSET = 0;
    public static final int PARENT_NODE_OFFSET = KEY_COUNT_OFFSET + KEY_COUNT_SIZE;
    public static final int LEFT_SIBLING_OFFSET = PARENT_NODE_OFFSET + PARENT_NODE_SIZE;
    public static final int RIGHT_SIBLING_OFFSET = LEFT_SIBLING_OFFSET + LEFT_SIBLING_SIZE;
    public static final int KEYS_OFFSET = RIGHT_SIBLING_OFFSET + RIGHT_SIBLING_SIZE;


    
    protected dbPage<dbIndexElement<TKey>> keyList;
    protected dbIndexElement<TKey> type;

    abstract dbIndexNode<TKey> newInstance();

    dbIndexNode(int keyCount , dbIndexElement<TKey> type) {
        super();
        this.type = type;
        this.keyCount = keyCount;
        this.keyList = new dbPage<dbIndexElement<TKey>>(keyCount * this.type.getSize(), type);
    }

    @Override
    public int getSize() {
        return keyCount * this.type.getSize() + KEY_COUNT_SIZE + dbEntityKey.CONTENT_SIZE * 3;
    }

    //@SuppressWarnings("unchecked")
    public dbIndexNode<TKey> initialize(dbEntityKey key, int keyCount, dbIndexElement<TKey> type ,TreeNodeType nodeType){
        dbIndexNode<TKey> dto = null;
        if(nodeType == TreeNodeType.LeafNode){
            dto = new dbLeafNode<TKey, TKey>(); // TODO: SC  sperate type required 
        }else if(nodeType == TreeNodeType.InnerNode){
            dto = new dbInnerNode<TKey>();
        }
        dto.key = key;
        dto.keyCount = keyCount;
        dto.type = type;
        return dto;
    };

    public int keyListSize(){
        return keyCount * this.type.getSize();
    }

    private void SetPageList(dbIndexElement<TKey> ary[] ){
        this.keyList.entities = Arrays.asList(ary);
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] DATA = new byte[this.getSize()];
        Serialize.integer(this.keyCount, KEY_COUNT_SIZE, KEY_COUNT_OFFSET, DATA);
        Serialize.bytes(this.parentNode.getKey().serialize(), PARENT_NODE_SIZE, PARENT_NODE_OFFSET, DATA);
        Serialize.bytes(this.leftSibling.getKey().serialize(), LEFT_SIBLING_SIZE, LEFT_SIBLING_OFFSET, DATA);
        Serialize.bytes(this.rightSibling.getKey().serialize(), RIGHT_SIBLING_SIZE, RIGHT_SIBLING_OFFSET, DATA);
        this.SetPageList((dbIndexElement<TKey>[]) this.keys);
        Serialize.bytes(
            this.keyList.serialize(), this.keyListSize() , KEYS_OFFSET, DATA
        );
        return DATA;
    }

    @Override
    public dbIndexNode<TKey> deserialize(byte[] DATA) throws UnsupportedEncodingException {
        dbIndexNode<TKey> dto = this.newInstance();
        dbEntityKey keyType = new dbEntityKey();
        dto.keyCount = Deserialize.integer(DATA, KEY_COUNT_SIZE, KEY_COUNT_OFFSET);
        dto.parentNode = dto.initialize(
            keyType.deserialize(
                Deserialize.bytes(DATA, PARENT_NODE_SIZE, PARENT_NODE_SIZE)
            ),
            this.keyCount,
            this.type,
            this.getNodeType()
        );
        dto.leftSibling = dto.initialize(
            keyType.deserialize(
                Deserialize.bytes(DATA, LEFT_SIBLING_SIZE, LEFT_SIBLING_SIZE)
            ),
            this.keyCount,
            this.type,
            this.getNodeType()
        );
        dto.rightSibling = dto.initialize(
            keyType.deserialize(
                Deserialize.bytes(DATA, RIGHT_SIBLING_SIZE, RIGHT_SIBLING_SIZE)
            ),
            this.keyCount,
            this.type,
            this.getNodeType()
        );
        this.keyList.deserialize(
            Deserialize.bytes(DATA, this.keyListSize(), KEYS_OFFSET )
        );
        this.keys = this.keyList.entities.toArray();

        return dto;
    }


    //#endregion
}
