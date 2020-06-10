package com.gwinilts.cpptestagain;

import android.content.res.AssetManager;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;

public class NativeThread implements Runnable {
    static {
        System.loadLibrary("native-lib");
    }

    private native int init(AssetManager mgr, byte[] bcast);
    private AssetManager mgr;

    public NativeThread(AssetManager mgr) {
        super();
        this.mgr = mgr;
    }

    @Override
    public void run() {
        System.out.println("in own thread i think");

        InetAddress addr = getBcast();
        String s = addr.getHostAddress();

        System.out.println(s);

        init(this.mgr, s.getBytes());

        System.out.println("thread left");
    }

    private InetAddress getBcast() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface i;
            Iterator addr;
            InterfaceAddress a;
            InetAddress bcast = null;

      /*
      We need to find a valid IPv4 bcast address to look for clients with.
      We won't accept interfaces that aren't up or are loopback interfaces.
      We will stupidly accept the valid bcast address we can find.
      */

            boolean valid;

            while (ifaces.hasMoreElements()) {
                i = ifaces.nextElement();
                System.out.println("Found Interface: " + i.getDisplayName());

                valid = true;

                if (i.isLoopback()) {
                    System.out.println("Is a loopback, ignoring.");
                    valid = false;
                }
                if (!i.isUp()) {
                    System.out.println("Is not up, ignoring.");
                    valid = false;
                }

                if (valid) {
                    addr = i.getInterfaceAddresses().iterator();

                    while (addr.hasNext()) {
                        a = (InterfaceAddress) addr.next();

                        if (a == null) {
                            System.out.println("Found null address.");
                            continue;
                        }
                        if ((bcast = a.getBroadcast()) == null) {
                            System.out.println("Found an unusable IPv6 addr.");
                        } else {
                            System.out.println("Found valid IPv4 bcast addr: " + bcast.getHostAddress());
                            return bcast;
                        }
                    }
                }
                if (bcast != null) break;
            }
        } catch (SocketException e) {
            System.out.println("CRASH");
        }

        return null;
    }
}
