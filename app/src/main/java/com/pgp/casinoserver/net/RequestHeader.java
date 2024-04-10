package com.pgp.casinoserver.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pgp.casinoserver.utils.BinaryUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    public Map<RequestHeaderValues, Object> Values = new HashMap<RequestHeaderValues, Object>(0);



    @NonNull
    public static RequestHeader Sample(){
        RequestHeader r = new RequestHeader();
        r.Values.put(RequestHeaderValues.REQUEST_TYPE, RequestType.INVALID);
        r.Values.put(RequestHeaderValues.PACKAGE_TYPE, PackageType.INVALID);

        return r;
    }

    public boolean isReady(){
        if (Values.containsKey(RequestHeaderValues.PACKAGE_TYPE) && Values.containsKey(RequestHeaderValues.REQUEST_TYPE)){
            if (Values.get(RequestHeaderValues.PACKAGE_TYPE) != PackageType.INVALID &&
                    Values.get(RequestHeaderValues.REQUEST_TYPE) != RequestType.INVALID){
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static RequestHeader /*Map.Entry<RequestHeader, Integer>*/ parse(@NonNull InputStream input/*, int available*/) throws IOException {
        if (input.available() >= 5){
            RequestHeader header = new RequestHeader();
            byte valuesCount = (byte)input.read();
            //available--;
            for (byte i = 0; i < valuesCount; i++){
                byte pairType = (byte)input.read();
                //available--;

                RequestHeaderValues key = RequestHeaderValues.get(pairType);

                switch (key){
                    case INVALID:
                        header.Values.put(key, null);
                        break;
                    case REQUEST_TYPE:
                        header.Values.put(key, RequestType.get((byte)input.read()));
                        //available--;
                        break;
                    case PACKAGE_TYPE:
                        header.Values.put(key, PackageType.get((byte)input.read()));
                        //available--;
                        break;
                    case PLAYER_ID:
                        header.Values.put(key, BinaryUtils.ReadInt(input));
                        //available-=4;
                        break;
                    case PLAYER_PASSWORD:
                        header.Values.put(key, BinaryUtils.ReadInt(input));
                        //available-=4;
                        break;
                    case REQUEST_CODE:
                        header.Values.put(key, (byte)input.read());
                        //available--;
                        break;
                }

            }

            //return new AbstractMap.SimpleEntry<>(header, available);
            return header;
        }

        return null;
    }

    public byte[] write() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(Values.size());

        for (Map.Entry<RequestHeaderValues, Object> pair : Values.entrySet()){
            out.write(pair.getKey().ordinal());
            Object val = pair.getValue();

            switch (pair.getKey()){
                case REQUEST_TYPE:
                    out.write(((RequestType)val).ordinal());
                    break;
                case PACKAGE_TYPE:
                    out.write(((PackageType)val).ordinal());
                    break;
                case PLAYER_ID:
                    out.write(BinaryUtils.Int2Bytes((int)val));
                    break;
                case PLAYER_PASSWORD:
                    out.write(BinaryUtils.Int2Bytes((int)val));
                    break;
                case REQUEST_CODE:
                    out.write((byte)val);
                    break;
                default:
                    out.write(0);
                    break;
            }
        }

        return out.toByteArray();
    }
}
