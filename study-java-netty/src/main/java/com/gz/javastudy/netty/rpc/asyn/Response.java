package com.gz.javastudy.netty.rpc.asyn;
public class Response {
	private long id;
	private Object result;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Response{" +
				"id=" + id +
				", result=" + result +
				'}';
	}
}
