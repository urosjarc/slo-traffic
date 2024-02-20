package com.urosjarc.slotraffic.exceptions

class ServiceException(msg: String, cause: Throwable? = null): Throwable(message = msg, cause = cause)
