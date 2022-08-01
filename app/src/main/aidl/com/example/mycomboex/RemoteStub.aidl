// RemoteStub.aidl
package com.example.mycomboex;

// Declare any non-default types here with import statements

interface RemoteStub {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

            // Client에서 원격 서비스에 접속을 한 후 사용할 것
            String getServicePackageName();
}