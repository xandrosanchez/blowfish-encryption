import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        byte[] bytes = new byte[150]; // Пример массива байт
        new java.util.Random().nextBytes(bytes); // Заполнение случайными значениями
        byte[][] blocks = splitBytes(bytes); // Вызов метода splitBytes
        System.out.println("Разделенные блоки:");
        for (byte[] block : blocks) {
            for (byte b : block) {
                System.out.printf("%02X ", b); // Вывод в шестнадцатеричном формате
            }
            System.out.println();
        }
    }

    public static byte[] xorBlocks(byte[] block1, byte[] block2) {
        if (block1.length != 32 || block2.length != 32) {
            throw new IllegalArgumentException("Размер блоков должен быть 32 байта.");
        }
        byte[] result = new byte[32]; // Результирующий блок

        for (int i = 0; i < 32; i++) {
            result[i] = (byte) (block1[i] ^ block2[i]); // XOR для каждого байта
        }
        return result;
    }

    public static byte[][] splitBytes(byte[] bytes) {
        int numBlocks = (int) Math.ceil(bytes.length / 64.0); // Количество блоков
        byte[][] blocks = new byte[numBlocks][64]; // Массив блоков

        // Заполнение блоков значениями из массива байт
        for (int i = 0; i < numBlocks; i++) {
            int startIndex = i * 64;
            int endIndex = Math.min(startIndex + 64, bytes.length);
            byte[] block = Arrays.copyOfRange(bytes, startIndex, endIndex);
            if (block.length < 64) {
                // Если длина блока меньше 64, дописываем нули в конец
                byte[] paddedBlock = new byte[64];
                System.arraycopy(block, 0, paddedBlock, 0, block.length);
                block = paddedBlock;
            }
            blocks[i] = block;
        }

        return blocks;
    }
}