import java.util.Arrays;
import java.util.Random;

public class Main {

    static int[] P = {
            0x243f6a88, 0x85a308d3, 0x13198a2e, 0x03707344, 0xa4093822,
            0x299f31d0, 0x082efa98, 0xec4e6c89, 0x452821e6, 0x38d01377,
            0xbe5466cf, 0x34e90c6c, 0xc0ac29b7, 0xc97c50dd, 0x3f84d5b5,
            0xb5470917, 0x9216d5d9, 0x8979fb1b
    };

    static int[][] S;

    public static void main(String[] args) {
        S = generateSBox();
        String hi = "Hello world and M I E T!";
        System.out.println(Arrays.toString(hi.getBytes()));
        long[] encryption = encryption(hi.getBytes());
        System.out.println(Arrays.toString(decoding(encryption)));
    }

    public static long[] divideIntoBlocks(byte[] byteArray) {
        int blockCount = (int) Math.ceil(byteArray.length / 8.0); // Вычисляем количество блоков
        long[] blocks = new long[blockCount];
        int byteIndex = 0;
        for (int i = 0; i < blockCount; i++) {
            long block = 0;
            // Формируем каждый блок из 8 байт (64 бит)
            for (int j = 0; j < 8; j++) {
                if (byteIndex < byteArray.length) {
                    block |= (long) (byteArray[byteIndex] & 0xFF) << (8 * j);
                    byteIndex++;
                } else {
                    // Если закончились байты, дописываем нулями
                    block |= 0L;
                }
            }
            blocks[i] = block;
        }
        return blocks;
    }

    /**
     * переводит лонги в байты
     */
    public static byte[] convertToByteArray(long[] longArray) {
        int byteCount = longArray.length * 8; // Вычисляем количество байт
        byte[] byteArray = new byte[byteCount];
        int byteIndex = 0;
        for (long number : longArray) {
            for (int i = 0; i < 8; i++) {
                byte b = (byte) ((number >> (8 * i)) & 0xFF);
                byteArray[byteIndex] = b;
                byteIndex++;
            }
        }
        return byteArray;
    }

    /**
     * делим лонг на два инта
     */
    public static int[] splitLong(long input) {
        int lowerBits = (int) input; // Нижние 32 бита
        int upperBits = (int) (input >> 32); // Верхние 32 бита
        int[] result = new int[2];
        result[0] = lowerBits;
        result[1] = upperBits;
        return result;
    }

    /**
     * два инта в лонг
     */
    public static long combineInts(int lowerBits, int upperBits) {
        return ((long) upperBits << 32) | (lowerBits & 0xFFFFFFFFL);
    }

    public static int xor32Bits(int number1, int number2) {
        return number1 ^ number2;
    }

    public static int moduloAdd(int number1, int number2) {
        long sum = (long) number1 + number2; // Промежуточная сумма типа long
        int result = (int) (sum % (1L << 32)); // Приведение к типу int и взятие остатка по модулю 2^32

        return result;
    }

    public static long[] encryption(byte[] bytes) {
        long[] arr = divideIntoBlocks(bytes);
        for (int i = 0; i < arr.length; i++) {
            long l = arr[i];
            int[] blocks = splitLong(l);
            int[] result = round(blocks[0], blocks[1], 0);
            arr[i] = combineInts(result[0], result[1]);
        }
        return arr;
    }



    public static byte[] decoding(long[] arr) {
        for (int i = 0; i < arr.length; i++) {
            long l = arr[i];
            int[] blocks = splitLong(l);
            int[] result = backRound(blocks[0], blocks[1], 17);
            arr[i] = combineInts(result[0], result[1]);
        }
        return convertToByteArray(arr);
    }

    public static int[] backRound(int firstBlock, int secondBlock, int count) {
        while (count >= 1 && count <= 15) {
            secondBlock = xor32Bits(secondBlock, P[14]);
            firstBlock = xor32Bits(firstBlock, F(secondBlock));
            count--;
        }
        if (count == 0) {
            firstBlock = xor32Bits(firstBlock, P[0]);
            secondBlock = xor32Bits(F(firstBlock), secondBlock);
        }
        firstBlock = xor32Bits(firstBlock, P[17]);
        secondBlock = xor32Bits(secondBlock, P[16]);
        return new int[]{firstBlock, secondBlock};
    }

    public static int[] round(int firstBlock, int secondBlock, int count) {
        while (count >= 1 && count <= 15) {
            firstBlock = xor32Bits(firstBlock, P[count]);
            secondBlock = xor32Bits(F(firstBlock), secondBlock);
            count++;
        }
        firstBlock = xor32Bits(firstBlock, P[15]);
        secondBlock = xor32Bits(F(firstBlock), secondBlock);
        firstBlock = xor32Bits(firstBlock, P[17]);
        secondBlock = xor32Bits(secondBlock, P[16]);
        return new int[]{firstBlock, secondBlock};
    }

    public static int[] backRound1(int firstBlock, int secondBlock, int count) {
        if (count == 17) {
            firstBlock = xor32Bits(firstBlock, P[17]);
            secondBlock = xor32Bits(secondBlock, P[16]);
            firstBlock = xor32Bits(firstBlock, P[15]);
            secondBlock = xor32Bits(F(firstBlock), secondBlock);
            count = 14;
        }
        if (count < 15 && count != 0) {
            secondBlock = xor32Bits(secondBlock, P[14]);
            firstBlock = xor32Bits(firstBlock, F(secondBlock));
            count = count - 1;
            backRound1(firstBlock, secondBlock, count);
        }
        if (count == 0) {
            firstBlock = xor32Bits(firstBlock, P[0]);
            secondBlock = xor32Bits(F(firstBlock), secondBlock);
        }
        return new int[]{firstBlock, secondBlock};
    }

    public static int[] round1(int firstBlock, int secondBlock, int count) {
        if (count < 15) {
            firstBlock = xor32Bits(firstBlock, P[count]);
            secondBlock = xor32Bits(F(firstBlock), secondBlock);
            count = count + 1;
            round1(secondBlock, firstBlock, count);
        }
        firstBlock = xor32Bits(firstBlock, P[15]);
        secondBlock = xor32Bits(F(firstBlock), secondBlock);
        firstBlock = xor32Bits(firstBlock, P[17]);
        secondBlock = xor32Bits(secondBlock, P[16]);
        return new int[]{firstBlock, secondBlock};
    }

    public static int[][] generateSBox() {
        int[][] S = new int[8][256];
        // Генерация случайных значений для каждого элемента S-блока
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 256; j++) {
                S[i][j] = random.nextInt();
            }
        }
        return S;
    }

    public static int F(int x) {
        int a = (x >> 24) & 0xFF; // Получаем старший байт
        int b = (x >> 16) & 0xFF; // Получаем второй байт
        int c = (x >> 8) & 0xFF;  // Получаем третий байт
        int d = x & 0xFF;         // Получаем младший байт
        int result = (S[0][a] + S[1][b]) ^ S[2][c];
        result = result + S[3][d];

        return result;
    }




}