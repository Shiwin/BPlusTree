import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.io.RandomAccessFile;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * BPTreeTests
 */
public class BPTreeTest {

    BPTree tree ;
    RandomAccessFile ra;


    @Before
    public void setUp() throws Exception {
        ra = new RandomAccessFile("tree_test.txt","rw");
        tree = new BPTree(ra);
    }

    @After
    public void tearDown() throws Exception {
        ra.close();
    }

    @Test
    public void testInsert() throws Exception {
        tree.insert("5","5");
        tree.insert("7","7");
        tree.insert("6","6");
        tree.insert("9","9");
        tree.insert("45","45");
        tree.insert("545","545");
        tree.insert("8","8");
//        tree.insert("654","654");
  //      tree.insert("335","335");

        tree.traverse();
    }



}