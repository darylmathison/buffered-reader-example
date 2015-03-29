/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mathison.io;

import org.mathison.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daryl
 */
public class BufferedReaderTest {
    
    private BufferedReader reader = null;
    public static final String EXPECTED_STRING = "The quick brown fox jumped over the lazy dog.";
    public static final String BUFFER_STRING = EXPECTED_STRING + "\n" 
            + EXPECTED_STRING + "\r" + EXPECTED_STRING + "\r\n" + EXPECTED_STRING;
    
    public BufferedReaderTest() {
    }
    
    @Before
    public void setUp() {
        reader = new BufferedReader(new StringReader(BUFFER_STRING), 50);
    }
    
    @After
    public void tearDown() {
        try {
            reader.close();
        } catch(IOException e) {
            // who cares
        }
    }

    /**
     * Test of reset method, of class BufferedReader.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testReset() throws Exception {
        System.out.println("testReset");
        reader.reset();
    }

    /**
     * Test of mark method, of class BufferedReader.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testMark() throws Exception {
        System.out.println("testMark");
        reader.mark(20);
    }

    /**
     * Test of markSupported method, of class BufferedReader.
     */
    @Test
    public void testMarkSupported() {
        System.out.println("testMarkSupported");
        assertFalse(reader.markSupported());
    }

    /**
     * Test of ready method, of class BufferedReader.
     */
    @Test
    public void testReady() throws Exception {
        System.out.println("testReady");
        boolean expResult = true;
        boolean result = reader.ready();
        assertEquals(expResult, result);
    }

    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipBeginningToMiddle() throws Exception {
        System.out.println("testSkipBeginningToMiddle");
        long n = BUFFER_STRING.length()/2;
        long expResult = n;
        long result = reader.skip(n);
        assertEquals(expResult, result);
    }

    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipBeginningToEnd() throws Exception {
        System.out.println("testSkipBeginningToEnd");
        long n = BUFFER_STRING.length();
        long expResult = n;
        long result = reader.skip(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipBeginningToPastEnd() throws Exception {
        System.out.println("testSkipBeginningToPastEnd");
        long n = BUFFER_STRING.length() + 1;
        long expResult = BUFFER_STRING.length();
        long result = reader.skip(n);
        assertEquals(expResult, result);
    }
    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipMiddleToPastEnd() throws Exception {
        System.out.println("testSkipMiddleToPastEnd");
        reader.skip(BUFFER_STRING.length()/2);
        long n = BUFFER_STRING.length();
        System.out.println("BUFFER_STRING.length()" + n);
        long expResult = n - BUFFER_STRING.length()/2;
        long result = reader.skip(n); 
        System.out.println("184th char = " + reader.read());
        assertEquals(expResult, result);
    }

    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipEndToPastEnd() throws Exception {
        System.out.println("testSkipEndToPastEnd");
        reader.skip(BUFFER_STRING.length());
        long n = 1;
        long expResult = 0;
        long result = reader.skip(n);
        assertEquals(expResult, result);
    }

    /**
     * Test of skip method, of class BufferedReader.
     */
    @Test
    public void testSkipMiddleToEnd() throws Exception {
        System.out.println("testSkipMiddleToEnd");
        reader.skip(BUFFER_STRING.length()/2);
        long n = BUFFER_STRING.length()/2;
        long expResult = n;
        long result = reader.skip(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadCharArrayLessThanStringBuffer() throws Exception {
        System.out.println("testReadCharArrayLessThanStringBuffer");
        final int strLen = BUFFER_STRING.length();
        char[] cbuf = new char[strLen/2];
        int expResult = cbuf.length;
        String expString = BUFFER_STRING.substring(0, strLen/2);
        int result = reader.read(cbuf);
        assertEquals(expResult, result);
        assertEquals(expString, new String(cbuf));
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadCharArrayGreaterThanStringBuffer() throws Exception {
        System.out.println("testReadCharArrayGreaterThanStringBuffer");
        final int strLen = BUFFER_STRING.length();
        char[] cbuf = new char[strLen + 1];
        int expResult = strLen;
        int result = reader.read(cbuf);
        assertEquals(expResult, result);
        assertEquals(BUFFER_STRING, new String(cbuf).substring(0, strLen));
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadCharArrayAfterReaderIsFinished() throws Exception {
        System.out.println("testReadCharArrayAfterReaderIsFinished");
        final int strLen = BUFFER_STRING.length();
        reader.skip(strLen);
        char[] cbuf = new char[strLen];
        int expResult = -1;
        int result = reader.read(cbuf);
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadBegining() throws Exception {
        System.out.println("testReadBegining");
        int expResult = BUFFER_STRING.charAt(0);
        int result = reader.read();
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadMiddle() throws Exception {
        System.out.println("testReadMiddle");
        final int length = BUFFER_STRING.length();
        long skip = length/2;
        reader.skip(skip);
        System.out.println("skip = " + skip);
        int expResult = BUFFER_STRING.charAt((int)skip);
        int result = reader.read();
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadEnd() throws Exception {
        System.out.println("testReadEnd");
        final int length = BUFFER_STRING.length();
        long skip = length;
        reader.skip(skip - 1);
        int expResult = BUFFER_STRING.charAt(length - 1);
        int result = reader.read();
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadPastEnd() throws Exception {
        System.out.println("testReadPastEnd");
        final int length = BUFFER_STRING.length();
        long skip = length;
        reader.skip(skip);
        int expResult = -1;
        int result = reader.read();
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testReadCharBufferBeginningToEnd() throws Exception {
        System.out.println("testReadCharBufferBeginningToEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        CharBuffer target = CharBuffer.allocate(lenOfBufferStr);
        int expResult = lenOfBufferStr;
        int result = reader.read(target);
        assertEquals(expResult, result);
    }

    @Test
    public void testReadCharBufferBeginningToMiddle() throws Exception {
        System.out.println("testReadCharBufferBeginningToMiddle");
        final int lenOfBufferStr = BUFFER_STRING.length();
        CharBuffer target = CharBuffer.allocate(lenOfBufferStr/2);
        int expResult = lenOfBufferStr/2;
        int result = reader.read(target);
        assertEquals(expResult, result);
    }

    @Test
    public void testReadCharBufferMiddleToEnd() throws Exception {
        System.out.println("testReadCharBufferMiddleToEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        CharBuffer target = CharBuffer.allocate(lenOfBufferStr/2);
        int expResult = lenOfBufferStr/2;
        reader.skip(lenOfBufferStr/2);
        int result = reader.read(target);
        assertEquals(expResult, result);
    }

    @Test
    public void testReadCharBufferEndToPastEnd() throws Exception {
        System.out.println("testReadCharBufferEndToPastEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        CharBuffer target = CharBuffer.allocate(lenOfBufferStr);
        int expResult = -1;
        reader.skip(lenOfBufferStr);
        int result = reader.read(target);
        assertEquals(expResult, result);
    }    
    
    /**
     * Test of read method, of class BufferedReader.
     */
    @Test
    public void testByteOffsetLenReadBeginingToEnd() throws Exception {
        System.out.println("testByteOffsetLenReadBeginingToEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        char[] cbuf = new char[lenOfBufferStr];
        int off = 0;
        int len = lenOfBufferStr;
        int expResult = lenOfBufferStr;
        int result = reader.read(cbuf, off, len);
        String resultStr = new String(cbuf);
        System.out.println("expString = " + BUFFER_STRING + "\n\n" 
                + "resultStr = " + resultStr);
        assertEquals(expResult, result);
        assertEquals(BUFFER_STRING, resultStr);
    }

    @Test
    public void testByteOffsetLenReadMiddleToEnd() throws Exception {
        System.out.println("testByteOffsetLenReadMiddleToEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        char[] cbuf = new char[lenOfBufferStr/2];
        int off = 0;
        int expResult = lenOfBufferStr/2;
        String expString = BUFFER_STRING.substring(lenOfBufferStr/2);
        
        reader.skip(lenOfBufferStr/2);
        int result = reader.read(cbuf, off, cbuf.length);
        String resultStr = new String(cbuf);
        System.out.println("'" + expString + "'");
        System.out.println("'" + resultStr + "'");
        assertEquals(expResult, result);
        assertEquals(expString, resultStr);
    }

    @Test
    public void testByteOffsetLenReadEndToPastEnd() throws Exception {
        System.out.println("testByteOffsetLenReadEndToPastEnd");
        final int lenOfBufferStr = BUFFER_STRING.length();
        char[] cbuf = new char[lenOfBufferStr];
        int off = 0;
        int len = lenOfBufferStr;
        int expResult = -1;
        reader.skip(lenOfBufferStr);
        int result = reader.read(cbuf, off, len);
        assertEquals(expResult, result);
    }

    /**
     * Test of close method, of class BufferedReader.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("testClose");
        reader.close();
    }
    
    @Test
    public void testReadLine() throws Exception {
        System.out.println("testReadLine");
        int lineNum = 1;
        String line = reader.readLine();
        while(line != null) {
            System.out.println(line);
            assertEquals("failed to read line at line " + lineNum, EXPECTED_STRING, line);
            line = reader.readLine();
            lineNum++;
        }
        if(lineNum == 1) {
            fail("ReadLine failed to read the first line");
        }
    }
}
