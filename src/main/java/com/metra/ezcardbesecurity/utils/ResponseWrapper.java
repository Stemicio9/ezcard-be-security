package com.metra.ezcardbesecurity.utils;

import com.metra.ezcardbesecurity.exception.BaseCustomException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@Configurable
public class ResponseWrapper implements Serializable {

    private String exception = null;
    private String info = null;
    private Object data = null;
    private Object errorID = 0;

    public static ResponseEntity<ResponseWrapper> format(String info, Callable<?> ret) {

        ResponseWrapper obj = new ResponseWrapper();
        obj.setInfo(info);

        try {

            obj.setData(ret.call());
            return ResponseEntity
                    .ok()
                    .body(obj);

        } catch (BaseCustomException baseCustomException) {

            obj.setException(baseCustomException.getMessage());
            return ResponseEntity
                    .status(baseCustomException.getStatusCode())
                    .body(obj);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(obj);
        }
    }
}
