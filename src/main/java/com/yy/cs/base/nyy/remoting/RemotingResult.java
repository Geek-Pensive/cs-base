package com.yy.cs.base.nyy.remoting;

import java.io.Serializable;

public class RemotingResult implements Result, Serializable {

    private static final long        serialVersionUID = -6925924956850004727L;

    private Object                   result;

    public Object getResult() {
		return result;
	}

	public RemotingResult(){
    }

    public RemotingResult(String result){
        this.result = result;
    }

    public Object recreate() throws Throwable {
        return result;
    }

    @Override
    public String toString() {
        return "RpcResult [result=" + result + "]";
    }
}