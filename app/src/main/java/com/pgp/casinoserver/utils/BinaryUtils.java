package com.pgp.casinoserver.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BinaryUtils {

    @NonNull
    public static String ReadString(@NonNull InputStream stream) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        int in = stream.read();
        if (in == 0 || in == -1){
            return "";
        }
        while(in != -1 && in != 0){
            b.write(((byte)(in & 0xff)));
            in = stream.read();
        }

        return new String(b.toByteArray(), StandardCharsets.UTF_8);
    }

    @NonNull
    public static String ReadString(@NonNull ByteBuffer stream) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        int in = stream.get();
        if (in == 0 || in == -1){
            return "";
        }
        while(in != -1 && in != 0){
            b.write(((byte)(in & 0xff)));
            in = stream.get();
        }

        return new String(b.toByteArray(), StandardCharsets.UTF_8);
    }

    public static void WriteString(@NonNull OutputStream stream, @NonNull String string) throws IOException {
        ByteBuffer b = ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8));
        b.put((byte)0);
        stream.write(b.array(), 0, b.capacity());
    }

    @NonNull
    public static byte[] WriteString(@NonNull String string) {
        byte[] buffer = string.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[buffer.length + 1];
        for (int i = 0; i < result.length - 1; i++){
            result[i] = buffer[i];
        }

        return result;
    }

    @NonNull
    public static byte[] Long2Bytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public static byte[] Int2Bytes(int x)
    {
        byte[] result = new byte[4];
        result[0] = (byte) (x >> 24);
        result[1] = (byte) (x >> 16);
        result[2] = (byte) (x >> 8);
        result[3] = (byte) (x >> 0);
        return result;
    }
    public static int Bytes2Int(@NonNull byte[] b){
        return ((b[0] & 0xFF) << 24) |
                ((b[1] & 0xFF) << 16) |
                ((b[2] & 0xFF) << 8 ) |
                ((b[3] & 0xFF) << 0 );
    }

    // Возвратит int.minvalue, если что-то пошло не так
    public static int ReadInt(@NonNull InputStream in) throws IOException {
        if (in.available() >= 4){
            byte[] buff = new byte[4];
            in.read(buff, 0, 4);
            return Bytes2Int(buff);
        }

        return Integer.MIN_VALUE;
    }

}
