package com.yy.cs.base.task;

public interface Task {
	
	
	
	
	public void start();
	/**
     * close the channel.
     */
    void close();
    
    /**
     * Graceful close the channel.
     */
    void close(int timeout);
    
    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();
}
