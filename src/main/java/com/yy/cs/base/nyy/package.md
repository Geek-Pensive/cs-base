# NYY通信协议框架

----
## 概要

> | **项**   | **详细**               |
> | ---      | ---                    |
> | 联系人   | 欧阳柱，温子恒         |
> | 状态     | 已经大量应用于各种系统 |

业务开发通常有大量的通信需求，协议不统一容易导致通信在协议形式，依赖技术，实现方式等方面都有较大的差异，重复的工作较多，不利于团队的积累。

这里尝试定义一种通用的通信协议，主要目的为简化通信协议的设计，减轻开发工作量，降低沟通的成本。理想的状态是，业务只需要约定业务数据与字段，说明使用”NYY协议，封装方式一，安全场景一”，即可以双方无依赖的进行开发。

----
## 代码支持

目前已经针对NYY的主要场景进行了代码封装（Java）：[NYY Framework][]

强烈建议使用它，可以极大的提高开发效率。


----
## 基本约定

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
	- 好处：十分通用的技术，各种语言均容易支持。
	<br/><br/>

1. JSON作数据封装
	- 说明：也可以使用HTTP的Query String替代JSON，Query String在HTTP GET请求中更易读，但对复杂数的封装能力不够
	- 好处一：十分通用的技术，Web前端的Javascript天然支持，其它语言也容易支持。
	- 好处二：客户端与服务器端无需严格的依赖，仅需有Key的约定即可。
	<br/><br/>

1. 提供通用的安全机制
	- 说明：下面”安全机制”一节会有详细说明
	- 好处：减轻业务在安全方面的工作量
	<br/><br/>

----
## 具体设计

### 设计思路

- 协议主要有三个字段：appId，sign，data先其中一种
- appId：字段用于标识请求的来源，一般由服务端分配
- sign：用于安全校验，详见”安全机制”一节
- data：用于业务数据，需要业务作具体设计（建议扁平的Key-Value形式）
- 请求有两种GET方式和一种POST方式，服务器兼容三种方式最好，部分支持也可以


### 协议设计

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


### 建议

1. 业务字段的设计中不建议使用"appId"这个key。因为如果与外层的"appId"值不一致的话，容易引起安全上的问题（身份伪造）。
   当然，data字段中为了开发或解析上的便利，可以再带上一份一样的"appId"（但它与外层的"appId"语义上要一致，而不是具体业务中的其它含义）
   后续的基础库可能会添加这方面的安全校验。

2. data字段中均为业务字段，可以自由定义，这里尝试建议如下一些常用的key，以期减小沟通成本：
   - statusCode：响应中的状态/结果码，String，业务自定义内容，如：123, APP_NOT_EXIST
   - statusMsg：响应中的状态/结果信息，String，针对statusCode给出的详细信息

----
## 安全场景

这里的安全校验，是协议层面的安全机制。与其它层面的安全机制（如应用级的白名单，机器级的iptables等）并不冲突，而且往往可以形成互补，提高系统整体的安全性。

下面列举出几种主要的安全场景供选择，对安全要求一般的，可以不看后面的场景以节省脑细胞。

### 安全场景一：无需安全检查

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s1.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s1.jpg" alt= "NYY安全场景一" style="margin: 0 auto; width: 500px;"/></a>
<br/>

在最简单的场景下，业务可能不需要任务安全检查。此时把appId和sign留空即可。
但仍旧建议保留appId和sign在设计中，一是为将来预留，二是通用的代码可以统一支持

| **数据项**      | **示例**                                                       |
| ----            | ----                                                           |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码 |
| 事先约定的Key   | （不需要）                                                     |
| 生成哈希的源串  | （不需要）                                                     |
| 消息中appId的值 | （为空即可）                                                   |
| 消息中sign的值  | （为空即可）                                                   |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`                               |

### 安全场景二：仅校验业务数据（服务器间通信）

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s2.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s2.jpg" alt="NYY安全场景二" style="margin: 0 auto; width: 500px;"/></a>
<br/>

在一些简单的服务器间通信过程中，仅需要检验数据的完整性，并不需要作更高的安全要求。或者更高的安全需求已经通过其它机制完成。
此时，服务端可以给每个不同的客户端（appId）分配一个唯一的key。通信中对data进行如下的哈希操作（哈希算法使用SHA256）：
*注意*：校验sign时，需要使用原原本本的data json串，即在网络传输中的data json串。而不能是已经解析成对象后再次生成的Json串（存在乱序导致校验不成功的风险）

| **数据项**      | **示例**                                                                                                   |
| ----            | ----                                                                                                       |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                             |
| 事先约定的Key   | `ljfadjaf023ur32lj`                                                                                        |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}&key=ljfadjaf023ur32lj` <br>注意这里是字符串而不是对象，应使用UTF-8编码 |
| 消息中appId的值 | （事先分配给对应业务的id）                                                                                 |
| 消息中sign的值  | `SHA256(data={"chId":"Zfb","payer":"小王"}&key=ljfadjaf023ur32lj)`                                         |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`                                    |
	
### 安全场景三：仅校验用户身份（客户端与服务器端通信）

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s3.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s3.jpg" alt="NYY安全场景三" style="margin: 0 auto; width: 500px;"/></a>
<br/>

在客户端与服务器间通信的过程中，因为代码运行于用户端，需要通过UDB的安全Token来进行校验才可以实现。
在一些业务中，需要校验用户的身份信息，但业务字段（data）中的信息并不重要。
此时，客户端只需要把UDB的Token放在sign中即可。好处是直接利用UDB的Token即可明确用户身份。
//TODO：cookie
*注意*：这种场景中，业务数据（data）中的内容存在被篡改的可能，业务需要明确此风险。

| **数据项**      | **示例**                                                       |
| ----            | ----                                                           |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码 |
| 事先约定的Key   | （利用UDB的安全机制，无需事先确定key）                         |
| 生成哈希的源串  | 无                                                             |
| 消息中appId的值 | （事先分配给对应业务的id）                                     |
| 消息中sign的值  | UDB的Token                                                     |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`                                |
	
### 安全场景四：同时校验用户身份和业务数据（客户端与服务器端通信）

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s4.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s4.jpg" alt="NYY安全场景四" style="margin: 0 auto; width: 500px;"/></a>
<br/>

类似安全场景三，很多业务不仅需要确认用户的身份，还需要确认业务数据的完整性。此时需要借助UDB的另一种带附加信息的安全Token才可以实现。

简述一下背后原理：客户端生成UDB Token时，这个UDB接口允许带入一个字符串，而服务器端利用UDB接口验证这个Token时，接口会返回生成Token时所带入的字符串。
协议可以利用这个字符串来传输data的哈希值（哈希算法使用SHA256，但不再附加key），以确认数据的完整性。之所以采用data的哈希值而不使用data本身，是为了避免生成的Token过大。
这种机制需要依赖公司UDB的相关验证接口，UDB对已有一定的支持（Javascript前端暂时好像没支持）。目前在一些重要的业务过程中已经成功使用了这种机制，欢迎联系咨询。
*注意*：校验sign时，需要使用原原本本的data json串，即在网络传输中的data json串。而不能是已经解析成对象后再次生成的Json串（存在乱序导致校验不成功的风险）

| **数据项**      | **示例**                                                                                                        |
| ----            | ----                                                                                                            |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                                  |
| 事先约定的Key   | （利用UDB的安全机制，无需事先确定key）                                                                          |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}` <br>注意这里是字符串而不是对象，应使用UTF-8编码                            |
| 消息中appId的值 | （为空即可）                                                                                                    |
| 消息中sign的值  | `gen_udb_token(  SHA256(data={"chId":"Zfb","payer":"小王"})  )` <br>使用UDB提供的库，把上面”源串”的哈希值也带入 |
| 消息中data的值  | `{"chId":"Zfb","payer":"小王"}`<br/>在HTTP请求中**需要**进行URL Encode                                          |

### 安全场景五：加密业务数据（服务器间通信）

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s5.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s5.jpg" alt="NYY安全场景五" style="margin: 0 auto; width: 500px;"/></a>
<br/>

在一些安全性要求很高的场合，也可以对data进行加密，这样既除了保证数据的完整性，还可以加强数据的保密性。加密算法使用AES。
*注意*：也可以通过HTTPS的方式实现数据的保密性，但不能做到针对不同appId这样的粒度。

| **数据项**      | **示例**                                                                                                                        |
| ----            | ----                                                                                                                            |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}`  <br/>Json字符串，使用UTF-8编码                                                                 |
| 事先约定的Key   | `ljfadjaf023ur32lj`                                                                                                             |
| 用于加密的源串  | `{"chId":"Zfb","payer":"小王"}`<br/>注意这里是字符串而不是对象，应使用UTF-8编码                                                 |
| 消息中appId的值 | （事先分配给对应业务的id）                                                                                                      |
| 消息中sign的值  | （为空即可）                                                                                                                    |
| 消息中data的值  | `BASE64(  AES({"chId":"Zfb","payer":"小王"}, ljfadjaf023ur32lj)  )`<br/>注意：在HTTP请求中，生成的字节码需要转换成BASE64，**不需要**进行URL Encode |
	
### 安全场景六：加密业务数据（客户端与服务器端通信）

<br/>
<a target="_blank" href="/posts/standard/nyy/nyy_s6.jpg"><img class="img-responsive" src="/posts/standard/nyy/nyy_s6.jpg" alt="NYY安全场景六" style="margin: 0 auto; width: 500px;"/></a>
<br/>

在一些安全性要求很高的场合，也可以对data进行加密，这样既除了保证数据的完整性，还可以加强数据的保密性。加密算法使用AES。
在一些非服务器间通信的过程中，因为代码运行于用户端，分配appId和key的做法变得不安全。此时需要通过UDB的安全Token来进行校验才可以实现。

简述一下背后原理：客户端生成UDB Token时，UDB接口允许带入一个字符串，而服务器端利用UDB接口验证这个Token时，接口会返回生成Token时所带入的字符串。这里的字符串我们采用针对data加密的随机密钥。
这种机制需要依赖公司UDB的相关验证接口，UDB对已有一定的支持（Javascript前端暂时好像没支持）。

*注意*：也可以通过HTTPS的方式实现数据的保密性，但不能做到针对不同appId这样的粒度。

| **数据项**      | **示例**                                                                                                                                            |
| ----            | ----                                                                                                                                                |
| 业务数据        | `{"chId":"Zfb","payer":"小王"}` <br/>Json字符串，使用UTF-8编码                                                                                      |
| 事先约定的Key   | （客户端随机生成一个key即可以，无需事先约定） `58wfwY5w33o5w95we`                                                                                   |
| 生成哈希的源串  | `data={"chId":"Zfb","payer":"小王"}` <br>注意这里是字符串而不是对象，应使用UTF-8编码                                                                |
| 消息中appId的值 | （为空即可）                                                                                                                                        |
| 消息中sign的值  | `gen_udb_token(58wfwY5w33o5w95we)` <br>使用UDB提供的库，把上面的”源串”作为参数的一部分生成的Token                                                   |
| 消息中data的值  | `BASE64(  AES({"chId":"Zfb","payer":"小王"}, 58wfwY5w33o5w95we)  )`<br/>注意：在HTTP请求中，生成的字节码需要转换成BASE64，**不需要**进行URL Encode |


----
## 扩展一：文件上传

在一些业务场景中，需要上传文件。简单的做法是直接把文件编码后（例如Base64）放在data中作为一个字段来传输。

但这样做一方面会因为编码降低传输效率（例如Base64会使尺寸增大三分之一），另一方面没有较好的利用HTTP协议本身的multipart特性。

*注意*：此扩展已经在[NYY Framework][]中支持。

### 请求

通过标准的HTTP Multipart的方式来传输文件，即：

- 参数有appId, sign, data, files。
- 设置HTTP Header的"Content-Type"为"multipart/form-data"。
- files的参数filename为文件名，同一个请求中filename不能重复

### 请求示例

HTTP Header（略去其它无关Header）：

```
Content-Type: multipart/form-data; boundary=---------------------------7dc3aff105d0
```

HTTP Body：

```
-----------------------------7dc3aff105d0
Content-Disposition: form-data; name="appId"
Content-Type: text/plain

app01
-----------------------------7dc3aff105d0
Content-Disposition: form-data; name="sign"
Content-Type: text/plain

xxxx
-----------------------------7dc3aff105d0
Content-Disposition: form-data; name="data"
Content-Type: text/plain

{"k1":"v1"}
-----------------------------7dc3aff105d0
Content-Disposition: form-data; name="files"; filename="1.jpg"
Content-Type: image/jpeg

>> 文件1.jpg的二进制数据 <<
-----------------------------7dc3aff105d0
Content-Disposition: form-data; name="files"; filename="2.jpg"
Content-Type: image/jpeg

>> 文件2.jpg的二进制数据 <<
-----------------------------7dc3aff105d0--
```

### 响应

与普通的NYY响应一致。


----
## 扩展二：JSONP支持

在业务前后端交互时，常会使用到JSONP技术进行函数回调。对于这种情况，可以使用NYY扩展形式。
具体来说就是在请求中新增加一个"callback"字段来代标识需要返回的函数名，这样服务端应返回使用这个函数名的JSONP数据。前端即可直接使用。

*注意*：此扩展已经在[NYY Framework][]中支持。

### 请求

形式一：`{"appId":"XXX","sign":"XXX","callback":"funcname","data":{"k1":"v1"}}`

形式二：`appId=XXX&sign=XXX&callback=funcname&data={"k1":"v1"}`

其中"funcname"即为自定义的JSONP函数名。


### 响应

`funcname({"appId":"xx","sign":"xx","data":{"key1":"val1"}})`


----
## Tips

如果是Java语言，强烈推荐使用[NYY Framework][]。

以下是针对其它语言的一些小的Tips，或是无法使用[NYY Framework][]的情况。

### 注意data字段的"原始性"

因为data字段会被用于校验，所以它里面的字段顺序不应该随意变更。应当注意：

- 生成sign时，应先生成data字段的json串，这个串既用于生成sign，也用于实际的发送请求中。
- 校验sign时，应先从收到的消息中原样的提取出data字段的json串，用于生成校验用的sign。
- 不要把data对应的串转换成对象后再转换成字符串才用于校验


对于普通的NYY消息（非NYY扩展消息），可以使用简单的正则表达式来提取data字段。
*前提是appId和sign中不包含"{"或"}"符号。

```java
nyyStr.replaceAll("^\\s*\\{[^\\{]*|[^\\}]*\\}\\s*$", ""))
```

### Javascript代码支持

对于Javascript语言，前端同事已经编写了一些封装的接口，可以直接使用。
	
```javascript
<!-- 引入依赖 -->
<script type="text/javascript" 
        src="http://file.do.yy.com/group2/M00/34/71/tz0CVFM7dO6ATqGiAAFp2WUy4C84783.js"></script>
<script type="text/javascript" 
        src="http://file.do.yy.com/group2/M00/33/19/tz0CN1M7dMKACmXOAAAIy5koT3Y6953.js"></script>
<script type="text/javascript">
    function doGet() {
        nyyGet("/demo1", "1001", "abcdef", '{"key1":123,"key2":"thisIsStrValue中文"}',
            function(dataObj){alert(dataObj);},
            function(error){alert(error);}
        );	  
    };
    function doTextPlainPost() {
        nyyTextPlainPost("/demo2","1001","abcdef",'{"key1":123,"key2":"thisIsStrValue中文"}',
            function(dataObj){alert(dataObj);},
            function(error){alert(error);}
        );
    };
    function doFormPost() {
        nyyFormPost("/nyyDemo3", "1001", "abcdef", '{"key1":123,"key2":"thisIsStrValue中文"}',
            function(dataObj){alert(dataObj);},
            function(error){alert(error);}
        );
    }
</script>
</html>
```

	
### Android端支持代码

```java
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import org.json.JSONObject;
/**
 * 因yy android sdk已经提供http请求和回调的模块，故nyy对于android端的代码支持就不在httpclient基础上封装.
 * </br>该类提供了nyy协议中会使用到的sha256哈希算法、拼凑nyy协议的get 方式url等。
 * </br>由于nyy协议使用的数据格式是json格式，android开发也不希望引入太多jar包。所以在实际json解析和生成
 * 建议使用android基础库的org.json包
 */
public class NyyAndroidClientUtils {
    /**
     * sha256哈希,使用android基础库的security模块
     */
    public static String toSHA256String(String str) {
        String hash = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(str.getBytes("UTF-8"));
            hash = bytesToHexString(digest.digest());
        } catch (Exception e) {
            //TODO 加上你们业务的异常处理
            //YLog.error("toHexString", "toHexString", e);
        }
        return hash;
    }

    /**
     * byte数组转为hex字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 生成nyy get方式的请求url
     * @param sign   要验证的sign,如果没有,请使用""
     * @param appId  业务appId
     * @param requestUri   请求的uri,比如 http://www.gate.yy.com/abc
     * @param params  放入data的key-value参数
     */
    public static String genNyyGetUrl(String sign,String appId,String requestUri,Object... params){
        String url = requestUri + "?appId=" + appId + "&sign=" + sign;
        if (params.length > 0 && params.length % 2 == 0) {
            JSONObject data = new JSONObject();
            for (int i = 0; i < params.length; i = i + 2) {
                try {
                    String key = (String) params[i];
                    Object value = params[i + 1];
                    data.putOpt(key, value);
                } catch (Exception e) {
                    //TODO 加上你们业务的异常处理
                    //YLog.error(this, "getUrl error! %s ,params: %s", request, params, e);
                }
            }       
            try {
                url = url + "&data=" + URLEncoder.encode(data.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //TODO 加上你们业务的异常处理
                //YLog.error(this, "getUrl", e);
            }
        }
        return url;
    }

    /**
     * 根据nyy协议约定,生成sha256哈希的sign
     * @param data  没有经过urlEncode的data,为json格式
     * @param key   业务的key
     * @return    
     */
    public static String genSign(String data, String key){
        String str = "data=" + data + "&key=" + key;
        return toSHA256String(str);
    }
}
```

[示例的SVN地址]: https://svn.yy.com/web/gh/apachecommons
[NYY Framework]: ?post=posts/library/nyy-framework/nyy-framework.md
