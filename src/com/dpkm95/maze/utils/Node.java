package com.dpkm95.maze.utils;

//Node of Stack
public class Node {
	Node next;
	int x, y;

	public Node(int i, int j, Node n) {
		x = i;
		y = j;
		next = n;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setData(int i, int j) {
		x = i;
		y = j;
	}

	public void setNext(Node n) {
		next = n;
	}

	public Node getNext() {
		return next;
	}

	public void removeCurrentNode() {
		this.x = this.next.x;
		this.y = this.next.y;
		this.next = this.next.next;
	}
}
