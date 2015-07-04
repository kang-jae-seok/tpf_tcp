package tpf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.impl.PinImpl; //this!!!!

/**
 * Created by kang on 2015-07-04.
 */
public class TpfThread implements Runnable {
    private static final int sizeBuf = 50;
    private Socket clientSock;
    private Logger logger;
    private SocketAddress clientAddress;

    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput pinlpwm = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "lpwm", PinState.LOW);

    public TpfThread(Socket clntSock, SocketAddress clientAddress, Logger logger) {
        this.clientSock = clntSock;
        this.logger = logger;
        this.clientAddress = clientAddress;
    }

    public void run(){
        try {
            InputStream ins = clientSock.getInputStream();
            OutputStream outs = clientSock.getOutputStream();

            int rcvBufSize;
            byte[] rcvBuf = new byte[sizeBuf];
            while ((rcvBufSize = ins.read(rcvBuf)) != -1) {

                String rcvData = new String(rcvBuf, 0, rcvBufSize, "UTF-8");

                if (rcvData.compareTo("Led_on") == 0) {
                    pinlpwm.high();
                    System.out.println("Led_on!");
                }
                if (rcvData.compareTo("Led_off") == 0) {
                    pinlpwm.low();
                    System.out.println("Led_off!");
                }

                logger.info("Received data : " + rcvData + " (" + clientAddress + ")");
                outs.write(rcvBuf, 0, rcvBufSize);
            }
        }catch (IOException ex) {
            logger.log(Level.WARNING, "Exception in RcvThread", ex);
        } finally {
            try {
                clientSock.close();
                System.out.println("Disconnected! Client IP : " + clientAddress); }
            catch (IOException e) {}
        }
    }
}
