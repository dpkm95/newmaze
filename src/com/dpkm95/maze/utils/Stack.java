package com.dpkm95.maze.utils;

//Stack storing the path coordinates
public class Stack {
	private Node head;
	private Node tail;
	private int size;

	public Stack() {
		head = null;
		tail = null;
		size = 0;
	}

	public Node top() {
		return head;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public int getSize() {
		return size;
	}

	public void push(int i, int j) {
		if (head == null)
			head = new Node(i, j, tail);
		else
			head = new Node(i, j, head);
		size++;
	}

	public int[] pop() {
		size--;
		int[] res = { head.getX(), head.getY() };
		head = head.getNext();
		return res;
	}

	public int topX() {
		return head.getX();
	}

	public int topY() {
		return head.getY();
	}

	public void insert(int i, int j) {
		if (size == 0) {
			head = new Node(i, j, null);
			tail = head;
		} else {
			tail.setNext(new Node(i, j, null));
			tail = tail.getNext();
		}
		size++;
	}

	public void copy(Stack s1) {
		Node p = s1.head;
		while (p != null) {
			this.insert(p.getX(), p.getY());
			p = p.getNext();
		}
	}

	public void removeLastNode() {
		size--;
		Node p = this.head;
		if (p.getNext() == null)
			head = null;
		else {
			while (p.getNext().getNext() != null)
				p = p.getNext();
			p.setNext(null);
		}
	}
	
	public void dec_size(){
		size--;
	}
}


