/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package friendlytemplate.list;

/**
 * Sample class definition.
 **/
public class LinkedList {

    private Node head;

    /**
     * Adds a new element to the list.
     *
     * @param element data to add to the list
     **/
    public void add(String element) {
        Node newNode = new Node(element);

        Node it = tail(head);
        if (it == null) {
            head = newNode;
        } else {
            it.next = newNode;
        }
    }

    /**
     * Gets last node in the list.
     *
     * @param head reference to the head node
     * @return reference to the last node in the list
     */
    private static Node tail(Node head) {
        Node it;

        for (it = head; it != null && it.next != null; it = it.next) {}

        return it;
    }

    /**
     * Removes an element from the list.
     *
     * @param element data value to remove
     * @return True if the element existed and was removed, False if not
     */
    public boolean remove(String element) {
        boolean result = false;
        Node previousIt = null;
        Node it = null;
        for (it = head; !result && it != null; previousIt = it, it = it.next) {
            if (0 == element.compareTo(it.data)) {
                result = true;
                unlink(previousIt, it);
                break;
            }
        }

        return result;
    }

    /**
     * removes a node from the list.
     *
     * @param previousIt node before one to remove
     * @param currentIt node after one to remove
     */
    private void unlink(Node previousIt, Node currentIt) {
        if (currentIt == head) {
            head = currentIt.next;
        } else {
            previousIt.next = currentIt.next;
        }
    }

    /**
     * number of elements in the list.
     *
     * @return number of elements in the list
     */
    public int size() {
        int size = 0;

        for (Node it = head; it != null; ++size, it = it.next) {}

        return size;
    }

    /**
     * Gets value from element at given offset.
     *
     * @param index offset of element to get
     * @return value of element at given offset
     */
    public String get(int index) {
        Node it = head;
        while (index > 0 && it != null) {
            it = it.next;
            index--;
        }

        if (it == null) {
            throw new IndexOutOfBoundsException("Index is out of range");
        }

        return it.data;
    }

    private static class Node {
        final String data;
        Node next;

        Node(String data) {
            this.data = data;
        }
    }
}
