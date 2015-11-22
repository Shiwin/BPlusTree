import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * B+ tree implementation
 */

public class BPTree {

    public static final int degree = 4 ;
    private static long nextOffset = 0;
    public static final int RECODE_SIZE = 500;
    private final RandomAccessFile raf;
    private Node root=null;

    public BPTree(RandomAccessFile raf) {
        this.raf = raf;
    }

    public BPTree(Node root, RandomAccessFile ra) {
        this.raf = ra;
        this.root = root;
    }

    /**
     * must be invoked after using `nextOffset`
     */
    public static void nextOffset() {
        nextOffset += RECODE_SIZE;
    }

    public void insert(String key, String value){
        if (root == null){
            root = new Node(degree,nextOffset);
            nextOffset();
            root.insert(key, value);
            root.writeNode(raf);
        }else {
            LinkedList<Node> nodes = getPathToLeaf(key);
            insertToNode(key,value,nodes);
        }

    }


    /**
     * insertValue to node
     * @param key
     * @param value
     * @param nodes
     */
    private void insertToNode(String  key,String  value,LinkedList<Node> nodes){
        Node node = nodes.pollFirst();
        if (node.isLeaf){
            node.insert(key,value);
            if (node.isFull()){
                split(node,nodes);
            }
        }else {
            throw new Error(String.format("Illegal arguments %s %s %s %s",key,value,node,nodes));
        }
    }

    /**
     *
     * @param node
     * @param nodes
     */
    private void split(Node node,LinkedList<Node> nodes){

        //new node
        Node newNode = new Node(root.deg,nextOffset);
        nextOffset();

        //move pointer to previous node if exists
        if (!node.pointers.isEmpty()) {
            long privPointer = node.pointers.remove(0);
            newNode.pointers.add(privPointer);
        }else {//set non pointer value
            newNode.pointers.add(-1l);
        }

        //
        newNode.pointers.add(node.position);
        node.pointers.add(newNode.position);

        //move values from node to new node
        String moveValue =  node.values.remove(0);
        String moveKey = node.keys.remove(0);
        newNode.keys.add(moveKey);
        newNode.values.add(moveValue);

        Node parent;
        if (!nodes.isEmpty()){//get parent from list
            parent = nodes.pollFirst();
            parent.keys.add(node.keys.get(0));
            parent.addRelated(newNode);
            parent.addRelated(node);
        }else {//create parent if list is empty(create new root)
            parent = new Node(degree,nextOffset);
            parent.setLeaf(false);
            nextOffset();
            parent.pointers.add(newNode.position);
            parent.pointers.add(node.position);
            parent.keys.add(node.keys.get(0));
            root = parent;
        }

        parent.writeNode(raf);
        newNode.writeNode(raf);
        node.writeNode(raf);

        parent.resetFull();
        if (parent.isFull()){
            split(parent,nodes);
        }
    }

    public Node find(String key){
        LinkedList<Node> nodes = getPathToLeaf(key);
        Node node = nodes.pollFirst();
        assert node.isLeaf();
        if(node.keys.contains(key)) {
            return node;
        }else {
            return null;
        }
    }

    private LinkedList<Node> getPathToLeaf(String key) {
        LinkedList<Node> path = new LinkedList<>();
        Node node = root;
        path.addFirst(node);
        while (!node.isLeaf()){
            node = node.getRelative(key,raf);
            path.addFirst(node);
        }
        return path;
    }

    public void traverse(){
        /*long offset = 0;
        while (true){
            Node n = Node.readNode(degree,raf,offset);
            System.out.println(n);
            offset += RECODE_SIZE;
        }*/
        traverse(root);
    }


    private void traverse(Node root){
        System.out.println(root);
        if(!root.isLeaf()){
            for (long pos:root.pointers) {
                Node n = Node.readNode(root.deg,raf,pos);
                traverse(n);
            }
        }
    }

    protected String listKeys(){
        Node leaf = root;
        while (!leaf.isLeaf()){
            leaf = Node.readNode(leaf.deg,raf,leaf.pointers.get(0));
        }
        StringBuilder sb = new StringBuilder();
        leaf.keys.forEach(n->{sb.append(n);
            sb.append(" ");});
        long pointer = leaf.pointers.get(1);
        while (pointer!=-1){
            leaf = Node.readNode(leaf.deg,raf,pointer);
            leaf.keys.forEach(n->{sb.append(n);
            sb.append(" ");});
            pointer = leaf.pointers.get(1);
        }
        return sb.toString();
    }
}

