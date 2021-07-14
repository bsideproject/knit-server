package com.project.knit.utils;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class StringUtils {
    private static Random random;

    public StringUtils() {
        this.random = new Random();
    }

    public static String decodeString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    public static String getAdjNickname() {
        List<String> adjList = Arrays.asList("주황", "노란", "초록", "파란", "핑크", "퍼플", "네이비", "민트", "얼룩", "반짝이", "무지개", "투명", "하얀", "그레이");
        int randomItem = random.nextInt(adjList.size());

        return adjList.get(randomItem);
    }

    public static String getNounNickname() {
        List<String> nounList = Arrays.asList("모니터", "키보드", "마우스", "책상", "나뭇잎", "텀블러", "충전기", "노트", "메모지", "볼펜", "스탠드");
        int randomItem = random.nextInt(nounList.size());

        return nounList.get(randomItem);
    }
}
