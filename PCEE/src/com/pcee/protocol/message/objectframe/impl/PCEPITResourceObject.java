/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PCEPComputationFactory;

import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Reserved |      CPU      |      RAM      |      STORAGE      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                       Optional TLVs                         //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PCEPITResourceObject implements PCEPObjectFrame {

	private final String NAME = "IT Resource";

	private String reserved;
	private String cpu;
	private String ram;
	private String storage;

	private PCEPCommonObjectHeader objectHeader;

	private int reservedStartBit = PCEPConstantValues.IT_RESOURCE_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.IT_RESOURCE_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.IT_RESOURCE_OBJECT_RESERVED_LENGTH;

	private int cpuStartBit = PCEPConstantValues.IT_RESOURCE_OBJECT_CPU_START_BIT;
	private int cpuEndBit = PCEPConstantValues.IT_RESOURCE_OBJECT_CPU_END_BIT;
	private int cpuLength = PCEPConstantValues.IT_RESOURCE_OBJECT_CPU_LENGTH;

	private int ramStartBit = PCEPConstantValues.IT_RESOURCE_OBJECT_RAM_START_BIT;
	private int ramEndBit = PCEPConstantValues.IT_RESOURCE_OBJECT_RAM_END_BIT;
	private int ramLength = PCEPConstantValues.IT_RESOURCE_OBJECT_RAM_LENGTH;

	private int storageStartBit = PCEPConstantValues.IT_RESOURCE_OBJECT_STORAGE_START_BIT;
	private int storageEndBit = PCEPConstantValues.IT_RESOURCE_OBJECT_STORAGE_END_BIT;
	private int storageLength = PCEPConstantValues.IT_RESOURCE_OBJECT_STORAGE_LENGTH;

	public PCEPITResourceObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPITResourceObject(PCEPCommonObjectHeader objectHeader, int reserved, int cpu, int ram, int storage) {
		this.setObjectHeader(objectHeader);
		this.setReservedDecimalValue(reserved);
		this.setCpuDecimalValue(cpu);
		this.setRamDecimalValue(ram);
		this.setStorageDecimalValue(storage);
		this.updateHeaderLength();
	}

	private void updateHeaderLength() {
		int objectFrameByteLength = this.getObjectFrameByteLength();
		this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
	}

	/**
	 * Object
	 */
	public PCEPCommonObjectHeader getObjectHeader() {
		return objectHeader;
	}

	public void setObjectHeader(PCEPCommonObjectHeader objectHeader) {
		this.objectHeader = objectHeader;
	}

	public String getObjectBinaryString() {
		String binaryString = reserved + cpu + ram + storage;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String cpuBinaryString = binaryString.substring(cpuStartBit, cpuEndBit + 1);
		String ramBinaryString = binaryString.substring(ramStartBit, ramEndBit + 1);
		String storageBinaryString = binaryString.substring(storageStartBit, storageEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setCpuBinaryString(cpuBinaryString);
		this.setRamBinaryString(ramBinaryString);
		this.setStorageBinaryString(storageBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + cpu.length() + ram.length() + storage.length();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH;
		int objectFrameByteLength = (objectLength + headerLength) / 8;
		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	/**
	 * reserved
	 */
	public int getReservedDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(reserved);
		return decimalValue;
	}

	public String getReservedBinaryString() {
		return this.reserved;
	}

	public void setReservedDecimalValue(int decimalValue) {
		int binaryLength = reservedLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);
		this.reserved = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setReservedBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	/**
	 * cpu
	 */
	public int getCpuDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(cpu);
		return decimalValue;
	}

	public void setCpuDecimalValue(int decimalValue) {
		int binaryLength = cpuLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.cpu = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public String getCpuBinaryString() {
		return this.cpu;
	}

	public void setCpuBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, cpuLength);
		this.cpu = checkedBinaryString;
	}

	public void setCpuBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(cpu, startingBit, binaryString, cpuLength);
		this.cpu = checkedBinaryString;
	}

	/**
	 * ram
	 */
	public int getRamDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(ram);
		return decimalValue;
	}

	public void setRamDecimalValue(int decimalValue) {
		int binaryLength = ramLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);
		this.ram = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public String getRamBinaryString() {
		return this.ram;
	}

	public void setRamBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, ramLength);
		this.ram = checkedBinaryString;
	}

	public void setRamBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(ram, startingBit, binaryString, ramLength);
		this.ram = checkedBinaryString;
	}

	/**
	 * 
	 * storage
	 */
	public void setStorageBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, storageLength);
		this.storage = checkedBinaryString;
	}

	public void setStorageBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(storage, startingBit, binaryString, storageLength);
		this.storage = checkedBinaryString;
	}

	public void setStorageDecimalValue(int decimalValue) {
		int binaryLength = storageLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.storage = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public String getStorageBinaryString() {
		return this.storage;
	}

	public int getStorageDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(storage);
		return decimalValue;
	}

	@Override
	public String binaryInformation() {
		String reservedBinaryInfo = getReservedBinaryString();
		String cpuBinaryInfo = "'" + getCpuBinaryString();
		String ramBinaryInfo = "'" + getRamBinaryString();
		String storageBinaryInfo = "'" + getStorageBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reservedBinaryInfo + cpuBinaryInfo + ramBinaryInfo + storageBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	@Override
	public String contentInformation() {
		return "[" + NAME + "]";
	}

	public String toString() {
		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<PCEPITResourceObject : Reserved-" + getReservedDecimalValue() + ", CPU-" + getCpuDecimalValue() + ", RAM-" + getRamDecimalValue() + ", STORAGE-" + getStorageDecimalValue() + ">";
		return headerInfo + objectInfo;
	}

	public void log(String logString) {
		System.out.println("PCEPITResourceObject::: " + logString);
	}
}
