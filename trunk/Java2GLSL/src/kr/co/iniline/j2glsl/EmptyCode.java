package kr.co.iniline.j2glsl;

import java.io.IOException;

public class EmptyCode extends Code {
	@Override
	public void write(TabbedWriter writer) throws IOException {
	}

	@Override
	public String toParamString() {
		return "emptyCode:"+idx;
	}
}
