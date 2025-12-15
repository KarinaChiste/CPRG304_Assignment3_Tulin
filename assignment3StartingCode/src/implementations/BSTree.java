package implementations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import utilities.BSTreeADT;
import utilities.Iterator;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E>, Iterator, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private BSTreeNode<E> root;
	private int size;
	private BSTreeNode<E> removed;
//	private ArrayList<E> iterator;
	
	public BSTree(E item) {
		size = 1;
		root = new BSTreeNode<E>(item);
	}
	
	public BSTree() {
		size = 0;
		root = null;
	}
	

	@Override
	public BSTreeNode<E> getRoot() throws NullPointerException
	{
		// TODO Auto-generated method stub
		if (root == null) {
			throw new NullPointerException("Tree is empty");
		}
		else {
			return root;
		}
		
	}

	@Override
	public int getHeight()
	{
		// TODO Auto-generated method stub
		
		return calculateHeight(root);
	}
	
	private int calculateHeight(BSTreeNode<E> node) {
		if(node == null) {
			return 0;
		}
		else {
			return Math.max(calculateHeight(node.left), calculateHeight(node.right)) + 1;
		}
		
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		if(size == 0) {
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		root=null;
		size=0;
		
	}

	@Override
	public boolean contains(E entry) throws NullPointerException
	{
		// TODO Auto-generated method stub
		if(entry==null) {
			throw new NullPointerException("Entry is null");
		}
		BSTreeNode<E> node = search(entry);
		if(node==null) {
			return false;
		}
		if(node.getElement() == entry) {
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public BSTreeNode<E> search(E entry) throws NullPointerException
	{
		// TODO Auto-generated method stub
		
		if (entry == null) {
			throw new NullPointerException("Entry is null");
		}
		else {
			return find(root, entry);
		}
		
	}
	
	private BSTreeNode<E> find(BSTreeNode<E> test, E value){
		
		if(test == null) {
			return null;
		}
		if(value.compareTo(test.getElement()) == 0) {
			return test;
		}
		else if(value.compareTo(test.getElement()) < 0) {
			test = find(test.left, value);
		}
		else if(value.compareTo(test.getElement()) > 0) {
			test = find(test.right, value);
		}
		
		return test;
		
	}

	@Override
	public boolean add(E newEntry) throws NullPointerException
	{
		// TODO Auto-generated method stub
		if (newEntry == null) {
			throw new NullPointerException("Entry is null");
		}
		else if(root == null) {
			root = new BSTreeNode<E>(newEntry);
			size ++;
		}
		else
		{
			insert(root, newEntry);
			size++;
		}
		
		
		return true;
	}
	private BSTreeNode<E> insert(BSTreeNode<E> node, E value) {
		if (node == null)
		{
			return new BSTreeNode<E>(value);
		}
		else if (value.compareTo(node.getElement())<0) {
			node.left = insert(node.left, value);
		}
		else if(value.compareTo(node.getElement())>0) {
			node.right = insert(node.right, value);
		}
		return node;
	}

	@Override
	public BSTreeNode<E> removeMin()
	{
		// TODO Auto-generated method stub
		if(root == null) {
			return null;
		}
		else {
			removed = null;
			root = removeSmallest(root);
			return removed;
		}
		
	}
	
	private BSTreeNode<E> removeSmallest(BSTreeNode<E> node)
	{
		if(node.left == null) {
			removed = node;
			node = node.right;
			size --;
			return removed;
		}
		else {
			removeSmallest(node.left);
		}
		return null;
	}

	@Override
	public BSTreeNode<E> removeMax()
	{
		// TODO Auto-generated method stub
		if(root == null) {
			return null;
		}
		else {
			removed = null;
			root = removeLargest(root);
			return removed;
		}
	}
	
	private BSTreeNode<E> removeLargest(BSTreeNode<E> node)
	{
		if(node.right == null) {
			removed = node;
			node = node.left;
			size --;
			return removed;
		}
		else {
			removeLargest(node.right);
		}
		return null;
	}

	@Override
	public Iterator<E> inorderIterator()
	{
		// TODO Auto-generated method stub
		ArrayList<E> iterator = new ArrayList<E>();
		inOrder(root, iterator);
		return new Iterator<E>() {
			private int index = 0;

			@Override
			public boolean hasNext()
			{
				// TODO Auto-generated method stub
				return index < iterator.size();
			}

			@Override
			public E next() throws NoSuchElementException
			{
				// TODO Auto-generated method stub
				 if (!hasNext()) {
		                throw new NoSuchElementException("No next element");
		            }
		            return iterator.get(index++);
			}
		};
	}
	
	private void inOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
		
		if(node ==null) {
			return;
		}
		inOrder(node.left, iterator);
		iterator.add(node.getElement());
		inOrder(node.right, iterator);
		
	}

	@Override
	public Iterator<E> preorderIterator()
	{
		// TODO Auto-generated method stub
		ArrayList<E> iterator = new ArrayList<E>();
		preOrder(root, iterator);
		return new Iterator<E>() {
			private int index = 0;

			@Override
			public boolean hasNext()
			{
				// TODO Auto-generated method stub
				return index < iterator.size();
			}

			@Override
			public E next() throws NoSuchElementException
			{
				// TODO Auto-generated method stub
				 if (!hasNext()) {
		                throw new NoSuchElementException("No next element");
		            }
		            return iterator.get(index++);
			}
		};
	}
	
	private void preOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
		
		if(node ==null) {
			return;
		}
		iterator.add(node.getElement());
		preOrder(node.left, iterator);
		preOrder(node.right, iterator);
	}

	@Override
	public Iterator<E> postorderIterator()
	{
		// TODO Auto-generated method stub
		ArrayList<E> iterator = new ArrayList<E>();
		postOrder(root, iterator);
		return new Iterator<E>() {
			private int index = 0;

			@Override
			public boolean hasNext()
			{
				// TODO Auto-generated method stub
				return index < iterator.size();
			}

			@Override
			public E next() throws NoSuchElementException
			{
				// TODO Auto-generated method stub
				 if (!hasNext()) {
		                throw new NoSuchElementException("No next element");
		            }
		            return iterator.get(index++);
			}
		};
	}
	
	private void postOrder(BSTreeNode<E> node, ArrayList<E> iterator) {
		
		if(node ==null) {
			return;
		}
		
		postOrder(node.left, iterator);
		postOrder(node.right, iterator);
		iterator.add(node.getElement());
	}

	
}
