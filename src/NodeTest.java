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
        Node q = node.readNode(ra,2);
        BPTree tree = new BPTree(q,ra);

        Node n = tree.find("2");

        assertEquals(n.get("2"), "0");
        assertEquals("1",n.get("3"));
        assertEquals(null,n.get("1"));
    }

    @Test
    public void testAddRelation() throws Exception {
        Node left = new Node(4,502);

        node.insert("f","10");
        node.insert("g","11");
        node.insert("h","11");
        node.insert("i","11");

        left.insert("a","0");
        left.insert("b","1");
        left.insert("c","2");

        Integer i1 = node.addRelated(left);
        Integer i2 = left.addRelated(node);

        System.out.println(node);
        System.out.println(left);

        assertNotEquals(i1,i2);
    }
}