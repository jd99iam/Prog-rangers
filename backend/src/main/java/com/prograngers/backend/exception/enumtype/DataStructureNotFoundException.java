package com.prograngers.backend.exception.enumtype;

import static com.prograngers.backend.exception.ErrorCode.DATASTRUCTURE_NOT_EXISTS;

public class DataStructureNotFoundException extends EnumTypeException {

    public DataStructureNotFoundException() {
        super("자료구조 타입을 확인해 주세요", DATASTRUCTURE_NOT_EXISTS);
    }
}
