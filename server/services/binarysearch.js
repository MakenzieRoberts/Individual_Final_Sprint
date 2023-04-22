class Node {
	constructor(data) {
		this.data = data;
		this.left = null;
		this.right = null;
	}
}

class BinarySearchTree {
	constructor() {
		this.root = null;
	}

	insertNode(data) {
		const newNode = new Node(data);

		if (this.root === null) {
			this.root = newNode;
		} else {
			let cur = this.root;

			while (true) {
				if (data < cur.data) {
					if (cur.left === null) {
						cur.left = newNode;
						break;
					}
					cur = cur.left;
				} else {
					if (cur.right === null) {
						cur.right = newNode;
						break;
					}
					cur = cur.right;
				}
			}
		}
	}
}

module.exports = {
	BinarySearchTree,
};
