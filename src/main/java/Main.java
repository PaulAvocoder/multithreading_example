import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static List<String> strings = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args)  {
        ExecutorService es = Executors.newFixedThreadPool(2);
        System.out.println("Зaпycк потоков");

        es.execute(new PreparWork());
        es.execute(new Writing());

        es.shutdown();

    }

    static class PreparWork extends Thread {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                synchronized (strings) {
                    strings.add(scanner.nextLine());
                    strings.notify(); //сообщаем, что завершили добавление элемента с клавиатуры
                    System.out.println("Это работает поток: " +Thread.currentThread().getName());
                }
                try {
                    Thread.sleep(600);
                    System.out.println("Приготовитель спал, теперь доступен");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Writing extends Thread {
        @Override
        public void run() {

            while (strings.isEmpty()) {
                synchronized (strings) {
                    System.out.println("Это трудится поток: " +Thread.currentThread().getName());
                    try {
                        strings.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(strings.remove(0));
                }
            }
        }

    }
}