package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.io.Writer;

public class TabbedWriter extends Writer {
	Writer orgWriter = null;
	int frameCount = 0;
	
	public TabbedWriter(Writer source) {
		orgWriter = source;
	}

	@Override
	public void close() throws IOException {
		orgWriter.close();
	}

	@Override
	public void flush() throws IOException {
		orgWriter.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		orgWriter.write(cbuf, off, len);
	}
	
	public void startFrame() throws IOException {
		orgWriter.append("{");
		frameCount++;
	}
	
	public void endFrame() throws IOException {
		frameCount--;
		if( frameCount < 0 ) frameCount = 0;
		appendNewTabbedLine();
		orgWriter.append("}");
	}
	
	public void appendTab() throws IOException {
		for (int i = 0; i < frameCount; i++) {
			orgWriter.append("\t");
		}
	}
	
	public void appendNewTabbedLine() throws IOException {
		orgWriter.append('\n');
		appendTab();
	}
}
