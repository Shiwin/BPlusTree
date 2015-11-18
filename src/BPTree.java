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
        Node parent;
        if (!nodes.isEmpty()){
            parent = nodes.pollFirst();
        }else {
            parent = new Node(degree,nextOffset);
            parent.setLeaf(false);
            nextOffset();
            parent.pointers.add(node.position);
            root = parent;
        }
        Node newNode = new Node(root.deg,nextOffset);
        nextOffset();

        if (!node.pointers.isEmpty()) {
            long privPointer = node.pointers.remove(0);
            newNode.pointers.add(privPointer);
        }

        String moveValue =  node.values.remove(0);
        String moveKey = node.keys.remove(0);

        newNode.keys.add(moveKey);
        newNode.values.add(moveValue);


        if (parent.pointers.isEmpty()){
            parent.pointers.add(newNode.position);
            parent.keys.add(node.keys.get(0));
        }else {
            parent.pointers.add(0, newNode.position);
            parent.keys.add(0,node.keys.get(0));
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
}

