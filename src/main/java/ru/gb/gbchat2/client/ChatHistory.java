package ru.gb.gbchat2.client;

import ru.gb.gbchat2.Command;

import java.io.*;

public class ChatHistory {

    public void logger(String message) throws IOException {
        if (Command.isCommand(message)) {
            return;
        }
        try (FileWriter writer = new FileWriter("history.txt", true)) {
            writer.write(message + '\n');
        }
    }

    public String getHistory(int rowOffset) throws IOException {
        File file = new File("history.txt");
        int readLines = 0;
        StringBuilder builder = new StringBuilder();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long endPointer = file.length() - 1;
            for (long pointer = endPointer; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c;
                c = (char) randomAccessFile.read();
                if (c == '\n') {
                    readLines++;
                    if (readLines == rowOffset + 1)// все сообщени формата **/n и последняя срока не исключение
                        break;
                }
                builder.append(c);
                endPointer = endPointer - pointer;
            }
        }
        return builder.reverse().toString();
    }

}
