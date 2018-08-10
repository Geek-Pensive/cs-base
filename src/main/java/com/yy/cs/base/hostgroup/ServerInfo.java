package com.yy.cs.base.hostgroup;

/**
 * @author Zhangtao3
 * @email zhanghao3@yy.com
 */
public class ServerInfo {
	private String flock_id;
	private Long area_id;
	private String buss;
	private String sysopResponsibleAdmin;
	private Long idc_id;
	private Integer status;
	private Integer isp;
	private String group_id;
	private Long roomId;
	private Integer vm_type;
	private String sysopResponsibleAdmin_dw;
	private Integer server_type;
	private String serviceName;
	private String responsibleAdmin_dw;
	private String ip;
	private Long city_id;
	private Long osId;
	private String roomName;
	private String serverGuid;
	private Long priGroupId;
	private Long room_id;
	private Long departId;
	private Long serverId;
	private Integer server_level;
	private Long tmpGroupId;
	private String responsibleAdmin;
	private Long time_stamp;

	public String getFlock_id() {
		return flock_id;
	}

	public void setFlock_id(String flock_id) {
		this.flock_id = flock_id;
	}

	public Long getArea_id() {
		return area_id;
	}

	public void setArea_id(Long area_id) {
		this.area_id = area_id;
	}

	public String getBuss() {
		return buss;
	}

	public void setBuss(String buss) {
		this.buss = buss;
	}

	public String getSysopResponsibleAdmin() {
		return sysopResponsibleAdmin;
	}

	public void setSysopResponsibleAdmin(String sysopResponsibleAdmin) {
		this.sysopResponsibleAdmin = sysopResponsibleAdmin;
	}

	public Long getIdc_id() {
		return idc_id;
	}

	public void setIdc_id(Long idc_id) {
		this.idc_id = idc_id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsp() {
		return isp;
	}

	public void setIsp(Integer isp) {
		this.isp = isp;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Integer getVm_type() {
		return vm_type;
	}

	public void setVm_type(Integer vm_type) {
		this.vm_type = vm_type;
	}

	public String getSysopResponsibleAdmin_dw() {
		return sysopResponsibleAdmin_dw;
	}

	public void setSysopResponsibleAdmin_dw(String sysopResponsibleAdmin_dw) {
		this.sysopResponsibleAdmin_dw = sysopResponsibleAdmin_dw;
	}

	public Integer getServer_type() {
		return server_type;
	}

	public void setServer_type(Integer server_type) {
		this.server_type = server_type;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getResponsibleAdmin_dw() {
		return responsibleAdmin_dw;
	}

	public void setResponsibleAdmin_dw(String responsibleAdmin_dw) {
		this.responsibleAdmin_dw = responsibleAdmin_dw;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getCity_id() {
		return city_id;
	}

	public void setCity_id(Long city_id) {
		this.city_id = city_id;
	}

	public Long getOsId() {
		return osId;
	}

	public void setOsId(Long osId) {
		this.osId = osId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getServerGuid() {
		return serverGuid;
	}

	public void setServerGuid(String serverGuid) {
		this.serverGuid = serverGuid;
	}

	public Long getPriGroupId() {
		return priGroupId;
	}

	public void setPriGroupId(Long priGroupId) {
		this.priGroupId = priGroupId;
	}

	public Long getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public Integer getServer_level() {
		return server_level;
	}

	public void setServer_level(Integer server_level) {
		this.server_level = server_level;
	}

	public Long getTmpGroupId() {
		return tmpGroupId;
	}

	public void setTmpGroupId(Long tmpGroupId) {
		this.tmpGroupId = tmpGroupId;
	}

	public String getResponsibleAdmin() {
		return responsibleAdmin;
	}

	public void setResponsibleAdmin(String responsibleAdmin) {
		this.responsibleAdmin = responsibleAdmin;
	}

	public Long getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(Long time_stamp) {
		this.time_stamp = time_stamp;
	}

	@Override
	public String toString() {
		return "ServerInfo [flock_id=" + flock_id + ", area_id=" + area_id + ", buss=" + buss
				+ ", sysopResponsibleAdmin=" + sysopResponsibleAdmin + ", idc_id=" + idc_id + ", status=" + status
				+ ", isp=" + isp + ", group_id=" + group_id + ", roomId=" + roomId + ", vm_type=" + vm_type
				+ ", sysopResponsibleAdmin_dw=" + sysopResponsibleAdmin_dw + ", server_type=" + server_type
				+ ", serviceName=" + serviceName + ", responsibleAdmin_dw=" + responsibleAdmin_dw + ", ip=" + ip
				+ ", city_id=" + city_id + ", osId=" + osId + ", roomName=" + roomName + ", serverGuid=" + serverGuid
				+ ", priGroupId=" + priGroupId + ", room_id=" + room_id + ", departId=" + departId + ", serverId="
				+ serverId + ", server_level=" + server_level + ", tmpGroupId=" + tmpGroupId + ", responsibleAdmin="
				+ responsibleAdmin + ", time_stamp=" + time_stamp + "]";
	}

}
