# NYY通信协议框架

----
## 概要

> | **项**   | **详细**                                             |
> | ---      | ---                                                  |
> | 状态     | 初稿                                                 |
> | 撰写人   | 欧阳柱  吴浩清                                       |
> | 应用业务 | 已有重要项目生产应用中<br>精彩世界数据接口（开发中）</br>在线集合 |

目前很多业务和服务的开发过程中，有大量的通信需求，但缺少一种统一的设计思路。导致目前通信在协议形式，依赖技术，实现方式等方面都有较大的差异，重复的工作较多，不利于团队的积累。

这里尝试定义一种通信协议的设计思路，主要目的为简化通信协议的设计，减轻开发工作量，降低沟通的成本。理想的状态是，业务只需要约定业务数据与字段，说明使用”NYY协议，封装方式一，安全场景一”，即可以双方无依赖的进行开发。

----
## 思路

1. 统一编码与算法
	- 说明一：对于ASCII以外的编码，使用UTF-8。
	- 说明二：对于一些需要使用字符串表示二进制字节的地方，都使用BASE64。
	- 说明三：哈希选用SHA256，对称加密使用AES（AES/ECB/PKCS5PADDING，key长度选用128bit）
	<br/><br/>

1. 扁平的数据结构
	- 说明：即尽量以Key-Value的形式来定义通信协议，尽量减少数据结构嵌套的层次。
	- 好处：易于扩展，请求与响应均可以看作是一个Map，可以很简单的通过增加Key来扩展协议
	<br/><br/>

1. HTTP/HTTPS作通信承载
	- 说明一：HTTP可以很简单的通过反向代理/DNS等支持Load Balance，Failover。HTTPS可以提供链路加密。
	- 说明二：建议同时支持HTTP GET和POST方式。
	- 说明三：普遍认为HTTP的短连接性能不高，其实HTTP也支持长连接，虽然理论上仍不及一些二进制协议，但大多数业务场景下并不是瓶颈所在。
	- 好处：十分通用的技术，Web前端的Javascript天然支持，其它语言也容易支持。
	<br/><br/>

1. JSON作数据封装
	- 说明：也可以使用HTTP的Query String替代JSON，Query String在HTTP GET请求中更易读，但对复杂数的封装能力不够
	- 好处一：十分通用的技术，Web前端的Javascript天然支持，其它语言也容易支持。
	- 好处二：客户端与服务器端无需严格的依赖，仅需有Key的约定即可。
	<br/><br/>

1. 提供通用的安全机制
	- 说明：下面”具体设计”一节会有详细说明
	- 好处：减轻业务在安全方面的工作量
	<br/><br/>

----
## 具体设计

### 协议内容

实际内容（假设请求和返回都是它）
>	`appId=1,sign="x",data={"k1":"v1"}` 

HTTP GET请求方式一（URL，整个JSON作为"nyy="的值，需要URL Encoding）
>	`http://a.com/abc?nyy=%7B%22appId%22:1,%22sign%22:%22x%22,%22data%22:%7B%22k1%22:%22v1%22%7D%7D` 

HTTP GET请求方式二（URL，分"appId","sign","data"三个key，需要URL Encoding）
>	`http://a.com/abc?appId=1&sign=x&data=%7B%22k1%22:%22v1%22%7D` 

HTTP POST请求方式（Body内容，不作URL Encoding）	
>	`{"appId":1,"sign":"x","data":{"k1":"v1"}}` 

HTTP返回（不作URL Encoding）	
>	`{"appId":1,"sign":"x","data":{"k1":"v1"}}` 


### 协议说明

- appId：由服务器端分配给业务端的ID。
- sign：用于安全校验的Hash（如SHA256），或是UDB的认证Token。
- data：所有的业务数据（建议扁平的Key-Value形式）
- 请求有两种GET方式和一种POST方式，服务器兼容三种方式最好，先其中一种支持也可以

### 建议

1. 业务字段的设计中不建议使用"appId"这个key。因为如果与外层的"appId"值不一致的话，容易引起安全上的问题（身份伪造）。
   当然，data字段中为了开发或解析上的便利，可以再带上一份一样的"appId"（但它与外层的"appId"语义上要一致，而不是具体业务中的其它含义）
   后续的基础库可能会添加这方面的安全校验。

2. data字段中均为业务字段，可以自由定义，这里尝试建议如下一些常用的key，以期减小沟通成本：
   - statusCode：响应中的状态/结果码，String，业务自定义内容，如：123, APP_NOT_EXIST
   - statusMsg：响应中的状态/结果信息，String，针对statusCode给出的详细信息

----
## 安全机制

这里的安全校验，是协议层面的安全机制。与其它层面的安全机制（如应用级的白名单，机器级的iptables等）并不冲突，而且往往可以形成互补，提高系统整体的安全性。

下面列举出几种主要的安全场景供选择，对安全要求一般的，可以不看后面的场景以节省脑细胞。

### 安全场景一：无需安全检查

在最简单的场景下，业务可能不需要任务安全检查。此时把appId和sign留空即可。

但仍旧建议保留appId和sign在设计中，一是为将来预留，二是通用的代码可以统一支持

| **数据项**      | **示例**                                                       |
| ----            | ----                                                           |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码 |
| 事先约定的Key   | （不需要）                                                     |
| 生成哈希的源串  | （不需要）                                                     |
| 消息中appId的值 | （为空即可）                                                   |
| 消息中sign的值  | （为空即可）                                                   |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`                                |

### 安全场景二：仅需要简单的完整性校验（服务器间通信）

在一些简单的服务器间通信过程中，仅需要检验数据的完整性，并不需要作更高的安全要求。或者更高的安全需求已经通过其它机制完成。
此时，服务端可以给每个不同的客户端（appId）分配一个唯一的key。通信中对data进行如下的哈希操作（哈希算法使用SHA256）：

| **数据项**      | **示例**                                                                                                   |
| ----            | ----                                                                                                       |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                             |
| 事先约定的Key   | `ljfadjaf023ur32lj`                                                                                        |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}&key=ljfadjaf023ur32lj` <br>注意这里是字符串而不是对象，应使用UTF-8编码 |
| 消息中appId的值 | （事先分配给对应业务的id）                                                                                 |
| 消息中sign的值  | `SHA256(data={"chId":"Zfb","payer":"小王"}&key=ljfadjaf023ur32lj)`                                         |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`<br/>在HTTP请求中**需要**进行URL Encode                                     |
	
### 安全场景三：仅需要简单的完整性校验（客户端与服务器端通信）

在一些非服务器间通信的过程中，因为代码运行于用户端，分配appId和key的做法变得不安全。此时需要通过UDB的安全Token来进行校验才可以实现。

简述一下背后原理：客户端生成UDB Token时，UDB接口允许带入一个字符串，而服务器端利用UDB接口验证这个Token时，接口会返回生成Token时所带入的字符串。这里的字符串我们采用data的哈希值（哈希算法使用SHA256，但不再附加key），之所以采用data的哈希值而不使用data本身，是为了避免生成的Token过大。

这种机制需要依赖公司UDB的相关验证接口，UDB对已有一定的支持（Javascript前端暂时好像没支持）。目前在一些重要的业务过程中已经成功使用了这种机制，欢迎联系咨询。

| **数据项**      | **示例**                                                                                                        |
| ----            | ----                                                                                                            |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                                  |
| 事先约定的Key   | （利用UDB的安全机制，无需事先确定key）                                                                          |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}` <br>注意这里是字符串而不是对象，应使用UTF-8编码                            |
| 消息中appId的值 | （为空即可）                                                                                                    |
| 消息中sign的值  | `gen_udb_token(  SHA256(data={"chId":"Zfb","payer":"小王"})  )` <br>使用UDB提供的库，把上面”源串”的哈希值也带入 |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`<br/>在HTTP请求中**需要**进行URL Encode                                          |

### 安全场景四：需要较安全的加密（服务器间通信）

在一些安全性要求很高的场合，也可以对data进行加密，这样既除了保证数据的完整性，还可以加强数据的保密性。加密算法使用AES。

注：也可以通过HTTPS的方式实现数据的保密性，但不能做到针对不同appId这样的粒度。

| **数据项**      | **示例**                                                                                                                        |
| ----            | ----                                                                                                                            |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}`  <br/>Json字符串，使用UTF-8编码                                                                 |
| 事先约定的Key   | `ljfadjaf023ur32lj`                                                                                                             |
| 用于加密的源串  | `{"chId":"Zfb","payer":"小王"}`<br/>注意这里是字符串而不是对象，应使用UTF-8编码                                                 |
| 消息中appId的值 | （事先分配给对应业务的id）                                                                                                      |
| 消息中sign的值  | （为空即可）                                                                                                                    |
| 消息中data的值  | `BASE64(  AES({"chId":"Zfb","payer":"小王"}, ljfadjaf023ur32lj)  )`<br/>注意：在HTTP请求中，生成的字节码需要转换成BASE64，**不需要**进行URL Encode |
	
### 安全场景五：需要较安全的加密（客户端与服务器端通信）

在一些安全性要求很高的场合，也可以对data进行加密，这样既除了保证数据的完整性，还可以加强数据的保密性。加密算法使用AES。

在一些非服务器间通信的过程中，因为代码运行于用户端，分配appId和key的做法变得不安全。此时需要通过UDB的安全Token来进行校验才可以实现。

简述一下背后原理：客户端生成UDB Token时，UDB接口允许带入一个字符串，而服务器端利用UDB接口验证这个Token时，接口会返回生成Token时所带入的字符串。这里的字符串我们采用针对data加密的随机密钥。

这种机制需要依赖公司UDB的相关验证接口，UDB对已有一定的支持（Javascript前端暂时好像没支持）。

| **数据项**      | **示例**                                                                                                                                            |
| ----            | ----                                                                                                                                                |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                                                                      |
| 事先约定的Key   | （客户端随机生成一个key即可以，无需事先约定） `58wfwY5w33o5w95we`                                                                                   |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}` <br>注意这里是字符串而不是对象，应使用UTF-8编码                                                                |
| 消息中appId的值 | （为空即可）                                                                                                                                        |
| 消息中sign的值  | `gen_udb_token(58wfwY5w33o5w95we)` <br>使用UDB提供的库，把上面的”源串”作为参数的一部分生成的Token                                                   |
| 消息中data的值  | `BASE64(  AES({"chId":"Zfb","payer":"小王"}, 58wfwY5w33o5w95we)  )`<br/>注意：在HTTP请求中，生成的字节码需要转换成BASE64，**不需要**进行URL Encode |



----
## 代码支持（Java）

### 依赖Jar包

	<dependency>
		<groupId>com.yy.cs</groupId>
 		<artifactId>cs-base</artifactId>
		<version>0.0.4-SNAPSHOT</version>
	</dependency>
	
### 服务端示例(主要是使用NyyProtocolHelper类)
	
	/**
	 * 支持get post的nyy协定
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/nyy")
	@ResponseBody
	public String nyyDemo(HttpServletRequest request, HttpServletResponse response){
		String jsonStr = null;
		NyyBeanObject object = null;
		try{
			//获取jsonStr, 返回是json格式,例如 {"appId":"test","sign":"testsign","data":{"k1":"v1"}}
			jsonStr = NyyProtocolHelper.getNyyContent(request);
			//sha256哈希检验, 如不需要,可去掉
			NyyProtocolHelper.sha256HashSecurityCheck("test", jsonStr);
			//NyyBeanObject 为业务自己封装的pojo
			object = Json.strToObj(jsonStr, NyyBeanObject.class);
			//TODO 业务逻辑
		}catch(Exception e){
			LOG.error("exception e = ", e);
		}
		
		//返回的数据
		BizObject o = new BizObject(1, "resp", new Date(), true, 3.14, 500);
		String respData = Json.ObjToStr(o);
		//最终返回给client的json格式
		String respStr = NyyProtocolHelper.genRespJson(object.getAppId(), "test", respData);
		LOG.info("jsonStr = {}, respData = {}, respStr = {}", jsonStr, respData, respStr);
		return respStr;
	}
	

### 客户端示例(主要是使用NyyClient类)

	String uri = "http://localhost:8080/nyy-demo-web/nyy";
	//主要方法都集中在NyyClient类中
	NyyClient client = new NyyClient("999", "test");
	BizObject bo1 = new BizObject(1, "bo1", new Date(), true, 3.14, 500);
	BizObject bo2 = new BizObject(1, "bo2", new Date(), true, 3.14, 500);
	List<BizObject> list = new ArrayList<BizObject>();
	list.add(bo1);
	list.add(bo2);
	Data data = new Data();
	data.setItems("items in data");

	data.setList(list);
	String dataJson = Json.ObjToStr(data);
	String doGetWithNyyJsonResult = client.doGet(uri, dataJson, true);
	String doGetWithoutNyyJsonResult = client.doGet(uri, dataJson, false);
	Data d = client.parseDataFromRespJson(doGetWithoutNyyJsonResult, Data.class, true); 
	Data d1 = client.parseDataFromRespJson(doGetWithoutNyyJsonResult, Data.class, false); 
	System.out.println(d);
	System.out.println(d1);

### 示例的SVN地址 
	https://svn.yy.com/web/gh/cs-demo/trunk/nyy-demo
