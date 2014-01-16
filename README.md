# CS-Base

---
## 概要

> | *项*                                 | *详细*                                                      |
> | ------------------------------------ | ----------------------------------------------------------- |
> | 状态                                 | 建设中                                                      |
> | 负责人                               | 李方杰（开发），吴浩清（开发），欧阳柱（开发）              |
> | 应用业务                             |                                                             |

---
## 使用
#### pom.xml配置

    <dependency>
      <groupId>com.yy.cs</groupId>
      <artifactId>cs-base</artifactId>
      <version>0.2</version>
    </dependency>


---
## Redis连接
### 简要说明
*	在Jedis的基础上封装
*	支持主从部署 | 支持单例
*	接口简易

### 使用spring接入
#### 	spring 配置

	<bean id="redisClientFactory" class="com.yy.cs.base.redis.RedisClientFactory" init-method="init" destroy-method="destroy">
		<property name="maxActive" value="300" /><!-- 可选 -->
		<property name="maxIdle" value="100" /><!-- 可选 -->
		<property name="maxWait" value="50" /><!-- 可选 -->
		<property name="redisServers">
			<list>
				<!-- 格式是  ip:port:pw:timeout -->
				<!-- 目前这三个实例都放在同个机器上,6379的实例为主库,其他两个为从库 -->
				<value>172.19.103.105:6331::</value>
				<value>172.19.103.105:6330::</value>
				<value>172.19.103.105:6379:fdfs123:</value>
			</list>
		</property>
	</bean>
	
	<bean id="redisClient" class="com.yy.cs.base.redis.RedisClient">
		<property name="factory" ref="redisClientFactory"></property>
	</bean>
	
	
####	api调用
	
	ApplicationContext context = new ClassPathXmlApplicationContext(
				"your-spring-application.xml");
		redisClient = (RedisClient) context.getBean("redisClient");
	redisClient.setAndReturn("helloworld","helloworld");
	

### 直接 Main方法接入

	RedisClientFactory redisClientFactory = new RedisClientFactory();
	List<String> list = new ArrayList<String>();
	//这里是业务要连接的redis
	list.add("172.19.103.105:6379:fdfs123:");
	redisClientFactory.setRedisServers(list);
	redisClientFactory.init();
	RedisClient redisClient = new RedisClient();
	redisClient.setFactory(redisClientFactory);
	System.out.println(redisClient.setAndReturn("test", "test"));
	
	
### 性能问题
*	 对比简易封装的RedisClient与原生Jedis的数据</br>
	 测试结果，两者相差不大：比如：</br>
	 test 1000 time cost 953 in Jedis</br> 
	 test 1000 time cost 807 in RedisClient</br>
	 test 1000 time cost 1170 in Jedis</br>
	 test 1000 time cost 823 in RedisClient

	 
*	 多线程性能
	
	将要进行
	 
### 联系人
	如果有问题或者建议，可联系吴浩清，真诚为你服务

	
---
## Timer Task
### 简要说明
*	基于jdk原生线程池进行了封装，提供了任务调用。
* 采用守护线程 | 支持cron表达式配置 | 提供了任务执行情况的状态获取 | 支持监控文件路径配置和文件格式配置（现提供html格式和文本格式）
*	接口简易

### 快速使用
#### 	spring 配置

	  <bean name="taskManage" class="com.yy.cs.base.task.TimerTaskManager" init-method="start" >
		  <property name="timerTasks">
		    <map>
		      	<entry key="com.yy.cs.base.task.TimerTaskTest" value-ref="timerTaskTest" />
		    </map>
		  </property>
		  <property name="monitorType" value="LOG"/>
		</bean>
		<bean id="timerTaskTest" class="com.yy.cs.base.task.TimerTaskTest" >
			<property name="cron" value="*/5 * * * * *" /> 
		</bean>
	
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-task.xml");
		TimerTaskManager	timerTaskManage =  (TimerTaskManage) context.getBean("taskManage");
		//获取所有task状态
		CsStatus status = timerTaskManage.getCsStatus();
		//释放所有任务
		timerTaskManage.destroy();
  

####	手动初始化

		TimerTaskManager timerTaskManage = new TimerTaskManager();
		TimerTaskTest time = new TimerTaskTest();
		time.setCron("*/30 * * * * *");
		timerTaskManage.addTimerTask(time);
		timerTaskManage.setMonitorfile("D:\\workspace\\monitortask.log");
    	timerTaskManage.setMonitorType(MonitorType.LOG);
		timerTaskManage.start();
		//获取所有task状态
		CsStatus status = timerTaskManage.getCsStatus();
		//释放所有任务
		timerTaskManage.destroy();
	
	
### 配置步骤
*	 定义任务</br>
	 继承com.yy.cs.base.task.TimerTask，实现抽象方法
	 void execute();
   任务只启动之后，会执行execute()方法。
   com.yy.cs.base.task.TimerTask里面有一个CsStatus的属性(CsStatus说明详见 ‘CsStatus’)
   用于标识当前任务的状态
   
   
*	 配置任务</br>	
		任务执行时间配置完全遵照spring的task的cron方式设置
					
		<bean id="timerTaskTest" class="com.yy.cs.base.task.TimerTaskTest" >
		<property name="cron" value="*/5 * * * * *" /> 
		<!-- 可选配置，如果配置了测认为是集群task -->
		<property name="cluster"  ref="cluster" />
		</bean>
		
		<!-- 集群任务配置 -->
		<bean id="cluster" class="com.yy.cs.base.task.ClusterConfig" >
			<!-- redisClient见Redis连接 -->
			<property name="redisClient" ref="redisClient"  /> 
		</bean>

	 
*	 配置任务管理器	

			<bean name="taskManage" class="com.yy.cs.base.task.TimerTaskManager" init-method="start" >
					<!-- 线程池大小配置启动线程池  -->
					<property name="poolSize" value="2" />
			    <!-- 在任务管理器中添加任务  -->
				 <property name="timerTasks">
					    <map>
					    		<!-- key代表任务id  -->
					      	<entry key="com.yy.cs.base.task.TimerTaskTest" value-ref="timerTaskTest" />
					    </map>
				 </property>
				 <property name="monitorfile" value="D:\\workspace\\monitortask.log"/>
				 <property name="monitorType" value="LOG"/>
			</bean>
			//获取状态对象
			CsStatus t = timerTaskManage.getCsStatus();
	 	 
### 状态监控说明
*	 任务状态的监控数据，默认会间隔5秒输出到当前系统的user.dir目录下monitortask.html文件中。
*	 此目录也支持配置，可以设置taskManage的monitorfile属性，输出到制定目录的指点文件中。
*	 监控文件输出格式支持配置，现阶段可以设置monitorType属性为LOG或HTML两种形式，分别以文本和网页形式输出监控数据。
*	 执行是否超时是指，本次执行的时间，超出了下次执行的时间；也就是本次执行时间超过了一个时间间隔。
*	 如果如果执行超时任务状态为WRONG,任务执行异常则状态FAIL。

*	 任务状态监控数据monitortask.html内容</br>
	 		<table cellpadding='0' cellspacing='0' align='center' border='1' >
		 		<tr>
		 				<th colspan='5' align='center' >总览</th>
		 		</tr>
		 		<tr>
				 		<th  align='center' >name</th>
				 		<th  align='center' >code</th>
				 		<th  align='center' >message</th>
				 		<th  align='center' >FailNumber</th>
				 		<th  align='center' >TotalNumber</th>
		 		</tr>
		 		<tr>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>TimerTaskManager</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>SUCCCESS</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'> </td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>0</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>2</td>
		 		</tr>
	 		</table>
	 		<hr />
	 		<br />
	 		<table cellpadding='0' cellspacing='0' align='center' border='1' >
		 		<tr>
				 		<th  align='center' >task id</th>
				 		<th  align='center' >code</th>
				 		<th  align='center' >message</th>
				 		<th  align='center' >下次执行时间</th>
				 		<th  align='center' >上次执行开始时间点</th>
				 		<th  align='center' >上次执行完成时间点</th>
				 		<th  align='center' >上次异常执行时间点</th>
				 		<th  align='center' >上次异常信息</th>
				 		<th  align='center' >执行是否超时</th>
		 		</tr>
		 		<tr>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>com.yy.cs.base.task.TimerTaskTest</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>SUCCCESS</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'> </td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>2013-12-03 16:47:35</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>2013-12-03 16:47:30</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>2013-12-03 16:47:31</td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'></td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'></td>
				 		<td align='center' style='padding:10px 10px 10px 10px;'>false</td>
		 		</tr>
	 		</table>	 	 

*	 任务状态监控数据monitortask.log内容</br>
		任务id;任务状态;异常时间:xxxxxx;异常信息:xxxxxx;(是否超时:xxxxxx;)


### 联系人
	如果有问题或者建议，可联系李方杰，真诚为你服务



---
## HTTP
### 简要说明
*	基于apache的httpclient-4.X版本封装，提供了常用的方法的调用，并且采用httpclient池。
*	池大小设置 | 超时时间设置
*	接口简易

### 快速使用
#### 初始化配置
   
   
			   com.yy.cs.base.http.CSHttpClientFactory factory = new com.yy.cs.base.http.CSHttpClientFactory();
			   factory.setMaxTotal(30);           			//池中的最大连接数
			   factory.setDefaultMaxPerRoute(4);  			//HttpClient中每个远程host最大连接数,一个host可能有多个连接
			   factory.setConnectionTimeout(5000);   			// 建立http连接的超时时间
			   factory.setSocketTimeOut(5000); 					  // socket读取的超时时间
			   factory.setConnectionRequestTimeout(5000);  // 从连接池获取连接的超时时间
	  		 com.yy.cs.base.http.CSHttpClient 	csHttpClient = new com.yy.cs.base.http.CSHttpClient(factory);
	
####	AIP说明(CSHttpClient)
	
	      /**
		     * @return 池化的原生httpClient
		     */
		    public HttpClient getHttpClient();
		    /**
		     * 执行一个http方法
		     * @param 执行方法的类型
		     * @return response正确返回后的字符串
		     * @throws HttpClientException
		     */
		    public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException;
		    /**
		     * 执行一个HttpGet方法
		     * @param 请求地址
		     * @return response正确返回后的字符串
		     * @throws HttpClientException
		     */
		    public String doGet(String url) throws HttpClientException;
				/**
		     * 执行一个HttpGet方法,返回response返回的流
		     * @param 请求地址
		     * @param 指点正确返回的状态码
		     * @return response正确返回后的流
		     * @throws HttpClientException
		     */
		    public InputStream getResponseStream(String url, int[] statusArray) throws HttpClientException;
				
				/**
		     * 发送一个HttpGet请求，检查地址是否正常 
		     * @param url地址
		     * @return true 'response返回响应状态码200或304'
		     */
			  public boolean isGetOK(String url);
			  /**
		     * 执行一个HttPost请求
		     * @param url 请求地址
		     * @param parameters 自动参数按utf-8编码
		     * @return  response正确返回后的字符串
		     * @throws HttpClientException
		     */
		    public String doPost(String url, Map<String, String> parameters) throws HttpClientException;
		 
	 
### 联系人
	如果有问题或者建议，可联系李方杰，真诚为你服务
	

---
## CsStatus
### 简要说明
*	通用状态的标准定义，当前项目很多项目的状态监控，表示形式不统一，为了简化运维监控的复杂度，所以定义了一种状态格式。


### 快速使用
####	AIP说明
		
		
		/**
		 * 设置状态code
		 * @param code
		 */
		public void setCode(StatusCode code)
		/**
		 * 设置状态信息
		 * @param message
		 */
		public void setMessage(String message)
		/**
		 * 设置当前状态的名称
		 * @param name
		 */
		public void setName(String name)
		/**
		 * 获取总状态数量
		 * @return
		 */
		public int getTotalNumber()
		/**
		 * 获取失败状态数量，包过本身
		 * @return
		 */
		public int getFailNumber()
		/**
		 * 获取某一个info信息
		 * @param key
		 * @return
		 */
		public Object getAdditionInfo(String key)
		/**
		 * additionInfo添加一个附加信息
		 * @param key
		 * @param value
		 */
		public void additionInfo(String key , Object value)
		/**
		 * 增加一个子节点状态
		 * @param csStatus
		 */
		public void addSubCsStatus(CsStatus csStatus)
		/**
		 * 获取所有的子节点状态
		 * @return
		 */
		public List<CsStatus> getSubCsStatus()
		
*	重写了toString方法，toString方法用json格式返回字符串
*	code属性采用StatusCode枚举对象，SUCCCESS：0，FAIL：1



---
## thrift
### 简要说明
*	为了简化用户thrift接口初始化和池化的过程，封装了一套以简单方式来引用thrift接口的方式。
*	默认采用libthrift-0.9.1版本的thrift，所依赖的版本取决于gen thrift接口所以的thrift.exe版本。
*	支持transport和protocol的配置。
*	简单，高效使用

### 快速使用
#### 	spring 配置
 
		<bean id="clientFactory" class="com.yy.cs.base.thrift.ThriftClientFactory" destroy-method="destroy"  >
			<property name="interfaceName"   value="com.yy.cs.base.thrift.NyyService$Iface" />
		  	<property name="thriftConfig">
		  		<list>
		  			<ref bean="thriftConfig"/>
		  		</list>
		  	</property>
		  	<property name="protocol"   value="BINARY" /><!--可选-->
		  	<property name="transport"  value="TSOCKET" /><!--可选-->
		</bean>
		<bean id="thriftConfig" class="com.yy.cs.base.thrift.ThriftConfig" >
			<property  name="host" value="127.0.0.1"/>
			<property  name="port" value="8181" />
			<property  name="weight" value="5" />
		</bean>
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-thrift.xml");
    ThriftClientFactory<Iface> c =  (ThriftClientFactory<Iface>) context.getBean("clientFactory");
    //返回封装好的thrift的原生接口的代理bean，
    com.yy.cs.base.thrift.NyyService.Iface face = c.getClient();
    

####	手动初始化
		
		ThriftConfig config = new ThriftConfig("127.0.0.1",8181);
 		ThriftClientFactory<Iface> thriftClient = new ThriftClientFactory<Iface>();		
		thriftClient.getThriftConfig().add(config);
		thriftClient.setInterface(Iface.class);
		//返回封装好的thrift的原生接口的代理bean，
		Iface face = thriftClient.getClient();   
		
		
### 配置说明
*	 ThriftConfig参数配置

| 属性			|类型			| 是否必填   |缺省值		|描述				|
|-----------------------|-----------------------|-----------------------|-----------------------|-----------------------|
| host			| String	|	是		|				|	 			地址		|
| port			| 	int		|	是		|				|  			端口		|
| timeout		| 	int		| 否		| 5000	|			超时时间				|
| weight		|   int		|	否		|  0		|	多地址时，池权重		|
| maxIdle		|   int		|	否		|  8		| 最大空闲空闲链接		|
| minIdle		|   int		|	否		|  0		| 最小空闲空闲链接		|
| maxActive	| 	int		|	否		|  8		| 	最大链接数				|

*	 ThriftClientFactory参数配置

| 属性			|类型			| 是否必填   |缺省值		|描述				|
|-----------------------|-----------------------|-----------------------|-----------------------|-----------------------|
| interfaceName			| String	|	 是		|				|	 		接口名称;thrift的实际接口都是内部类，在引用时需要用'$'替换'.'		|
| thriftConfig			| 	List<ThriftConfig>		|	是		|			|  		多服务器列表			|
| protocol			| 	ProtocolType		|	否		|	BINARY	|  	thrift原生protocol方式				|
| transport			| 	TransportType		|	否		|	TFRAMED		|  thrift原生transport方式			|


---
## Time

### 规范建议

在开发中经常会碰到时间方面的问题，虽然都是一些简单问题，但却很费时间。这里尝试定义一些通用的标准，并提供帮助类，以期减少开发和沟通的成本。

**注意：这里关于时间的定义均是针对开发和协议，界面的展现方面则需要根据产品需求来定**
**除以下格式外，建议尽量减少其它格式的引入**

#### 基本建议

- 尽量保存和传输不带格式的准确时间（如下面的格式一和格式二）
- 把时间转换成需要的格式，这个工作尽量推后做（例如，仅在需要展现时）
- 不要信任来自用户电脑上的时间（也可以理解为来自用户请求中的时间）

#### Java

| *编号* | *名称*       | *格式/形式*         | *类型* | *描述*                                                                                                     |
| ----   | ----         | ----                | ----   | ----                                                                                                       |
| 格式一 | milliseconds | 1342427800000       | Long   | 从1970-01-01数起的毫秒数，内部推荐使用这种格式，因为具有最大的灵活性，且不存在时区的烦恼（如果需要时区，建议额外保存）                   |
| 格式二 | seconds      | 1342427800          | Long   | 从1970-01-01数起的秒数，也是UNIX格式的时间，内部推荐使用这种格式，因为具有最大的灵活性，且不存在时区的烦恼 |
| 格式三 | readable     | 2011-12-20 18:10:08 | String | 便于阅读，但不包含时区信息，理论上并不是一个确切的时间。在一些不需要关注确切时间的场合下可以使用           |


