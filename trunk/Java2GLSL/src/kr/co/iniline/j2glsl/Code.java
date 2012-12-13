package kr.co.iniline.j2glsl;

import java.io.IOException;

public abstract class Code implements CodeWriter {
	int idx = -1;
	private Code previous = null;
	private Code next = null;
	
//	public void insert(Code... codes) {
//		Code oldNextCode = getNext();
//		setNext( codes[0] );
//		codes[0].setPrevious(this);
//		codes[codes.length-1].setNext(oldNextCode);
//		oldNextCode.setPrevious( codes[codes.length-1] );
//		for(int i=1; i<codes.length;i++) {
//			codes[i].setPrevious( codes[i-1] );
//			codes[i-1].setNext( codes[i] );
//		}
//	}
//	
//	public Code replace(Code code) {
//		getPrevious().setNext( code );
//		getNext().setPrevious( code );
//		return this;
//	}
//	
//	public Code remove() {
//		getPrevious().setNext( getNext() );
//		getNext().setPrevious( getPrevious() );
//		return this;
//	}
	
	public void setPrevious(Code code) {
		this.previous = code;
	}
	
	public void setNext(Code code) {
		this.next = code;
	}
	
	public Code getPrevious() {
		return previous;
	}
	
	public Code getNext() {
		return next;
	}
	
	public abstract String toParamString();
}
