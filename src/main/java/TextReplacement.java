import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextReplacement {
    private static final Logger logger = LogManager.getLogger(TextReplacement.class);

    public static void main(String[] args) {
        if(args.length != 3){
            logger.error("Неверно заданы параметры:" + Arrays.toString(args));
        }
        else {
            for (File file : getAllFilesInDirect(new File(args[0]))) {
                replaceTextInFile(file, args[1], args[2]);
            }
        }
    }

    private static List<File> getAllFilesInDirect (File dir){
        List<File> fileList = new ArrayList<>();
        if (dir.listFiles() == null){
            logger.error("Неверно указан каталог: " + dir);
        }
        else {
            for (File entry : dir.listFiles()) {
                if (entry.isDirectory()) {
                    fileList.addAll(getAllFilesInDirect(entry));
                } else {
                    fileList.add(entry);
                }
            }
        }
        return fileList;
    }

    private static void replaceTextInFile(File file, String oldText, String newText){
        logger.info("***********************************************************");
        logger.info("Работа с файлом " + file.getAbsolutePath());
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            if (line != null) {//Проверяем файл на пустоту
                StringBuilder fileText = new StringBuilder();//исходный текст файла
                fileText.append(line);
                while ((line = br.readLine()) != null) {
                    fileText.append(System.lineSeparator());
                    fileText.append(line);
                }
                br.close();
                StringBuilder fileTextNew = new StringBuilder();//текст файла с изменениями
                String[] fileTextSplit = fileText.toString().split(oldText);
                //если файл включает заменяемую подстроку
                int indexChanges = 0;//переменная для определения номера символа, с которого производится изменение
                if (fileTextSplit.length != 1) {
                    //если файл начинается с подстроки для замены
                    if (fileText.toString().startsWith(oldText)) {
                        fileTextNew.append(newText);
                        indexChanges += oldText.length();
                        if (fileTextSplit[1].length() > 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    oldText + fileTextSplit[1].substring(0, 2),
                                    newText + fileTextSplit[1].substring(0, 2)));
                        } else {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    oldText + fileTextSplit[1].substring(0, fileTextSplit[0].length()),
                                    newText + fileTextSplit[1].substring(0, fileTextSplit[0].length())));
                        }
                    }
                    //поиск всех совпадений для замены в файле
                    for (int i = 1; i < fileTextSplit.length; i++) {
                        fileTextNew.append(fileTextSplit[i - 1]);
                        fileTextNew.append(newText);
                        indexChanges += fileTextSplit[i-1].length();
                        if (fileTextSplit[i - 1].length() > 3 && fileTextSplit[i].length() > 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[i - 1].substring(fileTextSplit[i - 1].length() - 2) + oldText + fileTextSplit[i].substring(0, 2),
                                    fileTextSplit[i - 1].substring(fileTextSplit[i - 1].length() - 2) + newText + fileTextSplit[i].substring(0, 2)));
                        } else if (fileTextSplit[i - 1].length() < 3 && fileTextSplit[i].length() < 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[i - 1] + oldText + fileTextSplit[1 + 1],
                                    fileTextSplit[i - 1] + newText + fileTextSplit[i]));
                        } else if (fileTextSplit[i - 1].length() < 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[i - 1] + oldText + fileTextSplit[i].substring(0, 2),
                                    fileTextSplit[i - 1] + newText + fileTextSplit[i].substring(0, 2)));
                        } else if (fileTextSplit[i].length() < 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[i - 1].substring(fileTextSplit[i - 1].length() - 2) + oldText,
                                    fileTextSplit[i - 1].substring(fileTextSplit[i - 1].length() - 2) + newText));
                        }
                        indexChanges += oldText.length();
                    }
                    //добавляем последнюю часть файла
                    fileTextNew.append(fileTextSplit[fileTextSplit.length - 1]);
                    //если файл заканчивается на подстроку для замены - меняем её
                    if (fileText.toString().endsWith(oldText)) {
                        fileTextNew.append(newText);
                        indexChanges += fileTextSplit[fileTextSplit.length - 1].length();
                        if (fileTextSplit[fileTextSplit.length - 1].length() > 3) {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[fileTextSplit.length - 1].substring(fileTextSplit[fileTextSplit.length - 1].length() - 2) + oldText,
                                    fileTextSplit[fileTextSplit.length - 1].substring(fileTextSplit[fileTextSplit.length - 1].length() - 2) + newText));
                        } else {
                            logger.info(String.format("С %d символа произведены следующие изменения: \"%s\" -> \"%s\"",
                                    indexChanges,
                                    fileTextSplit[fileTextSplit.length - 1] + oldText,
                                    fileTextSplit[fileTextSplit.length - 1] + newText));
                        }
                    }
                    //File fileWrite = new File(String.format("C:\\work\\%s.txt", "result_" + file.getName() + new Date().getTime()));
                    //BufferedWriter bw = new BufferedWriter(new FileWriter(fileWrite));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(fileTextNew.toString());
                    bw.close();
                    logger.info("данные в файле изменены");
                }
                else {
                    logger.info("Файл не включает заменяемой подстроки");
                }
            }
            else {//файл пустой
                logger.warn("Файл пуст");
            }
        } catch (IOException ioException){
            logger.error("Невозможно получить доступ к файлу");
        }
    }
}
