/*
 * VxipmiRunner.java
 * Created on 2011-09-20
 *
 * Copyright (c) Verax Systems 2011.
 * All rights reserved.
 *
 * This software is furnished under a license. Use, duplication,
 * disclosure and all other uses are restricted to the rights
 * specified in the written license agreement.
 */
package Other.xml;


import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControl;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatus;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatusResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.PowerCommand;
import com.veraxsystems.vxipmi.coding.commands.fru.*;
import com.veraxsystems.vxipmi.coding.commands.fru.record.FruRecord;
import com.veraxsystems.vxipmi.coding.commands.fru.record.ProductInfo;
import com.veraxsystems.vxipmi.coding.commands.sdr.*;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.*;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.common.TypeConverter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.*;

public class IPMIUtil {

    public static final int COMMON_CIPHER_SUITES = 2;
    private static final int TIMEOUT = 10000;
    private static final int FRU_READ_PACKET_SIZE = 16;
    private static final int DEFAULT_FRU_ID = 0;
    private static final int MAX_REPO_RECORD_ID = 65535;
    private static final int INITIAL_CHUNK_SIZE = 8;
    private static final int CHUNK_SIZE = 16;
    private static final int HEADER_SIZE = 5;
    private String hostname;
    private String username;
    private String password;
    private int nextRecId;

    public IPMIUtil(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    public static void main(String[] args) {
//        IPMIUtil util = new IPMIUtil("172.30.30.22", "ADMIN", "ADMIN");
        IPMIUtil util = new IPMIUtil("172.30.20.28", "admin", "admin");
        try {
            List<SensorInfo> sensorMap = util.getSensorMap();
            System.out.println(sensorMap);
//            Boolean isPowerOnB = util.isPowerOn();
//            System.out.println(isPowerOnB);
//            if (isPowerOnB != null) {
//                boolean isPowerOn = isPowerOnB;
//            } else {
//                throw new Exception("连不上");
//            }
//            ProductInfo pi = util.getProductInfo();
//            if (pi != null) {
//                System.out.println(pi.getProductSerialNumber() + " " + pi.getProductModelNumber());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getLocalAvailablePort() {
        int port;
        do {
            port = new Random().nextInt(20000) + 10000;
        } while (!isPortAvailable(port));

        return port;
    }

    private static boolean isPortAvailable(final int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            return true;
        } catch (final IOException ignored) {
        }
        return false;
    }

    public void power(PowerCommand powerCommand) {
        IpmiConnector connector = null;
        ConnectionHandle handle = null;
        try {
            connector = new IpmiConnector(getLocalAvailablePort());
            handle = connector.createConnection(InetAddress.getByName(hostname));
            CipherSuite cs = connector.getAvailableCipherSuites(handle).get(COMMON_CIPHER_SUITES);
            connector.getChannelAuthenticationCapabilities(handle, cs, PrivilegeLevel.Administrator);
            connector.openSession(handle, username, password, null);
            connector.sendMessage(handle, new SetSessionPrivilegeLevel(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PrivilegeLevel.Administrator));

            ChassisControl chassisControl = null;
            if (powerCommand.equals(PowerCommand.PowerDown)) {
                chassisControl = new ChassisControl(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.PowerDown);
            } else if (powerCommand.equals(PowerCommand.PowerUp)) {
                chassisControl = new ChassisControl(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.PowerUp);
            } else if (powerCommand.equals(PowerCommand.HardReset)) {
                chassisControl = new ChassisControl(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.HardReset);
            }
            connector.sendMessage(handle, chassisControl);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connector, handle);
        }
    }

    private void close(IpmiConnector connector, ConnectionHandle handle) {
        if (connector != null && handle != null) {
            try {
                connector.closeSession(handle);
                connector.tearDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean isPowerOn() {
        IpmiConnector connector = null;
        ConnectionHandle handle = null;
        try {
            connector = new IpmiConnector(getLocalAvailablePort());
            handle = connector.createConnection(InetAddress.getByName(hostname));
            CipherSuite cs = connector.getAvailableCipherSuites(handle).get(COMMON_CIPHER_SUITES);
            connector.getChannelAuthenticationCapabilities(handle, cs, PrivilegeLevel.Administrator);
            connector.openSession(handle, username, password, null);
            GetChassisStatusResponseData rd = (GetChassisStatusResponseData) connector.sendMessage(handle, new GetChassisStatus(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus));
            if (rd == null) {
                return null;
            }
            return rd.isPowerOn();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(connector, handle);
        }
    }

    private ProductInfo getProductInfo() throws Exception {
        IpmiConnector connector = new IpmiConnector(getLocalAvailablePort());
        ConnectionHandle handle = connector.createConnection(InetAddress.getByName(hostname));
        CipherSuite cs = connector.getAvailableCipherSuites(handle).get(COMMON_CIPHER_SUITES);
        connector.getChannelAuthenticationCapabilities(handle, cs, PrivilegeLevel.User);
        connector.openSession(handle, username, password, null);
        List<ReadFruDataResponseData> fruData = new ArrayList<>();

        GetFruInventoryAreaInfoResponseData info = (GetFruInventoryAreaInfoResponseData) connector.sendMessage(handle, new GetFruInventoryAreaInfo(IpmiVersion.V20, handle.getCipherSuite(), AuthenticationType.RMCPPlus, DEFAULT_FRU_ID));
        int size = info.getFruInventoryAreaSize();
        BaseUnit unit = info.getFruUnit();
        for (int i = 0; i < size; i += FRU_READ_PACKET_SIZE) {
            int cnt = FRU_READ_PACKET_SIZE;
            if (i + cnt > size) {
                cnt = size % FRU_READ_PACKET_SIZE;
            }
            try {
                ReadFruDataResponseData data = (ReadFruDataResponseData) connector.sendMessage(handle, new ReadFruData(IpmiVersion.V20, handle.getCipherSuite(), AuthenticationType.RMCPPlus, DEFAULT_FRU_ID, unit, i, cnt));
                fruData.add(data);
            } catch (Exception e) {
                System.out.println("Error while sending ReadFruData command : " + e.getMessage());
            }
        }

        try {
            List<FruRecord> records = ReadFruData.decodeFruData(fruData);
            for (FruRecord record : records) {
                if (record instanceof ProductInfo) {
                    return (ProductInfo) record;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connector.closeSession(handle);
                connector.tearDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<SensorInfo> getSensorMap() throws Exception {
        Map<String, SensorInfo> nameToSensorInfo = new HashMap<>();

        // ID 0指示SDR中的第一个记录。下一个IDS可以从记录中检索出来——它们被组织在一个列表中，没有BMC命令来获取所有这些ID。
        nextRecId = 0;

        // 一些BMC允许无保留地获取传感器记录，所以我们尝试这样做。
        int reservationId = 0;
        int lastReservationId = -1;

        // 创建连接器
        IpmiConnector connector = new IpmiConnector(getLocalAvailablePort());

        // 启动会话到远程主机
        ConnectionHandle handle = startSession(connector, InetAddress.getByName(hostname), username, password, "", PrivilegeLevel.User);

        // 更改连接超时时间
        connector.setTimeout(handle, TIMEOUT);

        // 我们得到传感器数据，直到我们遇到ID＝65535，这意味着这个记录是最后一个。
        while (nextRecId < MAX_REPO_RECORD_ID) {
            SensorRecord record;

            try {
                // 填充传感器记录并获取存储库中的下一条记录的ID
                record = getSensorData(connector, handle, reservationId);

                int recordReadingId = -1;

                // 判断接收到的数据是全部传感器记录还是压缩的记录(详见IPMI规范)
                if (record instanceof FullSensorRecord) {
                    FullSensorRecord fsr = (FullSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt(fsr.getSensorNumber());
                    String sensorName = fsr.getName();
                    nameToSensorInfo.put(sensorName, new SensorInfo(sensorName, null, null));

                } else if (record instanceof CompactSensorRecord) {
                    CompactSensorRecord csr = (CompactSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt(csr.getSensorNumber());
                    String sensorName = csr.getName();
                    nameToSensorInfo.put(sensorName, new SensorInfo(sensorName, null, null));
                }

                // 如果有记录，我们会得到响应数据
                GetSensorReadingResponseData data2;
                try {
                    if (recordReadingId >= 0) {
                        data2 = (GetSensorReadingResponseData) connector
                                .sendMessage(handle, new GetSensorReading(IpmiVersion.V20, handle.getCipherSuite(),
                                        AuthenticationType.RMCPPlus, recordReadingId));
                        if (record instanceof FullSensorRecord) {
                            FullSensorRecord rec = (FullSensorRecord) record;
                            // 解析传感器读取的记录信息
                            String sensorName = rec.getName();
                            String sensorValue = data2.getSensorReading(rec) + " " + (rec.getSensorBaseUnit().equals(SensorUnit.Unspecified) ? (rec.getRateUnit() == RateUnit.None ? "percent" : "") : rec.getSensorBaseUnit().toString());
                            nameToSensorInfo.put(sensorName, new SensorInfo(sensorName, sensorValue, data2.getSensorState().toString()));
                            System.out.println("完全型传感器：" + sensorName);
                            System.out.println("记录信息:" + sensorValue);
                        }
                        if (record instanceof CompactSensorRecord) {
                            CompactSensorRecord rec = (CompactSensorRecord) record;
                            // 获取传感器状态
                            List<ReadingType> events = data2.getStatesAsserted(rec.getSensorType(),
                                    rec.getEventReadingType());
                            StringBuilder sensorValue = new StringBuilder();
                            for (ReadingType event : events) {
                                sensorValue.append(event).append(", ");
                            }
                            String sensorName = rec.getName();
                            nameToSensorInfo.put(sensorName, new SensorInfo(sensorName, null, sensorValue.toString().length() == 0 ? "Ok" : sensorValue.toString().substring(0, sensorValue.length() - 2)));
                            System.out.println("紧凑型传感器：" + sensorName);
                            System.out.println("记录信息：" + sensorValue);
                        }
                    }
                } catch (IPMIException e) {
                    if (e.getCompletionCode() == CompletionCode.DataNotPresent) {
                        e.printStackTrace();
                    } else {
                        throw e;
                    }
                }
            } catch (IPMIException e) {
                System.out.println("Getting new reservation ID");
                System.out.println("156: " + e.getMessage());
                // 如果获得传感器数据失败，检查预留id是否已经失败了
                if (lastReservationId == reservationId || e.getCompletionCode() != CompletionCode.ReservationCanceled)
                    throw e;
                lastReservationId = reservationId;

                // 如果失败的原因是取消预留，我们得到新的预留ID并重试
                // 在获得所有传感器时，这会发生很多次，因为BMC不能管理并行会话，如果出现新的会话，BMC就不能管理旧的会话。
                reservationId = ((ReserveSdrRepositoryResponseData) connector.sendMessage(handle, new ReserveSdrRepository(IpmiVersion.V20, handle.getCipherSuite(),
                        AuthenticationType.RMCPPlus))).getReservationId();
            }
        }
        connector.closeSession(handle);
        connector.closeConnection(handle);
        connector.tearDown();

        return new ArrayList<>(nameToSensorInfo.values());
    }

    public ConnectionHandle startSession(IpmiConnector connector, InetAddress address, String username, String password, String bmcKey, PrivilegeLevel privilegeLevel) throws Exception {
        ConnectionHandle handle = connector.createConnection(address);
        CipherSuite cs;
        try {
            // 获取远程主机支持的密码套件
            List<CipherSuite> suites = connector.getAvailableCipherSuites(handle);

            cs = suites.get(COMMON_CIPHER_SUITES);
            // 选择密码套件并请求会话特权级别
            connector.getChannelAuthenticationCapabilities(handle, cs, privilegeLevel);
            // 打开会话并验证
            connector.openSession(handle, username, password, bmcKey.getBytes());
        } catch (Exception e) {
            connector.closeConnection(handle);
            connector.tearDown();
            throw e;
        }
        return handle;
    }

    public SensorRecord getSensorData(IpmiConnector connector, ConnectionHandle handle, int reservationId) throws Exception {
        try {
            // BMC功能是有限的，这意味着有时记录大小超过消息的最大大小。因为我们不知道这个记录的大小，所以我们先把整个记录放在第一位。
            GetSdrResponseData data = (GetSdrResponseData) connector.sendMessage(handle, new GetSdr(IpmiVersion.V20, handle.getCipherSuite(), AuthenticationType.RMCPPlus, reservationId, nextRecId));
            // 如果获得完整的记录，我们从接收到的数据创建传感记录
            SensorRecord sensorDataToPopulate = SensorRecord.populateSensorRecord(data.getSensorRecordData());
            // 更新下一个记录的ID
            nextRecId = data.getNextRecordId();
            return sensorDataToPopulate;
        } catch (IPMIException e) {
            // 下面的错误代码意味着记录太大，无法在一个块中发送。这意味着我们需要把数据分割成更小的部分。
            if (e.getCompletionCode() == CompletionCode.CannotRespond
                    || e.getCompletionCode() == CompletionCode.UnspecifiedError) {
                System.out.println("Getting chunks");
                // 首先，我们得到记录的头来找出它的大小。
                GetSdrResponseData data = (GetSdrResponseData) connector.sendMessage(handle, new GetSdr(
                        IpmiVersion.V20, handle.getCipherSuite(), AuthenticationType.RMCPPlus, reservationId,
                        nextRecId, 0, INITIAL_CHUNK_SIZE));
                // 记录的大小是记录的第五字节。它没有考虑页眉的大小，所以我们需要添加它。
                int recSize = TypeConverter.byteToInt(data.getSensorRecordData()[4]) + HEADER_SIZE;
                int read = INITIAL_CHUNK_SIZE;

                byte[] result = new byte[recSize];

                System.arraycopy(data.getSensorRecordData(), 0, result, 0, data.getSensorRecordData().length);

                // 我们得到了剩余的记录块（注意超过记录大小），因为这将导致BMC的错误。
                while (read < recSize) {
                    int bytesToRead = CHUNK_SIZE;
                    if (recSize - read < bytesToRead) {
                        bytesToRead = recSize - read;
                    }
                    GetSdrResponseData part = (GetSdrResponseData) connector.sendMessage(handle, new GetSdr(
                            IpmiVersion.V20, handle.getCipherSuite(), AuthenticationType.RMCPPlus, reservationId,
                            nextRecId, read, bytesToRead));

                    System.arraycopy(part.getSensorRecordData(), 0, result, read, bytesToRead);
                    System.out.println("Received part");
                    read += bytesToRead;
                }

                // 最后，用收集的数据填充传感器记录。
                SensorRecord sensorDataToPopulate = SensorRecord.populateSensorRecord(result);
                // 更新下一个记录的ID
                nextRecId = data.getNextRecordId();
                return sensorDataToPopulate;
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

}
