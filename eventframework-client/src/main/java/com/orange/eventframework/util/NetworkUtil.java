package com.orange.eventframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author 小天
 * @date 2019/3/29 16:42
 */
public class NetworkUtil {

    private static Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    /**
     * 获取本机第一块网卡IP
     */
    public static String getLocalIP() {
        String ip = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                        if (ip.startsWith("127")) {
                            continue;
                        }
                        if (ip.equals("0.0.0.0")) {
                            continue;
                        }
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("获取本机IP时出现异常", e);
        }
        return ip;
    }
}
