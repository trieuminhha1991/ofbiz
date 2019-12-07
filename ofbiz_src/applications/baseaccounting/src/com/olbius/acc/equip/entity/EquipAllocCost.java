package com.olbius.acc.equip.entity;

import com.olbius.acc.asset.entity.AllocCost;

public class EquipAllocCost extends AllocCost {
	//Variables
	private String equipmentId;
	private String equipmentName;
	public String getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	public String getEquipmentName() {
		return equipmentName;
	}
	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
}	
