package com.yy.cs.base.task.execute.lock;

/**
 * 
 */
public interface TaskLock {

     
    /**
     * 取某个时间点的锁, 如果该执行时间点任务已经被锁，则返回false
     * @param id
     * @param executeTime
     * @return
     */
    boolean lock(String id, long executeTime);

    
     
    String getExecuteAddress(String id,long value);
//    /**
//     * 
//     * @param key
//     * @param value
//     * @return
//     */
//    boolean unLock(String id, long executeTime);
    
}
