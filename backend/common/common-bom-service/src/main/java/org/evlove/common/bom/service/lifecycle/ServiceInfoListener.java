package org.evlove.common.bom.service.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Network information listener to obtain the IP and port bound by the service
 *
 * @author massaton.github.io
 */
@Slf4j
@Component
public class ServiceInfoListener implements ApplicationListener<WebServerInitializedEvent> {

    /**
     * The IP address bound to the current service
     */
    public static String IP;
    /**
     * The port exposed by the current service
     */
    public static Integer PORT;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        log.debug("WebServerInitializedEvent...");

        // Get the IP address used by the current service
        try {
            IP = getIpByNetworkCard();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // Get the port number used by the current service
        PORT = event.getWebServer().getPort();

        log.info("Record the network information of the current service - IP:{} PORT:{}", IP, PORT);
    }

    /**
     * Obtain the IP address according to the network card
     */
    private String getIpByNetworkCard() throws SocketException {
        String ip = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface networkInterface = en.nextElement();
            String networkInterfaceName = networkInterface.getName();

            if (networkInterfaceName.toLowerCase().contains("docker")
                    || networkInterfaceName.toLowerCase().contains("lo")) {
                continue;
            }

            Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
            do {
                InetAddress inetAddress = enumIpAddr.nextElement();
                String ipAddress = inetAddress.getHostAddress();

                if (inetAddress.isLoopbackAddress()
                        || ipAddress.contains("::") || ipAddress.contains("0:0:") || ipAddress.contains("fe80")
                        || "127.0.0.1".equals(ip)) {
                    continue;
                }
                ip = ipAddress;
            } while (enumIpAddr.hasMoreElements());
        }
        return ip;
    }
}
