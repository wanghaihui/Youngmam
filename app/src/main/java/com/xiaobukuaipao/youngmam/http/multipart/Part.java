package com.xiaobukuaipao.youngmam.http.multipart;
import java.io.IOException;
import java.io.OutputStream;
/**
 * A part interface defines part length and function to write content
 */
public interface Part
{
    public long getContentLength(Boundary boundary);
    public void writeTo(final OutputStream out, Boundary boundary) throws IOException;
}
