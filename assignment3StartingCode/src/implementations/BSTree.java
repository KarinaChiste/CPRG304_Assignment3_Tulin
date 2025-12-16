package implementations;

import utilities.BSTreeADT;
import utilities.Iterator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E>
{
    private BSTreeNode<E> root;
    private int size;
    private BSTreeNode<E> removed;

    public BSTree(E item) {
        size = 1;
        root = new BSTreeNode<E>(item);
    }

    public BSTree() {
        size = 0;
        root = null;
    }

    @Override
    public BSTreeNode<E> getRoot() throws NullPointerException {
        if (root == null) {
            throw new NullPointerException("Tree is empty");
        }
        return root;
    }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(BSTreeNode<E> node) {
        if (node == null) {
            return 0;
        }
        return Math.max(calculateHeight(node.left), calculateHeight(node.right)) + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean contains(E entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException("Entry is null");
        }
        return search(entry) != null;
    }

    @Override
    public BSTreeNode<E> search(E entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException("Entry is null");
        }
        return find(root, entry);
    }

    private BSTreeNode<E> find(BSTreeNode<E> node, E value) {
        if (node == null) {
            return null;
        }

        int cmp = value.compareTo(node.getElement());

        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return find(node.left, value);
        } else {
            return find(node.right, value);
        }
    }

    @Override
    public boolean add(E newEntry) throws NullPointerException {
        if (newEntry == null) {
            throw new NullPointerException("Entry is null");
        }

        if (root == null) {
            root = new BSTreeNode<E>(newEntry);
            size++;
            return true;
        }

        if (insert(root, newEntry)) {
            size++;
            return true;
        }

        return false;
    }

    private boolean insert(BSTreeNode<E> node, E value) {
        int cmp = value.compareTo(node.getElement());

        if (cmp == 0) {
            return false; // duplicate
        } else if (cmp < 0) {
            if (node.left == null) {
                node.left = new BSTreeNode<E>(value);
                return true;
            }
            return insert(node.left, value);
        } else {
            if (node.right == null) {
                node.right = new BSTreeNode<E>(value);
                return true;
            }
            return insert(node.right, value);
        }
    }

    @Override
    public BSTreeNode<E> removeMin() {
        if (root == null) {
            return null;
        }

        removed = null;
        root = removeSmallest(root);
        return removed;
    }

    private BSTreeNode<E> removeSmallest(BSTreeNode<E> node) {
        if (node.left == null) {
            removed = node;
            size--;
            return node.right;
        }

        node.left = removeSmallest(node.left);
        return node;
    }

    @Override
    public BSTreeNode<E> removeMax() {
        if (root == null) {
            return null;
        }

        removed = null;
        root = removeLargest(root);
        return removed;
    }

    private BSTreeNode<E> removeLargest(BSTreeNode<E> node) {
        if (node.right == null) {
            removed = node;
            size--;
            return node.left;
        }

        node.right = removeLargest(node.right);
        return node;
    }

    @Override
    public Iterator<E> inorderIterator() {
        ArrayList<E> iterator = new ArrayList<>();
        inOrder(root, iterator);

        return new Iterator<E>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < iterator.size();
            }

            @Override
            public E next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                return iterator.get(index++);
            }
        };
    }

    private void inOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
        if (node == null) return;
        inOrder(node.left, iterator);
        iterator.add(node.getElement());
        inOrder(node.right, iterator);
    }

    @Override
    public Iterator<E> preorderIterator() {
        ArrayList<E> iterator = new ArrayList<>();
        preOrder(root, iterator);

        return new Iterator<E>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < iterator.size();
            }

            @Override
            public E next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                return iterator.get(index++);
            }
        };
    }

    private void preOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
        if (node == null) return;
        iterator.add(node.getElement());
        preOrder(node.left, iterator);
        preOrder(node.right, iterator);
    }

    @Override
    public Iterator<E> postorderIterator() {
        ArrayList<E> iterator = new ArrayList<>();
        postOrder(root, iterator);

        return new Iterator<E>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < iterator.size();
            }

            @Override
            public E next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                return iterator.get(index++);
            }
        };
    }

    private void postOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
        if (node == null) return;
        postOrder(node.left, iterator);
        postOrder(node.right, iterator);
        iterator.add(node.getElement());
    }
}