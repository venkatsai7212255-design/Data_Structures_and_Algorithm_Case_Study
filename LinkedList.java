package PROJ1;

import java.io.*;
import java.util.*;

class LLNode {
    String query;
    String file;
    LLNode next;

    public LLNode(String query, String file) {
        this.query = query;
        this.file = file;
    }
}

class LinkedListEngine {
    LLNode head = null;

    public void insert(String query, String file) {
        query = query.toLowerCase();
        if (searchNode(query) != null) {
            searchNode(query).file = file;
            return;
        }
        LLNode newNode = new LLNode(query, file);
        if (head == null) {
            head = newNode;
            return;
        }
        LLNode temp = head;
        while (temp.next != null) temp = temp.next;
        temp.next = newNode;
    }

    public boolean delete(String query) {
        query = query.toLowerCase();
        if (head == null) return false;
        if (head.query.equals(query)) {
            head = head.next;
            return true;
        }
        LLNode prev = head;
        LLNode curr = head.next;
        while (curr != null) {
            if (curr.query.equals(query)) {
                prev.next = curr.next;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    public LLNode searchNode(String query) {
        query = query.toLowerCase();
        LLNode temp = head;
        while (temp != null) {
            if (temp.query.equals(query)) return temp;
            temp = temp.next;
        }
        return null;
    }

    public void printAllQueries() {
        LLNode temp = head;
        int count = 0;
        while (temp != null) {
            count++;
            System.out.println(count + ". " + temp.query + " -> " + temp.file);
            temp = temp.next;
        }
        System.out.println("Total queries: " + count);
    }
}

public class LinkedList {
    private static final Scanner sc = new Scanner(System.in);
    private static final LinkedListEngine engine = new LinkedListEngine();
    private static final String folderPath = "C:/Users/ravim/OneDrive/Desktop/DSA linked lists";

    public static void main(String[] args) {
        ensureFolder();

        System.out.println("\nRe-indexing existing .txt files...");
        reIndexFiles();

        System.out.println("\n--- Linked List Query Engine ---");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add a query");
            System.out.println("2. Delete a query");
            System.out.println("3. Update a query");
            System.out.println("4. Search a query");
            System.out.println("5. List all queries");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input!");
                continue;
            }

            switch (choice) {
                case 1 -> addQuery();
                case 2 -> deleteQuery();
                case 3 -> updateMenu();
                case 4 -> searchQuery();
                case 5 -> engine.printAllQueries();
                case 6 -> {
                    System.out.println("Exiting Linked List Query Engine...");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void ensureFolder() {
        File folder = new File(folderPath);
        if (!folder.exists() && folder.mkdirs())
            System.out.println("Created folder: " + folderPath);
    }

    private static void reIndexFiles() {
        File[] files = new File(folderPath).listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No existing .txt files found.");
            return;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\W+");
                    for (String w : words) {
                        if (!w.isBlank())
                            engine.insert(w.toLowerCase(), file.getName());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading " + file.getName());
            }
        }
        System.out.println("Re-indexed " + files.length + " files successfully.");
    }

    private static void addQuery() {
        System.out.print("Enter query: ");
        String query = sc.nextLine().trim().toLowerCase();
        if (query.isBlank()) {
            System.out.println("Query cannot be empty.");
            return;
        }

        System.out.print("Enter file name (.txt): ");
        String fileName = sc.nextLine().trim();
        if (!fileName.endsWith(".txt")) fileName += ".txt";

        File f = new File(folderPath + File.separator + fileName);

        try {
            if (!f.exists() && f.createNewFile())
                System.out.println("File created: " + fileName);

            long start = System.nanoTime();
            engine.insert(query, fileName);
            long end = System.nanoTime();

            System.out.println("Query added successfully in " + (end - start) + " ns.");
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void deleteQuery() {
        System.out.print("Enter query to delete: ");
        String query = sc.nextLine().trim().toLowerCase();
        LLNode node = engine.searchNode(query);
        if (node == null) {
            System.out.println("Query not found.");
            return;
        }

        System.out.print("Delete associated file (" + node.file + ")? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase();

        long start = System.nanoTime();
        boolean deleted = engine.delete(query);
        long end = System.nanoTime();

        if (deleted) {
            System.out.println("Query deleted in " + (end - start) + " ns.");
            if (ans.equals("y") || ans.equals("yes")) {
                File f = new File(folderPath + File.separator + node.file);
                if (f.exists() && f.delete())
                    System.out.println("File deleted: " + node.file);
            }
        } else System.out.println("Delete failed.");
    }

    private static void updateMenu() {
        System.out.println("\n1. Update query name");
        System.out.println("2. Update file content");
        System.out.print("Enter choice: ");

        int c;
        try {
            c = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid input!");
            return;
        }

        switch (c) {
            case 1 -> updateQueryName();
            case 2 -> updateFileContent();
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void updateQueryName() {
        System.out.print("Enter old query: ");
        String oldQ = sc.nextLine().trim().toLowerCase();
        LLNode node = engine.searchNode(oldQ);
        if (node == null) {
            System.out.println("Query not found.");
            return;
        }

        System.out.print("Enter new query name: ");
        String newQ = sc.nextLine().trim().toLowerCase();

        long start = System.nanoTime();
        node.query = newQ;
        long end = System.nanoTime();

        System.out.println("Query updated successfully in " + (end - start) + " ns.");
    }

    private static void updateFileContent() {
        System.out.print("Enter query: ");
        String query = sc.nextLine().trim().toLowerCase();
        LLNode node = engine.searchNode(query);
        if (node == null) {
            System.out.println("Query not found.");
            return;
        }

        File file = new File(folderPath + File.separator + node.file);
        try {
            if (!file.exists() && file.createNewFile())
                System.out.println("File created: " + node.file);

            System.out.println("Enter new content (type 'END' to finish):");

            long start = System.nanoTime();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                while (true) {
                    String line = sc.nextLine();
                    if (line.equalsIgnoreCase("END")) break;
                    bw.write(line);
                    bw.newLine();
                }
            }
            long end = System.nanoTime();

            System.out.println("File content updated in " + (end - start) + " ns.");
        } catch (IOException e) {
            System.out.println("Error updating file: " + e.getMessage());
        }
    }

    private static void searchQuery() {
        System.out.print("Enter query: ");
        String query = sc.nextLine().trim().toLowerCase();

        long start = System.nanoTime();
        LLNode node = engine.searchNode(query);
        long end = System.nanoTime();

        if (node == null) {
            System.out.println("Query not found! Search completed in " + (end - start) + " ns.");
            return;
        }

        System.out.println("Query found in file: " + node.file + " (Search time: " + (end - start) + " ns.)");

        File f = new File(folderPath + File.separator + node.file);
        if (f.exists()) {
            System.out.println("\n--- Content of " + node.file + " ---");
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                int i = 1;
                while ((line = br.readLine()) != null) {
                    System.out.println("Line " + i + ": " + line);
                    i++;
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        } else {
            System.out.println("Associated file missing.");
        }
    }
}
