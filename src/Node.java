import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * B+ tree node.
 */
public class Node {

    public static final boolean DEBUG = true;

    boolean isFull;
    boolean isLeaf;
    public final int deg;

    long position;

    ArrayList<String > keys;
    ArrayList<String > values;
    ArrayList<Long> pointers;

    public Node(int deg,long offset) {
        this.isFull = false;
        this.isLeaf = true;
        this.deg = deg;
        this.values = new ArrayList<>();
        this.keys = new ArrayList<>();
        this.pointers = new ArrayList<>();
        this.position = offset;
    }


    public void DEBUG(){
        assert keys.size()<=deg;
        assert pointers.size()<=deg+1;
        if (isLeaf()){
            assert pointers.size()<=2;
        }
    }

    public boolean isFull() {
        return isFull;
    }

    public boolean isLeaf() {
        return isLeaf;
    }


    public void resetFull() {
        isFull = (keys.size() == deg);
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public Node getRelative(String key, RandomAccessFile ra){
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).compareTo(key)>=0){//keys.get(i)>=value
                return readNode(ra,pointers.get(i));
            }
        }
        return readNode(ra,pointers.get(keys.size()));
    }

    public Integer addChild(Node n,long position){
        if (keys.size()==0){
            pointers.add(position);
            return 0;
        }
        String minKey = n.minKey();
        if (minKey.compareTo(this.minKey())<=0){
            pointers.add(0,position);
            return 0;
        }
        for (int i = 1; i < keys.size(); i++) {
            if (minKey.compareTo(this.keys.get(i))<=0&&minKey.compareTo(this.keys.get(i-1))>=0){
                pointers.add(i,position);
                return i;
            }
        }
        pointers.add(position);
        return pointers.size()-1;
    }

    public String minKey(){
        return keys.get(0);
    }

    public static Node readNode(int deg,RandomAccessFile ra,long possition){
        return new Node(deg,possition).readNode(ra,possition);
    }

    protected Node readNode(RandomAccessFile ra, long position) {
        try {
            ra.seek(position);
            Node n = new Node(deg,position);

            Boolean isLeaf = ra.readBoolean();
            n.setLeaf(isLeaf);
            for (int i = 0; i < deg; i++) {
                String key = ra.readLine().trim();
                if (!key.equals("")){
                    n.keys.add(key);
                }
            }

            if (isLeaf){
                for (int i = 0; i < n.keys.size(); i++) {
                    String value = ra.readLine().trim();
                    n.values.add(value);
                }
                for (int i = 0; i < deg - n.keys.size(); i++) {
                    ra.readLine();
                }
            }

            for (int i = 0; i <= deg; i++) {
                Long p = ra.readLong();
                n.pointers.add(p);
            }

            n.resetFull();
            return n;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeNode(RandomAccessFile ra){
        this.writeNode(ra, position);
    }

    public void writeNode(RandomAccessFile ra, long position){
        try {
            ra.seek(position);
            ra.writeBoolean(isLeaf());
            for (String key : keys) {
                ra.writeChars(key + "\n");
            }
            for (int i = 0; i < deg - keys.size(); i++) {
                ra.writeChars("\n");
            }

            if (isLeaf()){
                for (int i = 0; i < deg; i++) {
                    if (i < values.size()) {
                        ra.writeChars(values.get(i) + "\n");
                    } else {
                        ra.writeChars("\n");
                    }
                }
            }

            for (int i = 0; i < deg; i++) {
                if (i<pointers.size()) {
                    ra.writeLong(pointers.get(i));
                }else {
                    ra.writeLong(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insert(String  key,String  value) {
        if (keys.size()==0){
            keys.add(key);
            values.add(value);
        }else {
            for (int i = 0; i < keys.size() ; i++) {
                if (keys.get(i).compareTo(key) >= 0) {//keys.get(i) >= value
                    keys.add(i, key);
                    values.add(i, value);
                    resetFull();
                    return;
                }
            }
            keys.add(key);
            values.add(value);
        }
        this.resetFull();
    }

    public String get(String  key){
        int index = keys.lastIndexOf(key);
        if (index<0){
            return null;
        }else {
            return values.get(index);
        }
    }


    @Override
    public String toString() {
        return "Node{" +
                "isFull=" + isFull +
                ", isLeaf=" + isLeaf +
                ", deg=" + deg +
                ", position=" + position +
                ", keys=" + keys +
                ", values=" + values +
                ", pointers=" + pointers +
                '}';
    }
}
