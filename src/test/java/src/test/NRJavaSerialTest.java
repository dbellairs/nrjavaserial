package test;

import gnu.io.CommPortIdentifier;
import gnu.io.NRSerialPort;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum NRJavaSerialTest {
    ;

    private static final String TTY_SERIAL_PORT_NAME = "/dev/tty.usbserial-A50285BI";
    public static final int BAUD = 115200;


    private static boolean hasPort(final String portName) {
        final List<CommPortIdentifier> portIdentifiers = Collections.list(CommPortIdentifier.getPortIdentifiers());

        return portIdentifiers.stream()
                              .anyMatch(portIdentifier -> Objects.equals(portIdentifier.getName(), portName));

    }

    /**
     * @param args
     */
    public static void main(final String... args) {
        System.out.println("Starting Test..");

        if (hasPort(TTY_SERIAL_PORT_NAME)) {
            doSendAndReceive();
        }

    }

    private static void doSendAndReceive() {
        final NRSerialPort port = new NRSerialPort(TTY_SERIAL_PORT_NAME, BAUD);

        if (port.connect()) {
            try (DataInputStream inputStream = new DataInputStream(port.getInputStream());
                 DataOutputStream outputStream = new DataOutputStream(port.getOutputStream())) {

                for (short txByte = 0x41; txByte <= 0x5A; txByte++) {
                    outputStream.write(new byte[] {(byte) txByte});

                    outputStream.flush();
                    final byte rxByte = inputStream.readByte();

                    System.out.printf("TX: 0x%02X RX: 0x%02X\n", txByte, rxByte);
                    assert rxByte == txByte;

                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n");

        port.disconnect();
    }

}
