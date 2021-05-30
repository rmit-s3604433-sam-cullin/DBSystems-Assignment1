package bTree;

import java.io.UnsupportedEncodingException;

import dbstore.Idbentity;
import entity.dbEntity;
import entity.dbEntityKey;
import utils.Deserialize;
import utils.Serialize;

public abstract class dbIndexNode<TKey extends Comparable<TKey> & Idbentity<TKey> , TValue extends Idbentity<TValue>>  extends dbEntity<dbIndexNode<TKey,TValue>> {
    protected final static int LEAFORDER = 4;
    protected final static int INNERORDER = 4;
    protected final static int KEY_SIZE = INNERORDER + 1;
    protected final static int CHILDREN_SIZE = INNERORDER + 2;
    protected final static int VALUE_SIZE = KEY_SIZE;
    
    protected Object[] keys;
    protected int keyCount;
    protected dbIndexNode<TKey,TValue> parentNode;
    protected dbIndexNode<TKey,TValue> leftSibling;
    protected dbIndexNode<TKey,TValue> rightSibling;



    

	//#region BTree
    //#region BTree Helpers


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

	public dbIndexNode<TKey,TValue> getParent() {
		return this.parentNode;
	}

	public void setParent(dbIndexNode<TKey,TValue> parent) {
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
	
	public dbIndexNode<TKey,TValue> dealOverflow() {
		int midIndex = this.getKeyCount() / 2;
		TKey upKey = this.getKey(midIndex);
		
		dbIndexNode<TKey,TValue> newRNode = this.split();
				
		if (this.getParent() == null) {
			this.setParent(new dbInnerNode<TKey,TValue>(this.keyType, this.valueType));
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
	
	protected abstract dbIndexNode<TKey,TValue> split();
	
	protected abstract dbIndexNode<TKey,TValue> pushUpKey(TKey key, dbIndexNode<TKey,TValue> leftChild, dbIndexNode<TKey,TValue> rightNode);
	//#endregion
	
	
	
	
	
	/* The codes below are used to support deletion operation */
	//#region Delete Support
	public boolean isUnderflow() {
		return this.getKeyCount() < (this.keys.length / 2);
	}
	
	public boolean canLendAKey() {
		return this.getKeyCount() > (this.keys.length / 2);
	}
	
	public dbIndexNode<TKey,TValue> getLeftSibling() {
		if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void setLeftSibling(dbIndexNode<TKey,TValue> sibling) {
		this.leftSibling = sibling;
	}

	public dbIndexNode<TKey,TValue> getRightSibling() {
		if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(dbIndexNode<TKey,TValue> silbling) {
		this.rightSibling = silbling;
	}
	
	public dbIndexNode<TKey,TValue> dealUnderflow() {
		if (this.getParent() == null)
			return null;
		
		// try to borrow a key from sibling
		dbIndexNode<TKey,TValue> leftSibling = this.getLeftSibling();
		if (leftSibling != null && leftSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
			return null;
		}
		
		dbIndexNode<TKey,TValue> rightSibling = this.getRightSibling();
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
	
	protected abstract void processChildrenTransfer(dbIndexNode<TKey,TValue> borrower, dbIndexNode<TKey,TValue> lender, int borrowIndex);
	
	protected abstract dbIndexNode<TKey,TValue> processChildrenFusion(dbIndexNode<TKey,TValue> leftChild, dbIndexNode<TKey,TValue> rightChild);
	
	protected abstract void fusionWithSibling(TKey sinkKey, dbIndexNode<TKey,TValue> rightSibling);
	
	protected abstract TKey transferFromSibling(TKey sinkKey, dbIndexNode<TKey,TValue> sibling, int borrowIndex);
    //#endregion
    //#endregion
   
    //#region Db storable
    public static final int NODE_TYPE_SIZE = 4;
    public static final int NODE_NULL_CHECK_SIZE = 4;
    public static final int NODE_LOOKUP_SIZE = NODE_TYPE_SIZE + NODE_NULL_CHECK_SIZE + dbEntityKey.CONTENT_SIZE;

    public static final int NODE_NULL_CHECK_OFFSET = 0;
    public static final int NODE_TYPE_OFFSET = NODE_NULL_CHECK_OFFSET + NODE_NULL_CHECK_SIZE;
    public static final int NODE_LOOKUP_OFFSET = NODE_TYPE_OFFSET + NODE_TYPE_SIZE;

    public static final int PARENT_NODE_SIZE = NODE_LOOKUP_SIZE;
    public static final int LEFT_SIBLING_SIZE = NODE_LOOKUP_SIZE;
    public static final int RIGHT_SIBLING_SIZE = NODE_LOOKUP_SIZE;
    

    public static final int PARENT_NODE_OFFSET = 0;
    public static final int LEFT_SIBLING_OFFSET = PARENT_NODE_OFFSET + PARENT_NODE_SIZE;
    public static final int RIGHT_SIBLING_OFFSET = LEFT_SIBLING_OFFSET + LEFT_SIBLING_SIZE;
    public static final int KEYS_OFFSET = RIGHT_SIBLING_OFFSET + RIGHT_SIBLING_SIZE;


    
    protected TKey keyType;
    protected TValue valueType;
    protected dbIndexNodeLoader<TKey, TValue> loader;
    protected boolean isLoaded = true;


    public void initializeLoad(dbEntityKey key, dbIndexNodeLoader<TKey, TValue> loader){
        this.isLoaded = false;
        this.key = key;
        this.loader = loader;
    }
    

    abstract dbIndexNode<TKey,TValue> newInstance();

    dbIndexNode( TKey keyType, TValue valueType) {
        super();
        this.keyType = keyType;
        this.keyCount = 0;
        this.valueType = valueType;
        this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
    }


    protected int keyListSize(){
        return KEY_SIZE * this.keyType.getSize();
    }

    @Override
    public int getSize() {
        return this.keyListSize() + NODE_LOOKUP_SIZE * 3;
    }

    public abstract dbIndexNode<TKey,TValue> load();

    public dbIndexNode<TKey,TValue> initialize(boolean isNull, dbEntityKey key,TreeNodeType nodeType){
        if(isNull) { return null; }
        dbIndexNode<TKey,TValue> dto = null;
        if(nodeType == TreeNodeType.LeafNode){
            dto = new dbLeafNode<TKey, TValue>(this.keyType, this.valueType); 
        }else if(nodeType == TreeNodeType.InnerNode){
            dto = new dbInnerNode<TKey, TValue>(this.keyType, this.valueType);
        }
        dto.key = key;
        dto.loader = this.loader;
        dto.isLoaded = false;
        return dto;
    };





    protected byte[] serializeLookup(dbIndexNode<TKey, TValue> node) 
        throws UnsupportedEncodingException
    {
        byte[] DATA = new byte[NODE_LOOKUP_SIZE];
        if(node == null){ return DATA; }
        Serialize.integer(1, NODE_NULL_CHECK_SIZE , NODE_NULL_CHECK_OFFSET, DATA);
        Serialize.integer(node.getNodeType().toInt(), NODE_TYPE_SIZE, NODE_TYPE_OFFSET,DATA);
        Serialize.bytes(node.getKey().serialize(), node.getKey().getSize(), NODE_LOOKUP_OFFSET, DATA);
        return DATA;
    }

    



    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize() throws UnsupportedEncodingException {
        byte[] DATA = new byte[this.getSize()];
        Serialize.bytes(this.serializeLookup(this.parentNode), PARENT_NODE_SIZE, PARENT_NODE_OFFSET, DATA);
        Serialize.bytes(this.serializeLookup(this.leftSibling), LEFT_SIBLING_SIZE, LEFT_SIBLING_OFFSET, DATA);
        Serialize.bytes(this.serializeLookup(this.rightSibling), RIGHT_SIBLING_SIZE, RIGHT_SIBLING_OFFSET, DATA);
        Serialize.bytes(
            Serialize.array(this.keys, this.keyListSize() ),
            this.keyListSize(), 
            KEYS_OFFSET, 
            DATA
        );
        return DATA;
    }

    protected boolean deserializeNullCheck(byte[] rec, int offset)
        throws UnsupportedEncodingException
    {
        int val = Deserialize.integer(rec, 4, offset);
        return val == 0;
    }
    protected TreeNodeType deserializeNodeType(byte[] rec, int offset)
        throws UnsupportedEncodingException
    {
        return TreeNodeType.fromInt(
            Deserialize.integer(rec, 4, offset)
        );
    }

    protected dbEntityKey deserializeLookupKey(byte[] rec, int offset)
        throws UnsupportedEncodingException
    {
        return new dbEntityKey().deserialize(
            Deserialize.bytes(rec, dbEntityKey.CONTENT_SIZE, 4+ offset)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public dbIndexNode<TKey,TValue> deserialize(byte[] DATA) throws UnsupportedEncodingException {
        this.parentNode = this.initialize(
            this.deserializeNullCheck(DATA, PARENT_NODE_OFFSET),
            this.deserializeLookupKey(DATA, PARENT_NODE_OFFSET),
            this.deserializeNodeType(DATA, PARENT_NODE_OFFSET)
        );
        this.leftSibling = this.initialize(
            this.deserializeNullCheck(DATA, PARENT_NODE_OFFSET),
            this.deserializeLookupKey(DATA, LEFT_SIBLING_OFFSET),
            this.deserializeNodeType(DATA, LEFT_SIBLING_OFFSET)
        );
        this.rightSibling = this.initialize(
            this.deserializeNullCheck(DATA, PARENT_NODE_OFFSET),
            this.deserializeLookupKey(DATA, RIGHT_SIBLING_OFFSET),
            this.deserializeNodeType(DATA, RIGHT_SIBLING_OFFSET)
        );
        this.keys = Deserialize.array(
            Deserialize.bytes(DATA, this.keyListSize(), KEYS_OFFSET), 
            this.keyType
        ); 
        
        return (dbIndexNode<TKey,TValue>) this.clone();
    }

    public String lookupJson(dbIndexNode<TKey, TValue> node){
        if(node == null) return "{dbKey:null,type:null}";
        return "{ dbKey:"+ node.key.toJsonString()+ ",type:"+node.getNodeType() +" }";
    }

    public String toJsonString(){
        return "{"+
        " ,dbKey:"+this.key.toJsonString()+
        " ,type:"+this.getNodeType()+ ""+
        " ,parentNode:"+this.lookupJson(this.parentNode)+
        " ,keyCount:"+this.keyCount+""+
        "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj instanceof dbIndexNode<?,?>){
            try {
                dbIndexNode<?,?> casted = (dbIndexNode<?,?>) obj;
                if(casted.getNodeType() == this.getNodeType() && super.equals(obj)){
                    return true;
                }
            }catch(Exception e){}
        }
        return false;
    }



    public abstract void fillIterator(bTreeStats<TKey,TValue> items, int depth);


    //#endregion
}
