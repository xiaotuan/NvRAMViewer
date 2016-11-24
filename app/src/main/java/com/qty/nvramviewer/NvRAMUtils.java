package com.qty.nvramviewer;

import android.os.RemoteException;
import android.os.ServiceManager;

public class NvRAMUtils {
    private static final String TAG = "NvRAMUtils";
    private static final NvRAMAgent mAgent = NvRAMAgent.Stub.asInterface(ServiceManager.getService("NvRAMAgent"));

    public static final int UNIFIED_LID = 59;
    private static final int MAX_INDEX = 2047;

    /**
     * Read the specific index in NvRAM
     * @param index the position that need to be accessed
     * @return the byte written in the index, -1 when read failed
     */
    public synchronized static byte readNV(int index) throws RemoteException {
        if (index < 0 || index > 2047) {
            throw new IndexOutOfBoundsException(index + " is out of bounds, index must more than 0 and less than " + MAX_INDEX + ".");
        }
        byte result = -1;
        byte[] buff = readNV();
        if (buff != null && index < buff.length) {
            result = buff[index];
        }
        return result;
    }
    /**
     * Read the specific range in NvRAM
     * @param index the start index that need to be accessed
     * @param length from the start index, the length of the range that need to be accessed
     * @return the byte array written in the specified range, null when read failed
     */
    public synchronized static byte[] readNV(int index, int length) throws RemoteException {
        if (index < 0 || index > 2047) {
            throw new IndexOutOfBoundsException(index + " is out of bounds, index must more than 0 and less than " + MAX_INDEX + ".");
        }
        byte[] buff = readNV();
        byte[] target = new byte[length];
        for(int i = 0 ; i < length; i++){
            target[i] = buff[i+index];
        }
        return target;
    }

    /**
     * Read all the data in NvRAM
     * @return the byte array(usually with length of 2048) containing all the bytes in NvRAM, null when read failed.
     */
    public synchronized static byte[] readNV() throws RemoteException {
        byte[] buff = null;
        if (mAgent != null) {
            buff = mAgent.readFile(UNIFIED_LID);
        } else {
            Log.e(TAG, "readNV=>can not open NvRAM Agent.");
        }
        return buff;
    }

    /**
     * write a specified byte in a specified position
     * @param index the position that need to be written
     * @param value the value that need to be written
     * @return true when write succeeded, false when write failed
     */
    public synchronized static boolean writeNV(int index, byte value) throws RemoteException {
        if (index < 0 || index > 2047) {
            throw new IndexOutOfBoundsException(index + " is out of bounds, index must more than 0 and less than " + MAX_INDEX + ".");
        }
        boolean result = false;
        byte[] buff = readNV();
        if (buff != null && index < buff.length) {
            buff[index] = value;
            result = writeNV(buff);
        }
        return result;
    }

    /**
     * write a specified byte array into NvRAM, start with the specified index
     * @param index the start position that need to be written
     * @param buff the values that need to be written from the start position
     * @return true when write succeeded, false when write failed
     */
    public synchronized static boolean writeNV(int index, byte[] buff) throws RemoteException {
        if (index + buff.length < 0 || index + buff.length > 2047) {
            throw new IndexOutOfBoundsException(index + " + " + buff.length + " is out of bounds, index must more than 0 and less than " + MAX_INDEX + ".");
        }
        boolean result = false;
        byte[] origin = readNV();
        if (buff != null && origin != null && (index + buff.length) < origin.length) {
            for(int i = 0 ; i < buff.length; i++){
                origin[index+i] = buff[i];
            }
            result = writeNV(origin);
        }
        return result;
    }

    /**
     * overwrite all the data in NvRAM
     * @param buff the byte array(usually with length of 2048) containing all the bytes in NvRAM
     * @return true when write succeeded, false when write failed
     */
    public synchronized static boolean writeNV(byte[] buff) throws RemoteException {
        if (buff.length < 0 || buff.length > 2048) {
            throw new IndexOutOfBoundsException("buff length " + buff.length + " is out of bounds, index must more than 0 and less than " + MAX_INDEX + ".");
        }
        boolean result = false;
        if (buff != null) {
            if (mAgent != null) {
                if (mAgent.writeFile(UNIFIED_LID, buff) > 0) {
                    result = true;
                }
            } else {
                Log.e(TAG, "writeNV=>can not open NvRAM Agent.");
            }
        }
        return result;
    }

    public synchronized static int getNvRAMLength() throws RemoteException {
        byte[] bytes = readNV();
        return (bytes != null ? bytes.length : 0);
    }

}
