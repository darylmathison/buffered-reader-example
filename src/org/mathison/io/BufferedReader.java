
package org.mathison.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;

public class BufferedReader extends FilterReader {

    private static final int DEFAULT_BUFFER_SIZE = 10 * 1024; //10k buffer by default
    private int bufferSize;
    private char[] buffer;
    private int endIndex;
    private int offset;
    
    public BufferedReader(Reader reader) {
        super(reader);
        bufferSize = DEFAULT_BUFFER_SIZE;
        buffer = new char[bufferSize];
        offset = 0;
        endIndex = 0;
    }
    
    public BufferedReader(Reader reader, int bufferSize) {
        super(reader);
        this.bufferSize = bufferSize;
        buffer = new char[bufferSize];
        offset = 0;
        endIndex = 0;
    }
    
    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public boolean ready() throws IOException {
        return (offset != endIndex || in.ready());
    }

    @Override
    public long skip(long n) throws IOException {
        long numSkipped = 0;
        long leftToSkip = n;
        int lenRead = 0;
        while(leftToSkip > 0 && lenRead != -1) {
            if((offset + leftToSkip) <= endIndex) {
                offset += leftToSkip;
                numSkipped += leftToSkip;
                leftToSkip -= leftToSkip;
            } else if((offset + leftToSkip) > endIndex) {
                lenRead = fillBuffer();
                if(lenRead != -1) {
                    int amountBuffered = endIndex - offset;
                    long amountToSkip = (amountBuffered < leftToSkip)? 
                            amountBuffered:leftToSkip;
                    offset += amountToSkip;
                    numSkipped += amountToSkip;
                    leftToSkip -= amountToSkip;
                }
            }
        }
        
        return numSkipped;
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    @Override
    public int read() throws IOException {
        int character = -1;
        if(offset == endIndex) {
            int readLen = fillBuffer();
            if(readLen != -1) {
                character = buffer[offset];
                offset++;
            } // else the default of -1 will work
        } else {
            character = buffer[offset];
            offset++;
        }
        return character;   
    }

    @Override
    public int read(CharBuffer target) throws IOException, ReadOnlyBufferException {
        if(target == null) {
            throw new NullPointerException("target cannot be null");
        }
        if(target.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        int totalRead = 0;
        int readLen = 0;
        boolean noRead = true;
        int length;
        int spaceLeft = target.remaining();
        
        while(readLen != -1 && spaceLeft > 0) {
            if((offset + spaceLeft) <= endIndex) {
                target.put(buffer, offset, spaceLeft);
                noRead = false;
                spaceLeft -= spaceLeft;
                totalRead += spaceLeft;
                offset += spaceLeft;
            } else if((offset + spaceLeft) > endIndex) {
                readLen = fillBuffer();
                if(readLen != -1) {
                    length = endIndex - offset;
                    int lenToCopy = (spaceLeft < length)? spaceLeft:length;
                    target.put(buffer, offset, lenToCopy);
                    offset += lenToCopy;
                    totalRead += lenToCopy;
                    spaceLeft -= lenToCopy;
                    noRead = false;
                }
            }
        }
        if(noRead) {
            totalRead = -1;
        }
        
        return totalRead;
    }

    @Override
    public int read(char[] cbuf, int off, int length) throws IOException {
        int totalRead = 0;
        boolean noReads = true;
        int targetOffset = off;
        int newTargetOffset = off + length;
        int readLen = 0;
        int lenToCopy = 0;
        int leftToCopy = length;
        
        while(targetOffset < newTargetOffset && readLen != -1) {
            if((offset + leftToCopy) <= endIndex) {
                System.arraycopy(buffer, offset, cbuf, targetOffset, leftToCopy);
                offset += leftToCopy;
                targetOffset += leftToCopy;
                totalRead += leftToCopy;
                leftToCopy -= leftToCopy;
                noReads = false;
            } else if((offset + leftToCopy) > endIndex) {
                readLen = fillBuffer();
                if(readLen != -1) {
                    int amountBuffered = endIndex - offset;
                    lenToCopy = (amountBuffered < leftToCopy)? amountBuffered:leftToCopy;
                    System.arraycopy(buffer, offset, cbuf, targetOffset, lenToCopy);
                    noReads = false;
                    offset += lenToCopy;
                    targetOffset += lenToCopy;
                    leftToCopy -= lenToCopy;
                    totalRead += lenToCopy;
                }
            }
        }
        
        if(noReads) {
            totalRead = -1;
        }

        return totalRead;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
    
    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        boolean foundCR = false;
        boolean foundLinefeed = false;
        boolean foundBoth = false;
        int readLen = 0;
        final char LINEFEED = '\n';
        final char CR = '\r';
        
        if(offset == endIndex) {
            readLen = fillBuffer();
        }
        
        while(!(foundCR || foundLinefeed || foundBoth) && readLen != -1) {
            if(buffer[offset] == CR) {
                foundCR = true;
                offset++;
                if(offset == endIndex) {
                    readLen = fillBuffer();
                    if(readLen != -1) {
                        if(buffer[offset] == LINEFEED) {
                            foundBoth = true;
                            offset ++;
                        } 
                    }
                } else if(buffer[offset] == LINEFEED) {
                    foundBoth = true;
                    offset++;
                } 
            } else if(buffer[offset] == LINEFEED) {
                foundLinefeed = true;
                offset ++;
            } else {
                line.append(buffer[offset]);
                offset++;
            }
            
            if(offset == endIndex) {
                readLen = fillBuffer();
            }
        }
        
        if(line.length() == 0) {
            return null;
        }
        
        return line.toString();
    }
    
    private int fillBuffer() throws IOException {
        int length;
        int lenRead = 0;
        long newOffset = offset + buffer.length;
        if(newOffset >= endIndex) {
            moveLeftoverToBeginning();
            endIndex = endIndex - offset;
            offset = 0;
            
            length = bufferSize - endIndex;
            lenRead = in.read(buffer, endIndex, length);
            if (lenRead != -1) {
               endIndex += lenRead;
            } else {
                //endIndex = offset;
            }
        } else if(newOffset < endIndex) {
            lenRead = 0;
        }
        return lenRead;
    }
    
    private void moveLeftoverToBeginning() {
        int len = endIndex - offset;
        System.arraycopy(buffer, offset, buffer, 0, len);
    }
}
