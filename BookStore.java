import java.util.Scanner;

class Volume {
    int identifier;
    String name;
    String writer;
    boolean borrowed;
    String borrower;
    Volume following;
}

class BookStore {
    private Volume start;
    private int countVolumes;
    private Volume[] lendingQueue;
    private int frontQueue, backQueue, capacityQueue;

    private void merge(Volume[] volumes, int left, int mid, int right) {
        int len1 = mid - left + 1;
        int len2 = right - mid;

        Volume[] leftArray = new Volume[len1];
        Volume[] rightArray = new Volume[len2];

        for (int i = 0; i < len1; i++)
            leftArray[i] = volumes[left + i];
        for (int j = 0; j < len2; j++)
            rightArray[j] = volumes[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < len1 && j < len2) {
            if (leftArray[i].identifier <= rightArray[j].identifier) {
                volumes[k] = leftArray[i];
                i++;
            } else {
                volumes[k] = rightArray[j];
                j++;
            }
            k++;
        }

        while (i < len1) {
            volumes[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < len2) {
            volumes[k] = rightArray[j];
            j++;
            k++;
        }
    }

    private void mergeSort(Volume[] volumes, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(volumes, left, mid);
            mergeSort(volumes, mid + 1, right);
            merge(volumes, left, mid, right);
        }
    }

    private Volume binarySearchById(Volume[] volumes, int left, int right, int identifier) {
        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (volumes[mid].identifier == identifier)
                return volumes[mid];

            if (volumes[mid].identifier < identifier)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return null;
    }

    private void displayVolume(Volume volume) {
        System.out.println("ID: " + volume.identifier);
        System.out.println("Title: " + volume.name);
        System.out.println("Author: " + volume.writer);
        System.out.println("Status: " + (volume.borrowed ? "Issued" : "Available"));
        if (volume.borrowed) {
            System.out.println("Issued To: " + volume.borrower);
        }
        System.out.println("-----------------------------");
    }

    public BookStore(int size) {
        start = null;
        countVolumes = 0;
        capacityQueue = size;
        lendingQueue = new Volume[size];
        frontQueue = backQueue = -1;
    }

    public void addVolume() {
        Scanner scanner = new Scanner(System.in);
        Volume newVolume = new Volume();
        System.out.print("Enter book ID: ");
        newVolume.identifier = scanner.nextInt();
        System.out.print("Enter book title: ");
        scanner.nextLine(); // consume newline
        newVolume.name = scanner.nextLine();
        System.out.print("Enter book author: ");
        newVolume.writer = scanner.nextLine();
        newVolume.borrowed = false;
        newVolume.borrower = "";
        newVolume.following = start;
        start = newVolume;
        countVolumes++;
        System.out.println("Book added successfully!");
    }

    public void searchVolumeById() {
        if (countVolumes == 0) {
            System.out.println("No books in the library.");
            return;
        }

        Volume[] volumeArray = new Volume[countVolumes];
        Volume temp = start;
        int index = 0;
        while (temp != null) {
            volumeArray[index++] = temp;
            temp = temp.following;
        }

        mergeSort(volumeArray, 0, countVolumes - 1);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID: ");
        int identifier = scanner.nextInt();
        Volume result = binarySearchById(volumeArray, 0, countVolumes - 1, identifier);
        if (result != null) {
            displayVolume(result);
        } else {
            System.out.println("Book not found.");
        }
    }

    public void searchVolumeByTitle() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book title: ");
        scanner.nextLine(); // consume newline
        String title = scanner.nextLine();
        Volume temp = start;
        while (temp != null) {
            if (temp.name.equals(title)) {
                displayVolume(temp);
                return;
            }
            temp = temp.following;
        }
        System.out.println("Book not found.");
    }

    public void issueVolume() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID: ");
        int identifier = scanner.nextInt();
        System.out.print("Enter student name: ");
        scanner.nextLine(); // consume newline
        String studentName = scanner.nextLine();
        Volume temp = start;
        while (temp != null) {
            if (temp.identifier == identifier) {
                if (!temp.borrowed) {
                    if ((backQueue + 1) % capacityQueue == frontQueue) {
                        System.out.println("Issue queue is full. Cannot issue more books.");
                        return;
                    }
                    if (frontQueue == -1) frontQueue = 0;
                    backQueue = (backQueue + 1) % capacityQueue;
                    lendingQueue[backQueue] = temp;

                    temp.borrowed = true;
                    temp.borrower = studentName;
                    System.out.println("Book issued successfully!");
                } else {
                    System.out.println("Book is already issued.");
                }
                return;
            }
            temp = temp.following;
        }
        System.out.println("Book not found.");
    }

    public void returnVolume() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID: ");
        int identifier = scanner.nextInt();
        Volume temp = start;
        while (temp != null) {
            if (temp.identifier == identifier) {
                if (temp.borrowed) {
                    temp.borrowed = false;
                    temp.borrower = "";
                    System.out.println("Book returned successfully!");
                } else {
                    System.out.println("Book is not issued.");
                }
                return;
            }
            temp = temp.following;
        }
        System.out.println("Book not found.");
    }

    public void listAllVolumes() {
        if (countVolumes == 0) {
            System.out.print("No books in the library.");
            return;
        }

        Volume[] volumeArray = new Volume[countVolumes];
        Volume temp = start;
        int index = 0;
        while (temp != null) {
            volumeArray[index++] = temp;
            temp = temp.following;
        }

        mergeSort(volumeArray, 0, countVolumes - 1);

        for (int i = 0; i < countVolumes; i++) {
            displayVolume(volumeArray[i]);
        }
    }

    public void deleteVolume() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID: ");
        int identifier = scanner.nextInt();
        Volume temp = start;
        Volume previous = null;
        while (temp != null) {
            if (temp.identifier == identifier) {
                if (previous != null) {
                    previous.following = temp.following;
                } else {
                    start = temp.following;
                }
                countVolumes--;
                System.out.println("Book deleted successfully!");
                return;
            }
            previous = temp;
            temp = temp.following;
        }
        System.out.println("Book not found.");
    }

    public static void main(String[] args) {
        BookStore lib = new BookStore(10);  // Initialize library with issue queue size 10
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("*-*-*-*-*-*-*--*- || Welcome to Library Management System || *-*-*-*-*-*-*-*-*");
            System.out.println("1. Add New Book");
            System.out.println("2. Search Book by ID");
            System.out.println("3. Search Book by Title");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. List All Books");
            System.out.println("7. Delete Book");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    lib.addVolume();
                    break;
                case 2:
                    lib.searchVolumeById();
                    break;
                case 3:
                    lib.searchVolumeByTitle();
                    break;
                case 4:
                    lib.issueVolume();
                    break;
                case 5:
                    lib.returnVolume();
                    break;
                case 6:
                    lib.listAllVolumes();
                    break;
                case 7:
                    lib.deleteVolume();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}

