package com.yy.cs.base.task.execute.lock;

/**
 *  集中式task的锁对象接口
 */
public interface TaskLock {

     
    /**
     * 判断任务在时间点  executeTime 之前是否已经被执行。 判断这个任务在redis中是否已经存在。
     * <p>如果不存在，则将任务标识id存入到redis中，并且返回true；如果已经存在则返回false；
     * @param id
     * 		一个标识
     * @param executeTime
     * 		任务将被执行的时间
     * @return
     * 	    boolean 任务是否已经执行
     */
    boolean lock(String id, long executeTime);

    
    /**
     * 获取执行的地址 
     * @param id
     * 		任务的标识id
     * @param value
     * 		k-v中的value,这里通常需要传入的参数是某个任务执行的时间点
     * @return
     * 		ip地址
     */
    String getExecuteAddress(String id,long value);
//    /**
//     * 
//     * @param key
//     * @param value
//     * @return
//     */
//    boolean unLock(String id, long executeTime);
    
}
