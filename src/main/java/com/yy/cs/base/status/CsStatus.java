package com.yy.cs.base.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CsStatus{
	
	private StatusCode code = StatusCode.SUCCCESS;
	
	private String message;
	
	private Map<String, Object> additionInfo = new HashMap<String, Object>();
	
	private String name;
	
	private int failNumber = 0;		//失败数量

	/**
	 * 获取总状态数量，包过本身
	 * @return
	 */
	public int getTotalNumber(){
		if(subCsStatus == null){
			return 1;	
		}
		return subCsStatus.size() + 1;
	}
	
	private List<CsStatus> subCsStatus;
	
	public CsStatus(){}
	
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
	 * @return
	 */
	public Object getAdditionInfo(String key) {
		return additionInfo.get(key);
	}
	

	/**
	 * 添加一个info信息
	 * @param key
	 * @param value
	 */
	public void additionInfo(String key , Object value) {
		additionInfo.put(key, value);
	}

	/**
	 * 增加一个字节点
	 * @param csStatus
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
	 */
	public StatusCode getCode() {
		return code;
	}
	
	/**
	 * 设置状态code
	 * @param code
	 */
	public void setCode(StatusCode code) {
		this.code = code;
	}

	/**
	 * 获取状态信息
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * 设置状态信息
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 获取状态名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置当前状态的名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		
		/*StringBuilder str = new StringBuilder();
		str.append("[");
		str.append("code:").append(getCode()).append(", ");
		str.append("message:").append(getMessage()).append(", ");
		str.append("name:").append(getName()).append(", ");
		str.append("additionInfo:").append(additionInfo.toString()).append(", ");
		str.append("failNumber:").append(getFailNumber()).append(", ");
		str.append("subCsStatus:");
		if(getSubCsStatus() != null && getSubCsStatus().size() > 0 ){
			for(CsStatus cs: subCsStatus){
				str.append(cs.toString());
			}
		}
		str.append("]");*/
		return JacksonUtils.toJson(this);
	}
	
}
