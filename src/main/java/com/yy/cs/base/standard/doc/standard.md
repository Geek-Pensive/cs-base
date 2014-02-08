1.行长度：
*不应超过80个字符，公司大号显示器22寸每行约200个字符
2.换行：
*在逗号后断开
*在操作符前断开
*选择高级别的断开，而非较低级别的断开
*新的一行应该与上一行同一级别表达式的开头处对齐
*如果上述规则导致你的代码混乱，那就代之以缩进8个空格
例子：longExpression4与longExpression1对齐；someMethod2与longExpression1对齐，
longExpression3与longExpression2对齐：

someMethod(longExpression1, longExpression2, longExpression3,
           longExpression4, longExpression5);
var = someMethod1(longExpression1,
                  someMethod2(longExpression2,
                              longExpression3));
							  
							  
							  


2.申明建议：
一行一个申明，这样以利于写注释。如：
int level;
int size;
优于，
int level, size;
切勿将不同类型变量在同一行定义，例如
int foo, fooarray[];

3.语句：
* 简单语句：每行至多包含一条语句，例如
argv++; //正确
argc--; //正确
argv++; argc--;//不建议

*返回语句：
返回值不建议有小括号，除非是为了让返回值更明显，例如
return;
return myDisk.size();
return (size ? size : defaultSize);

*if else语句：
if (condition) {
    statements;
}
 
if (condition) {
    statements;
} else {
    statements;
}
 
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
if语句建议与"{"和"}"括起来，避免使用如下格式引起错误：
if(condiction) //不建议这种方式
    statements;

4. 命名规范
*包名	通常是com,edu,gov 等。公司的包名为 com.yy
*类	    类名是一个名词，采用大小写混合的方式，每个单词的首字母大写。例如 class Raster
*接口   命名规则与类名相似
*方法   方法名是一个动词，采用大小写混合的方式，第一个单词的首字母小写，其后单词的首字母大写。
*变量   采用大小写混合的方式，第一个单词的首字母小写，其后单词的首字母大写。
		临时变量通常被取名为i,j,k,m 和 n, 它们一般用于整型; c,d,e, 它们一般用于字符型。
*常量	类常量和基本类型常量的声明,应该全部大写,单词间用下划线隔开。比如 MAX_WIDTH = 999;

5. 编程实践
* 实例和类变量的访问控制
不建议把实例或类变量声明为公有，除非是静态变量，仅仅是作为数据结构使用的。例如

* 引用类变量和类方法
避免用一个对象访问一个类的静态变量和方法，应该使用类名替代。例如：
classMethod();
AClass.classMethod();
anObject.classMethod();//避免这种方式

* 变量赋值
避免在一个语句中给多个变量赋相同的值，比如
a = b = 3;//避免这种方式

* 不要忽略异常
避免完全忽视异常，比如
void setServerPort(String value){
	try{
		serverPort = Integer.parseInt(value);
	}catch(NumberFormatException e){//避免这种完全忽略异常的编码风格
	}
}
你可以将异常继续抛出
void setServerPort(String value) throws NumberFormatException {
    serverPort = Integer.parseInt(value);
}
或者自己封装异常或者记录日志等。
void setServerPort(String value) throws ConfigurationException {
    try {
        serverPort = Integer.parseInt(value);
    } catch (NumberFormatException e) {
        throw new ConfigurationException("Port " + value + " is not valid.");
		//或者记录Log
    }
}
		
		
		

		


	
