package com.feiliks.common

class NotFoundException : RuntimeException {

    constructor(name: String) : super(name) {}

    constructor(entityCls: Class<*>, name: String) : super(String.format("%s not found: %s", entityCls.getSimpleName(), name)) {}
}
