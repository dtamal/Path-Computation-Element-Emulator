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

package com.pcee.protocol.message;

public class PCEPConstantValues {

	//FIXME
//	public static final short CONNECT_TIMER = 60;
//	public static final short KEEPALIVE_TIMER = 30;
//	public static final short DEAD_TIMER = KEEPALIVE_TIMER * 4;
//	public static final short OPENWAIT_TIMER = 60;
//	public static final short KEEPWAIT_TIMER = 60;
	
	//
//	public static final short CONNECT_TIMER = 1500;
//	public static final short KEEPALIVE_TIMER = 1500;
//	public static final short DEAD_TIMER = KEEPALIVE_TIMER * 4;
//	public static final short OPENWAIT_TIMER = 1500;
//	public static final short KEEPWAIT_TIMER = 1500;
	
	// Testing Values
	public static final short CONNECT_TIMER = 300;
	public static final short KEEPALIVE_TIMER = 60;
	public static final short DEAD_TIMER = KEEPALIVE_TIMER * 4;
//	public static final short DEAD_TIMER = 12000;
	public static final short OPENWAIT_TIMER = 100;
	public static final short KEEPWAIT_TIMER = 100;

	// Common Message Header
	public static final short COMMON_MESSAGE_HEADER_LENGTH = 32;

	public static final short COMMON_MESSAGE_HEADER_VERSION_LENGTH = 3;
	public static final short COMMON_MESSAGE_HEADER_VERSION_START_BIT = 0;
	public static final short COMMON_MESSAGE_HEADER_VERSION_END_BIT = 2;

	public static final short COMMON_MESSAGE_HEADER_FLAGS_LENGTH = 5;
	public static final short COMMON_MESSAGE_HEADER_FLAGS_START_BIT = 3;
	public static final short COMMON_MESSAGE_HEADER_FLAGS_END_BIT = 7;

	public static final short COMMON_MESSAGE_HEADER_TYPE_LENGTH = 8;
	public static final short COMMON_MESSAGE_HEADER_TYPE_START_BIT = 8;
	public static final short COMMON_MESSAGE_HEADER_TYPE_END_BIT = 15;

	public static final short COMMON_MESSAGE_HEADER_LENGTH_LENGTH = 16;
	public static final short COMMON_MESSAGE_HEADER_LENGTH_START_BIT = 16;
	public static final short COMMON_MESSAGE_HEADER_LENGTH_END_BIT = 31;

	// Common Object Header
	public static final short COMMON_OBJECT_HEADER_LENGTH = 32;

	public static final short COMMON_OBJECT_HEADER_CLASS_LENGTH = 8;
	public static final short COMMON_OBJECT_HEADER_CLASS_START_BIT = 0;
	public static final short COMMON_OBJECT_HEADER_CLASS_END_BIT = 7;

	public static final short COMMON_OBJECT_HEADER_TYPE_LENGTH = 4;
	public static final short COMMON_OBJECT_HEADER_TYPE_START_BIT = 8;
	public static final short COMMON_OBJECT_HEADER_TYPE_END_BIT = 11;

	public static final short COMMON_OBJECT_HEADER_RESERVED_LENGTH = 2;
	public static final short COMMON_OBJECT_HEADER_RESERVED_START_BIT = 12;
	public static final short COMMON_OBJECT_HEADER_RESERVED_END_BIT = 13;

	public static final short COMMON_OBJECT_HEADER_FLAGS_LENGTH = 2;
	public static final short COMMON_OBJECT_HEADER_FLAGS_START_BIT = 14;
	public static final short COMMON_OBJECT_HEADER_FLAGS_END_BIT = 15;

	public static final short COMMON_OBJECT_HEADER_FLAG_PROCESSED_LENGTH = 1;
	public static final short COMMON_OBJECT_HEADER_FLAG_PROCESSED_START_BIT = 14;
	public static final short COMMON_OBJECT_HEADER_FLAG_PROCESSED_END_BIT = 14;

	public static final short COMMON_OBJECT_HEADER_FLAG_IGNORED_LENGTH = 1;
	public static final short COMMON_OBJECT_HEADER_FLAG_IGNORED_START_BIT = 15;
	public static final short COMMON_OBJECT_HEADER_FLAG_IGNORED_END_BIT = 15;

	public static final short COMMON_OBJECT_HEADER_LENGTH_LENGTH = 16;
	public static final short COMMON_OBJECT_HEADER_LENGTH_START_BIT = 16;
	public static final short COMMON_OBJECT_HEADER_LENGTH_END_BIT = 31;
	
	//IT Resource Object
	public static final short IT_RESOURCE_OBJECT_LENGTH = 32;
	
	public static final short IT_RESOURCE_OBJECT_RESERVED_LENGTH = 6;
	public static final short IT_RESOURCE_OBJECT_RESERVED_START_BIT = 0;
	public static final short IT_RESOURCE_OBJECT_RESERVED_END_BIT = 5;
	
	public static final short IT_RESOURCE_OBJECT_CPU_LENGTH = 8;
	public static final short IT_RESOURCE_OBJECT_CPU_START_BIT = 6;
	public static final short IT_RESOURCE_OBJECT_CPU_END_BIT = 13;
	
	public static final short IT_RESOURCE_OBJECT_RAM_LENGTH = 8;
	public static final short IT_RESOURCE_OBJECT_RAM_START_BIT = 14;
	public static final short IT_RESOURCE_OBJECT_RAM_END_BIT = 21;
	
	public static final short IT_RESOURCE_OBJECT_STORAGE_LENGTH = 10;
	public static final short IT_RESOURCE_OBJECT_STORAGE_START_BIT = 22;
	public static final short IT_RESOURCE_OBJECT_STORAGE_END_BIT = 31;
	

	//PCEPTNASourceObject
	public static final short TNA_SOURCE_OBJECT_TYPE_START_BIT = 0;
	public static final short TNA_SOURCE_OBJECT_TYPE_END_BIT = 15;
	public static final short TNA_SOURCE_OBJECT_TYPE_LENGTH = 16;
	
	public static final short TNA_SOURCE_OBJECT_LENGTH_START_BIT = 16;
	public static final short TNA_SOURCE_OBJECT_LENGTH_END_BIT = 31;
	public static final short TNA_SOURCE_OBJECT_LENGTH_LENGTH = 16;
	
	public static final short TNA_SOURCE_OBJECT_ADDRESS_LENGTH_START_BIT = 32;
	public static final short TNA_SOURCE_OBJECT_ADDRESS_LENGTH_END_BIT = 39;
	public static final short TNA_SOURCE_OBJECT_ADDRESS_LENGTH_LENGTH = 8;
	
	public static final short TNA_SOURCE_OBJECT_RESERVED_START_BIT = 40;
	public static final short TNA_SOURCE_OBJECT_RESERVED_END_BIT = 63;
	public static final short TNA_SOURCE_OBJECT_RESERVED_LENGTH = 24;
	
	public static final short TNA_SOURCE_OBJECT_SRC_IP_START_BIT = 64;
	public static final short TNA_SOURCE_OBJECT_SRC_IP_END_BIT = 95;
	public static final short TNA_SOURCE_OBJECT_SRC_IP_LENGTH = 32;
	
	//PCEPTNADestinationObject
	public static final short TNA_DESTINATION_OBJECT_TYPE_START_BIT = 0;
	public static final short TNA_DESTINATION_OBJECT_TYPE_END_BIT = 15;
	public static final short TNA_DESTINATION_OBJECT_TYPE_LENGTH = 16;
	
	public static final short TNA_DESTINATION_OBJECT_LENGTH_START_BIT = 16;
	public static final short TNA_DESTINATION_OBJECT_LENGTH_END_BIT = 31;
	public static final short TNA_DESTINATION_OBJECT_LENGTH_LENGTH = 16;
	
	public static final short TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_START_BIT = 32;
	public static final short TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_END_BIT = 39;
	public static final short TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_LENGTH = 8;
	
	public static final short TNA_DESTINATION_OBJECT_RESERVED_START_BIT = 40;
	public static final short TNA_DESTINATION_OBJECT_RESERVED_END_BIT = 63;
	public static final short TNA_DESTINATION_OBJECT_RESERVED_LENGTH = 24;
	
	public static final short TNA_DESTINATION_OBJECT_DEST_IP_START_BIT = 64;
	public static final short TNA_DESTINATION_OBJECT_DEST_IP_END_BIT = 95;
	public static final short TNA_DESTINATION_OBJECT_DEST_IP_LENGTH = 32;
	
	//PCEPGeneralizedEndPointsTNAObject
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_START_BIT = 0;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_END_BIT = 23;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_LENGTH = 24;
	
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_START_BIT = 24;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_END_BIT = 31;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_LENGTH = 8;
	
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_START_BIT = 32;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_END_BIT = 127;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_LENGTH = 96;
	
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_START_BIT = 128;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_END_BIT = 223;
	public static final short GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_LENGTH = 96;
	
	
	
	
	// Open Object
	public static final short OPEN_OBJECT_LENGTH = 32;

	public static final short OPEN_OBJECT_VERSION_LENGTH = 3;
	public static final short OPEN_OBJECT_VERSION_START_BIT = 0;
	public static final short OPEN_OBJECT_VERSION_END_BIT = 2;

	public static final short OPEN_OBJECT_FLAGS_LENGTH = 5;
	public static final short OPEN_OBJECT_FLAGS_START_BIT = 3;
	public static final short OPEN_OBJECT_FLAGS_END_BIT = 7;

	public static final short OPEN_OBJECT_KEEPALIVE_LENGTH = 8;
	public static final short OPEN_OBJECT_KEEPALIVE_START_BIT = 8;
	public static final short OPEN_OBJECT_KEEPALIVE_END_BIT = 15;

	public static final short OPEN_OBJECT_DEADTIMER_LENGTH = 8;
	public static final short OPEN_OBJECT_DEADTIMER_START_BIT = 16;
	public static final short OPEN_OBJECT_DEADTIMER_END_BIT = 23;

	public static final short OPEN_OBJECT_SESSIONID_LENGTH = 8;
	public static final short OPEN_OBJECT_SESSIONID_START_BIT = 24;
	public static final short OPEN_OBJECT_SESSIONID_END_BIT = 31;

	// ERROR Object
	public static final int ERROR_OBJECT_LENGTH = 32;

	public static final int ERROR_OBJECT_RESERVED_LENGTH = 8;
	public static final int ERROR_OBJECT_RESERVED_START_BIT = 0;
	public static final int ERROR_OBJECT_RESERVED_END_BIT = 7;

	public static final int ERROR_OBJECT_FLAGS_LENGTH = 8;
	public static final int ERROR_OBJECT_FLAGS_START_BIT = 8;
	public static final int ERROR_OBJECT_FLAGS_END_BIT = 15;

	public static final int ERROR_OBJECT_TYPE_LENGTH = 8;
	public static final int ERROR_OBJECT_TYPE_START_BIT = 16;
	public static final int ERROR_OBJECT_TYPE_END_BIT = 23;

	public static final int ERROR_OBJECT_VALUE_LENGTH = 8;
	public static final int ERROR_OBJECT_VALUE_START_BIT = 24;
	public static final int ERROR_OBJECT_VALUE_END_BIT = 31;

	// CLOSE Object
	public static final int CLOSE_OBJECT_LENGTH = 32;

	public static final int CLOSE_OBJECT_RESERVED_LENGTH = 16;
	public static final int CLOSE_OBJECT_RESERVED_START_BIT = 0;
	public static final int CLOSE_OBJECT_RESERVED_END_BIT = 15;

	public static final int CLOSE_OBJECT_FLAGS_LENGTH = 8;
	public static final int CLOSE_OBJECT_FLAGS_START_BIT = 16;
	public static final int CLOSE_OBJECT_FLAGS_END_BIT = 23;

	public static final int CLOSE_OBJECT_REASON_LENGTH = 8;
	public static final int CLOSE_OBJECT_REASON_START_BIT = 24;
	public static final int CLOSE_OBJECT_REASON_END_BIT = 31;

	public static final short REQUEST_PARAMETERS_OBJECT_LENGTH = 64;

	public static final short REQUEST_PARAMETERS_OBJECT_FLAGS_LENGTH = 32;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAGS_START_BIT = 0;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAGS_END_BIT = 31;

	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_O_LENGTH = 1;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_O_START_BIT = 26;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_O_END_BIT = 26;

	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_B_LENGTH = 1;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_B_START_BIT = 27;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_B_END_BIT = 27;

	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_R_LENGTH = 1;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_R_START_BIT = 28;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_R_END_BIT = 28;

	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_PRI_LENGTH = 1;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_PRI_START_BIT = 29;
	public static final short REQUEST_PARAMETERS_OBJECT_FLAG_PRI_END_BIT = 31;

	public static final short REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_LENGTH = 32;
	public static final short REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_START_BIT = 32;
	public static final short REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_END_BIT = 63;

	public static final short NO_PATH_OBJECT_LENGTH = 32;

	public static final short NO_PATH_OBJECT_NATURE_OF_ISSUE_LENGTH = 8;
	public static final short NO_PATH_OBJECT_NATURE_OF_ISSUE_START_BIT = 0;
	public static final short NO_PATH_OBJECT_NATURE_OF_ISSUE_END_BIT = 7;

	public static final short NO_PATH_OBJECT_FLAGS_LENGTH = 16;
	public static final short NO_PATH_OBJECT_FLAGS_START_BIT = 8;
	public static final short NO_PATH_OBJECT_FLAGS_END_BIT = 23;

	public static final short NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_LENGTH = 1;
	public static final short NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_START_BIT = 8;
	public static final short NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_END_BIT = 8;

	public static final short NO_PATH_OBJECT_RESERVED_LENGTH = 8;
	public static final short NO_PATH_OBJECT_RESERVED_START_BIT = 24;
	public static final short NO_PATH_OBJECT_RESERVED_END_BIT = 31;

	public static final short END_POINTS_OBJECT_LENGTH = 64;

	public static final short END_POINTS_OBJECT_SOURCE_ADDRESS_LENGTH = 32;
	public static final short END_POINTS_OBJECT_SOURCE_ADDRESS_START_BIT = 0;
	public static final short END_POINTS_OBJECT_SOURCE_ADDRESS_END_BIT = 31;

	public static final short END_POINTS_OBJECT_DESTINATION_ADDRESS_LENGTH = 32;
	public static final short END_POINTS_OBJECT_DESTINATION_ADDRESS_START_BIT = 32;
	public static final short END_POINTS_OBJECT_DESTINATION_ADDRESS_END_BIT = 63;

	public static final short BANDWIDTH_OBJECT_LENGTH = 32;

	public static final short BANDWIDTH_OBJECT_BANDWIDTH_LENGTH = 32;
	public static final short BANDWIDTH_OBJECT_BANDWIDTH_START_BIT = 0;
	public static final short BANDWIDTH_OBJECT_BANDWIDTH_END_BIT = 31;

	public static final short METRIC_OBJECT_LENGTH = 64;

	public static final short METRIC_OBJECT_RESERVED_LENGTH = 16;
	public static final short METRIC_OBJECT_RESERVED_START_BIT = 0;
	public static final short METRIC_OBJECT_RESERVED_END_BIT = 15;

	public static final short METRIC_OBJECT_FLAGS_LENGTH = 8;
	public static final short METRIC_OBJECT_FLAGS_START_BIT = 16;
	public static final short METRIC_OBJECT_FLAGS_END_BIT = 23;

	public static final short METRIC_OBJECT_FLAG_C_LENGTH = 1;
	public static final short METRIC_OBJECT_FLAG_C_START_BIT = 22;
	public static final short METRIC_OBJECT_FLAG_C_END_BIT = 22;

	public static final short METRIC_OBJECT_FLAG_B_LENGTH = 1;
	public static final short METRIC_OBJECT_FLAG_B_START_BIT = 23;
	public static final short METRIC_OBJECT_FLAG_B_END_BIT = 23;

	public static final short METRIC_OBJECT_TYPE_LENGTH = 8;
	public static final short METRIC_OBJECT_TYPE_START_BIT = 24;
	public static final short METRIC_OBJECT_TYPE_END_BIT = 31;

	public static final short METRIC_OBJECT_METRIC_VALUE_LENGTH = 32;
	public static final short METRIC_OBJECT_METRIC_VALUE_START_BIT = 32;
	public static final short METRIC_OBJECT_METRIC_VALUE_END_BIT = 63;

	public static final short LSPA_OBJECT_LENGTH = 128;

	public static final short LSPA_OBJECT_EXCLUDE_ANY_LENGTH = 32;
	public static final short LSPA_OBJECT_EXCLUDE_ANY_START_BIT = 0;
	public static final short LSPA_OBJECT_EXCLUDE_ANY_END_BIT = 31;

	public static final short LSPA_OBJECT_INCLUDE_ANY_LENGTH = 32;
	public static final short LSPA_OBJECT_INCLUDE_ANY_START_BIT = 32;
	public static final short LSPA_OBJECT_INCLUDE_ANY_END_BIT = 63;

	public static final short LSPA_OBJECT_INCLUDE_ALL_LENGTH = 32;
	public static final short LSPA_OBJECT_INCLUDE_ALL_START_BIT = 64;
	public static final short LSPA_OBJECT_INCLUDE_ALL_END_BIT = 95;

	public static final short LSPA_OBJECT_SETUP_PRIO_LENGTH = 8;
	public static final short LSPA_OBJECT_SETUP_PRIO_START_BIT = 96;
	public static final short LSPA_OBJECT_SETUP_PRIO_END_BIT = 103;

	public static final short LSPA_OBJECT_HOLDING_PRIO_LENGTH = 8;
	public static final short LSPA_OBJECT_HOLDING_PRIO_START_BIT = 104;
	public static final short LSPA_OBJECT_HOLDING_PRIO_END_BIT = 111;

	public static final short LSPA_OBJECT_FLAGS_LENGTH = 8;
	public static final short LSPA_OBJECT_FLAGS_START_BIT = 112;
	public static final short LSPA_OBJECT_FLAGS_END_BIT = 119;

	public static final short LSPA_OBJECT_FLAG_L_LENGTH = 1;
	public static final short LSPA_OBJECT_FLAG_L_START_BIT = 119;
	public static final short LSPA_OBJECT_FLAG_L_END_BIT = 119;

	public static final short LSPA_OBJECT_RESERVED_LENGTH = 8;
	public static final short LSPA_OBJECT_RESERVED_START_BIT = 120;
	public static final short LSPA_OBJECT_RESERVED_END_BIT = 127;

	public static final short NOTIFICATION_OBJECT_LENGTH = 32;

	public static final short NOTIFICATION_OBJECT_RESERVED_LENGTH = 8;
	public static final short NOTIFICATION_OBJECT_RESERVED_START_BIT = 0;
	public static final short NOTIFICATION_OBJECT_RESERVED_END_BIT = 7;

	public static final short NOTIFICATION_OBJECT_FLAGS_LENGTH = 8;
	public static final short NOTIFICATION_OBJECT_FLAGS_START_BIT = 8;
	public static final short NOTIFICATION_OBJECT_FLAGS_END_BIT = 15;

	public static final short NOTIFICATION_OBJECT_NOTIFICATION_TYPE_LENGTH = 8;
	public static final short NOTIFICATION_OBJECT_NOTIFICATION_TYPE_START_BIT = 16;
	public static final short NOTIFICATION_OBJECT_NOTIFICATION_TYPE_END_BIT = 23;

	public static final short NOTIFICATION_OBJECT_NOTIFICATION_VALUE_LENGTH = 8;
	public static final short NOTIFICATION_OBJECT_NOTIFICATION_VALUE_START_BIT = 24;
	public static final short NOTIFICATION_OBJECT_NOTIFICATION_VALUE_END_BIT = 31;

	public static final short LOAD_BALANCING_OBJECT_LENGTH = 64;

	public static final short LOAD_BALANCING_OBJECT_RESERVED_LENGTH = 16;
	public static final short LOAD_BALANCING_OBJECT_RESERVED_START_BIT = 0;
	public static final short LOAD_BALANCING_OBJECT_RESERVED_END_BIT = 15;

	public static final short LOAD_BALANCING_OBJECT_FLAGS_LENGTH = 8;
	public static final short LOAD_BALANCING_OBJECT_FLAGS_START_BIT = 16;
	public static final short LOAD_BALANCING_OBJECT_FLAGS_END_BIT = 23;

	public static final short LOAD_BALANCING_OBJECT_MAX_LSP_LENGTH = 8;
	public static final short LOAD_BALANCING_OBJECT_MAX_LSP_START_BIT = 24;
	public static final short LOAD_BALANCING_OBJECT_MAX_LSP_END_BIT = 31;

	public static final short LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_LENGTH = 32;
	public static final short LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_START_BIT = 32;
	public static final short LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_END_BIT = 63;

	/**
	 * Reported Route Object 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type
	 * | Length | Flags | Reserved (MBZ)|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Router ID |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Interface ID (32 bits) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */
	public static final short REPORTED_ROUTE_OBJECT_LENGTH = 96;

	public static final short REPORTED_ROUTE_OBJECT_TYPE_LENGTH = 8;
	public static final short REPORTED_ROUTE_OBJECT_TYPE_START_BIT = 0;
	public static final short REPORTED_ROUTE_OBJECT_TYPE_END_BIT = 7;

	public static final short REPORTED_ROUTE_OBJECT_LENGTH_LENGTH = 8;
	public static final short REPORTED_ROUTE_OBJECT_LENGTH_START_BIT = 8;
	public static final short REPORTED_ROUTE_OBJECT_LENGTH_END_BIT = 15;

	public static final short REPORTED_ROUTE_OBJECT_FLAGS_LENGTH = 8;
	public static final short REPORTED_ROUTE_OBJECT_FLAGS_START_BIT = 16;
	public static final short REPORTED_ROUTE_OBJECT_FLAGS_END_BIT = 23;

	public static final short REPORTED_ROUTE_OBJECT_RESERVED_LENGTH = 8;
	public static final short REPORTED_ROUTE_OBJECT_RESERVED_START_BIT = 24;
	public static final short REPORTED_ROUTE_OBJECT_RESERVED_END_BIT = 31;

	public static final short REPORTED_ROUTE_OBJECT_ROUTER_ID_LENGTH = 32;
	public static final short REPORTED_ROUTE_OBJECT_ROUTER_ID_START_BIT = 32;
	public static final short REPORTED_ROUTE_OBJECT_ROUTER_ID_END_BIT = 63;

	public static final short REPORTED_ROUTE_OBJECT_INTERFACE_ID_LENGTH = 32;
	public static final short REPORTED_ROUTE_OBJECT_INTERFACE_ID_START_BIT = 64;
	public static final short REPORTED_ROUTE_OBJECT_INTERFACE_ID_END_BIT = 95;

	public static final short SVEC_OBJECT_LENGTH = 32;

	public static final short SVEC_OBJECT_RESERVED_LENGTH = 8;
	public static final short SVEC_OBJECT_RESERVED_START_BIT = 0;
	public static final short SVEC_OBJECT_RESERVED_END_BIT = 7;

	public static final short SVEC_OBJECT_FLAGS_LENGTH = 24;
	public static final short SVEC_OBJECT_FLAGS_START_BIT = 8;
	public static final short SVEC_OBJECT_FLAGS_END_BIT = 31;

	public static final short SVEC_OBJECT_FLAG_S_LENGTH = 1;
	public static final short SVEC_OBJECT_FLAG_S_START_BIT = 29;
	public static final short SVEC_OBJECT_FLAG_S_END_BIT = 29;

	public static final short SVEC_OBJECT_FLAG_N_LENGTH = 1;
	public static final short SVEC_OBJECT_FLAG_N_START_BIT = 30;
	public static final short SVEC_OBJECT_FLAG_N_END_BIT = 30;

	public static final short SVEC_OBJECT_FLAG_L_LENGTH = 1;
	public static final short SVEC_OBJECT_FLAG_L_START_BIT = 31;
	public static final short SVEC_OBJECT_FLAG_L_END_BIT = 31;

	/**
	 * Template Values
	 */
	public static final short TEMPLATE_OBJECT_LENGTH = 24;

	public static final short TEMPLATE_OBJECT_1_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_1_FIELD_C_START_BIT = 0;
	public static final short TEMPLATE_OBJECT_1_FIELD_C_END_BIT = 2;

	public static final short TEMPLATE_OBJECT_2_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_2_FIELD_C_START_BIT = 3;
	public static final short TEMPLATE_OBJECT_2_FIELD_C_END_BIT = 5;

	public static final short TEMPLATE_OBJECT_3_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_3_FIELD_C_START_BIT = 6;
	public static final short TEMPLATE_OBJECT_3_FIELD_C_END_BIT = 8;

	public static final short TEMPLATE_OBJECT_4_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_4_FIELD_C_START_BIT = 9;
	public static final short TEMPLATE_OBJECT_4_FIELD_C_END_BIT = 11;

	public static final short TEMPLATE_OBJECT_5_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_5_FIELD_C_START_BIT = 12;
	public static final short TEMPLATE_OBJECT_5_FIELD_C_END_BIT = 14;

	public static final short TEMPLATE_OBJECT_6_FIELD_C_LENGTH = 3;
	public static final short TEMPLATE_OBJECT_6_FIELD_C_START_BIT = 15;
	public static final short TEMPLATE_OBJECT_6_FIELD_C_END_BIT = 17;

	public static final short TEMPLATE_OBJECT_FLAGS_LENGTH = 6;
	public static final short TEMPLATE_OBJECT_FLAGS_START_BIT = 18;
	public static final short TEMPLATE_OBJECT_FLAGS_END_BIT = 18;

	public static final short TEMPLATE_OBJECT_FLAG_1_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_1_FLAG_C_START_BIT = 18;
	public static final short TEMPLATE_OBJECT_FLAG_1_FLAG_C_END_BIT = 18;

	public static final short TEMPLATE_OBJECT_FLAG_2_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_2_FLAG_C_START_BIT = 19;
	public static final short TEMPLATE_OBJECT_FLAG_2_FLAG_C_END_BIT = 19;

	public static final short TEMPLATE_OBJECT_FLAG_3_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_3_FLAG_C_START_BIT = 20;
	public static final short TEMPLATE_OBJECT_FLAG_3_FLAG_C_END_BIT = 20;

	public static final short TEMPLATE_OBJECT_FLAG_4_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_4_FLAG_C_START_BIT = 21;
	public static final short TEMPLATE_OBJECT_FLAG_4_FLAG_C_END_BIT = 21;

	public static final short TEMPLATE_OBJECT_FLAG_5_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_5_FLAG_C_START_BIT = 22;
	public static final short TEMPLATE_OBJECT_FLAG_5_FLAG_C_END_BIT = 22;

	public static final short TEMPLATE_OBJECT_FLAG_6_FLAG_C_LENGTH = 1;
	public static final short TEMPLATE_OBJECT_FLAG_6_FLAG_C_START_BIT = 23;
	public static final short TEMPLATE_OBJECT_FLAG_6_FLAG_C_END_BIT = 23;

	
	public static final short OF_OBJECT_OFCODE_START_BIT=0;
	public static final short OF_OBJECT_OFCODE_END_BIT=15;
	public static final short OF_OBJECT_OFCODE_LENGTH=16;
	
	public static final short OF_OBJECT_RESERVED_START_BIT=16;
	public static final short OF_OBJECT_RESERVED_END_BIT=31;
	public static final short OF_OBJECT_RESERVED_LENGTH=16;
}
