package com.bxl.utils;

/**
 * Created by hjliu on 16/3/16.
 */



import scala.Tuple3;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;

import java.io.IOException;
import java.math.BigInteger;
//import java.util.HashMap;
//import java.util.Map;

public class SimHash {

   // val g_mInfo = mutable.HashMap[String, ArrayBuffer[(Int, String, String)]]()
    public static HashMap<String, ArrayBuffer<Tuple3<Object, String, String>>> g_mInfo = new HashMap<>();

    private String[] tokens;

    private BigInteger intSimHash;

    private int hashbits = 64;

    public String strSimHash;

    public SimHash(){}

    public SimHash(String[] tokens) throws IOException {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    //SimHash构造器
    public SimHash(String[] tokens, int hashbits) throws IOException {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

    public BigInteger simHash() throws IOException {
        int[] v = new int[this.hashbits];
        for(String word:tokens) {
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                //位移运算
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                //求按位与，然后求此BigInteger的返回值（1，-1,0）
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                //先做位移运算，再求和
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        return fingerprint;
    }

    //hash算法
    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            //乘以2的7次方
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            //算出2的64次方并减去1
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                //先乘上m,然后与temp返回两个大整数的异或,最后与mask返回两个大整数的按位与的结果
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    //根据(appname或者pkgname)的strHash 算出 distance
    public static int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;

    }

    public static void main(String[] args) throws IOException {

//        SimHash hash1 = new SimHash(Match.treatPackage("com.cocos2dx.yuerlianliankan2").split(""), 64);
//        SimHash hash2 = new SimHash(Match.treatPackage("com.cocostest.coco").split(""), 64);
//        SimHash hash3 = new SimHash(Match.treatPackage("com.cod.castlematching").split(""), 64);
//
//        int dis =  getDistance(hash1.strSimHash, hash2.strSimHash);
//        System.out.println(dis);
//        int dis2 = getDistance(hash1.strSimHash, hash3.strSimHash);
//        System.out.println(dis2);

        //Tuple2<String, Object> metaId = Match.getMetaId(new Tuple4<String, String, String, String>("","","",""), g_mInfo);

        String name = "我爱北京";
        for (String s : name.split("")) {
            System.out.println(s);
        }


    }
}
