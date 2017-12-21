/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Shine
 */
public class MessageTest {
    
    public MessageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getMsgType method, of class Message.
     */
    @Test
    public void testGetMsgType() {
        System.out.println("getMsgType");
        Message instance = null;
        MessageType expResult = null;
        MessageType result = instance.getMsgType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessage method, of class Message.
     */
    @Test
    public void testGetMessage() {
        System.out.println("getMessage");
        Message instance = null;
        String expResult = "";
        String result = instance.getMessage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSecretWord method, of class Message.
     */
    @Test
    public void testGetSecretWord() {
        System.out.println("getSecretWord");
        Message instance = null;
        String expResult = "";
        String result = instance.getSecretWord();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRemainingAttempts method, of class Message.
     */
    @Test
    public void testGetRemainingAttempts() {
        System.out.println("getRemainingAttempts");
        Message instance = null;
        int expResult = 0;
        int result = instance.getRemainingAttempts();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScore method, of class Message.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        Message instance = null;
        int expResult = 0;
        int result = instance.getScore();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isGameRunning method, of class Message.
     */
    @Test
    public void testIsGameRunning() {
        System.out.println("isGameRunning");
        Message instance = null;
        boolean expResult = false;
        boolean result = instance.isGameRunning();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isConnectedToServer method, of class Message.
     */
    @Test
    public void testIsConnectedToServer() {
        System.out.println("isConnectedToServer");
        Message instance = null;
        boolean expResult = false;
        boolean result = instance.isConnectedToServer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHiddenWord method, of class Message.
     */
    @Test
    public void testGetHiddenWord() {
        System.out.println("getHiddenWord");
        Message instance = null;
        String expResult = "";
        String result = instance.getHiddenWord();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
