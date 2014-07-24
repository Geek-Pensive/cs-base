package com.yy.cs.base.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yy.cs.base.json.Json;

/**
 * 返回状态对象
 * @author duowan-PC
 *
 */
public class CsStatus{
	
	private StatusCode code = StatusCode.SUCCCESS;
	
	private String message;
	
	private Map<String, Object> additionInfo = new HashMap<String, Object>();
	
	private String name;
	
	private int failNumber = 0;		//失败数量

	/**
	 * 获取总状态数量，包括本身
	 * @return
	 *   int 总状态数量
	 */
	public int getTotalNumber(){
		if(subCsStatus == null){
			return 1;	
		}
		return subCsStatus.size() + 1;
	}
	
	private List<CsStatus> subCsStatus;
	
	public CsStatus(){}
	/**
	 * 构造器
	 * @param code 
	 * 		请求状态
	 * @param message
	 * 		请求信息
	 */
	public CsStatus(StatusCode code,String message){
		this.code = code;
		this.message = message;
	}
	
	public CsStatus(String name){
		this.name = name;
	}
	
	public CsStatus(StatusCode code,String message,String name){
		this.code = code;
		this.message = message;
		this.name = name;
	}
	
	
	public Map<String,Object> getAdditionInfo(){
		return new HashMap<String, Object>(additionInfo);
	}
	/**
	 * 获取失败状态数量，包过本身
	 * @return
	 * 		int 失败状态数量
	 */
	public int getFailNumber(){
		if(code == StatusCode.FAIL){
			failNumber += 1;
		}
		return failNumber;
	}
	
	
	/**
	 * 获取某一个info信息
	 * @param key
	 * 		key值
	 * @return
	 * 		Object 
	 */
	public Object getAdditionInfo(String key) {
		return additionInfo.get(key);
	}
	

	/**
	 * 添加一个info信息
	 * @param key
	 * 		key值
	 * @param value
	 * 		value值
	 */
	public void additionInfo(String key , Object value) {
		additionInfo.put(key, value);
	}

	/**
	 * 增加一个字节点
	 * @param csStatus
	 * 		返回状态对象
	 */
	public void addSubCsStatus(CsStatus csStatus) {
		if( this.subCsStatus ==  null){
			this.subCsStatus = new ArrayList<CsStatus>();
		}
		this.subCsStatus.add(csStatus);
		if(csStatus.getCode() == StatusCode.FAIL){
			failNumber += 1;
		}
	}
	
	/**
	 * 获取所有的子节点
	 * @return
	 * 		持有CsStatus的List,代表的是所有的子节点
	 */
	public List<CsStatus> getSubCsStatus() {
		if(subCsStatus == null){
			return null;
		}
		return new ArrayList<CsStatus>(subCsStatus);
	}
	
	/**
	 * 设置所有的子节点
	 * @param csStatus
	 * 		返回状态对象list集合
	 */
	public void setSubCsStatus(List<CsStatus> csStatus) {
		this.subCsStatus = csStatus;
		int f = 0;
		if(csStatus != null){
			for(CsStatus s : csStatus){
				if(s.getCode() == StatusCode.FAIL){
					f += 1;
				}
			}
		}
		failNumber = f;
	}
	
	/**
	 * 取得状态code
	 * @return
	 * 		状态码，SUCCESS 、FAIL、WRONG 之一
	 */
	public StatusCode getCode() {
		return code;
	}
	
	/**
	 * 设置状态code
	 * @param code
	 * 		状态 SUCCESS 、FAIL、WRONG 之一
	 */
	public void setCode(StatusCode code) {
		this.code = code;
	}

	/**
	 * 获取状态信息
	 * @return
	 * 	 message,消息
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * 设置状态信息
	 * @param message
	 * 		状态信息
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 获取状态名称
	 * @return
	 * 		name 返回状态名称
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置当前状态的名称
	 * @param name
	 * 		状态名称name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return Json.ObjToStr(this);
	}
	
}
