package com.kienht.janus.client.utils

import kotlin.random.Random

/**
 * @author kienht
 * @since 08/09/2020
 */
internal fun randomTransactionId(length: Int = 16): String {
    val charList =
        "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z"
            .split(",")
    val size = charList.size
    val random = List(length) {
        charList[Random.nextInt(0, Int.MAX_VALUE) % size]
    }
    return random.joinToString(separator = "") + System.currentTimeMillis()
}