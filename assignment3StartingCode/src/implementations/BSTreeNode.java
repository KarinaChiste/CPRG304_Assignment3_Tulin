package implementations;

public class BSTreeNode<E>
{
	E key;
	BSTreeNode<E> left;
	BSTreeNode<E> right;
	
	
	public BSTreeNode(E item) {
		this.key = item;
		this.left = null;
		this.right = null;
	}
	
	public E getElement() {
        return key;
    }

    public void setElement(E element) {
        this.key = element;
    }

    public BSTreeNode<E> getLeft() {
        return left;
    }

    public void setLeft(BSTreeNode<E> left) {
        this.left = left;
    }

    public BSTreeNode<E> getRight() {
        return right;
    }

    public void setRight(BSTreeNode<E> right) {
        this.right = right;
    }
}
