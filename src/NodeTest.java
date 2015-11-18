import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by ivan on 17.11.15.
 */
public class NodeTest {

    Node node;

    @Before
    public void setUp() throws Exception {
        node = new Node(4,2);
    }

    @org.junit.Test
    public void testInsertAndGet() throws Exception {
        node.insert("2","0");
        node.insert("3","1");

        assertEquals("0",node.get("2"));
        assertEquals("1",node.get("3"));
    }

    @Test
    public void testWriteNode() throws Exception {
        RandomAccessFile ra = new RandomAccessFile("text.txt","rw");
        ra.setLength(500);


        node.insert("2","0");
        node.insert("3","1");

        node.writeNode(ra,2);
        ra.close();

        ra = new RandomAccessFile("text.txt","r");
        Node n = node.readNode(ra,2);

        ra.close();

        assert n!=null;
        assertEquals(n.get("2"), "0");
        assertEquals("1",n.get("3"));
        assertEquals(null,n.get("1"));
    }

    @Test
    public void testFind() throws Exception {
        RandomAccessFile ra = new RandomAccessFile("text.txt","r");
        Node q = Node.readNode(4,ra,2);
        BPTree tree = new BPTree(q,ra);

        Node n = tree.find("2");

        assertEquals(n.get("2"), "0");
        assertEquals("1",n.get("3"));
        assertEquals(null,n.get("1"));


    }
}