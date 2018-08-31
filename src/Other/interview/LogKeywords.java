package Other.interview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LogKeywords {
    public static void main(String[] args) throws IOException {
        File logFile = new File("/Users/tianyiwang/Downloads/JavaTest/resource/sys_info.log");
        BufferedReader logFileReader = new BufferedReader(new FileReader(logFile));
        System.out.println("请输入关键字：（以空格为间隔，并且关键词不能包含`符号）");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] keywords = input.split(" ");

        Map<String, Integer> keywordToCountMap = new HashMap<>();

        String line;
        while ((line = logFileReader.readLine()) != null) {
            for (String keyword : keywords) {
                String[] split = line.split(keyword);
                System.out.println(Arrays.toString(split));
                int count = split.length - 1;
                // 如果有关键字在一行字符串的最后，那么split方法将少返回一个数组
                if (line.endsWith(keyword)) {
                    count++;
                }
                if (keywordToCountMap.containsKey(keyword)) {
                    keywordToCountMap.put(keyword, keywordToCountMap.get(keyword) + count);
                } else {
                    keywordToCountMap.put(keyword, count);
                }
            }
        }

        if (!keywordToCountMap.isEmpty()) {
            System.out.println(keywordToCountMap
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        } else {
            System.out.println("无匹配");
        }
    }
}
